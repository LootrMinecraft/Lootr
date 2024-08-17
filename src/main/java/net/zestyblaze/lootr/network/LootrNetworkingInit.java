package net.zestyblaze.lootr.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.zestyblaze.lootr.config.ConfigManager;
import net.zestyblaze.lootr.entity.LootrChestMinecartEntity;

public class LootrNetworkingInit {
  public static void registerClientNetwork() {
    ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
      ConfigManager.clientConfigStore = null;
    });

    ClientPlayConnectionEvents.INIT.register((handler, client) -> {
      ConfigManager.clientConfigStore = null;
    });

    ClientPlayNetworking.registerGlobalReceiver(NetworkConstants.CLOSE_CART_CHANNEL, (client, handler, buf, responseSender) -> {
      int entityId = buf.readVarInt();
      client.execute(() -> {
        if (client.player != null && client.player.level() != null) {
          Entity potential = client.player.level().getEntity(entityId);
          if (potential instanceof LootrChestMinecartEntity cart) {
            cart.setClosed();
          }
        }
      });
    });

    ClientPlayNetworking.registerGlobalReceiver(NetworkConstants.OPEN_CART_CHANNEL, (client, handler, buf, responseSender) -> {
      int entityId = buf.readVarInt();
      client.execute(() -> {
        if (client.player != null && client.player.level() != null) {
          Entity potential = client.player.level().getEntity(entityId);
          if (potential instanceof LootrChestMinecartEntity cart) {
            cart.setOpened();
          }
        }
      });
    });

    ClientPlayNetworking.registerGlobalReceiver(NetworkConstants.SYNC_DISABLE_BREAK, (client, handler, buf, responseSender) -> {
      boolean value = buf.readBoolean();
      client.execute(() -> {
        ConfigManager.clientConfigStore = new ConfigManager.ClientConfigStore();
        ConfigManager.clientConfigStore.disableBreak = value;
      });
    });
  }
}
