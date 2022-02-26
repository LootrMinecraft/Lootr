package net.zestyblaze.lootr.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.entity.Entity;
import net.zestyblaze.lootr.entity.LootrChestMinecartEntity;

@Environment(value = EnvType.CLIENT)
public class ClientNetworkHandlers {
  static {
    ClientPlayNetworking.registerGlobalReceiver(NetworkConstants.CLOSE_CART_CHANNEL, (client, handler, buf, responseSender) -> {
      client.execute(() -> {
        int entityId = buf.readVarInt();
        if (client.player != null && client.player.level != null) {
          Entity potential = client.player.level.getEntity(entityId);
          if (potential instanceof LootrChestMinecartEntity cart) {
            cart.setClosed();
          }
        }
      });
    });

    ClientPlayNetworking.registerGlobalReceiver(NetworkConstants.OPEN_CART_CHANNEL, (client, handler, buf, responseSender) -> {
      client.execute(() -> {
        int entityId = buf.readVarInt();
        if (client.player != null && client.player.level != null) {
          Entity potential = client.player.level.getEntity(entityId);
          if (potential instanceof LootrChestMinecartEntity cart) {
            cart.setOpened();
          }
        }
      });
    });
  }

  // TODO: Not sure where this should be called
  public static void load() {
  }
}
