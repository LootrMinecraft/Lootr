package noobanidus.mods.lootr.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.entity.Entity;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.network.to_client.PacketCloseCart;
import noobanidus.mods.lootr.network.to_client.PacketOpenCart;

public class LootrNetworkingInit {
  public static void registerClientNetwork() {
    ClientPlayNetworking.registerGlobalReceiver(PacketCloseCart.TYPE, (payload, context) -> {
      int entityId = payload.entityId();
      context.client().execute(() -> {
        if (context.client().player != null && context.client().player.level() != null) {
          Entity potential = context.client().player.level().getEntity(entityId);
          if (potential instanceof LootrChestMinecartEntity cart) {
            cart.setClosed();
          }
        }
      });
    });

    ClientPlayNetworking.registerGlobalReceiver(PacketOpenCart.TYPE, (payload, context) -> {
      int entityId = payload.entityId();
      context.client().execute(() -> {
        if (context.client().player != null && context.client().player.level() != null) {
          Entity potential = context.client().player.level().getEntity(entityId);
          if (potential instanceof LootrChestMinecartEntity cart) {
            cart.setOpened();
          }
        }
      });
    });
  }
}
