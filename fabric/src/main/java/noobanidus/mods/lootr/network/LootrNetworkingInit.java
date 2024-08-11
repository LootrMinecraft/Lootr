package noobanidus.mods.lootr.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.client.ClientHooks;
import noobanidus.mods.lootr.network.to_client.PacketCloseCart;
import noobanidus.mods.lootr.network.to_client.PacketCloseContainer;
import noobanidus.mods.lootr.network.to_client.PacketOpenCart;
import noobanidus.mods.lootr.network.to_client.PacketOpenContainer;

public class LootrNetworkingInit {
  public static void registerClientNetwork() {
    ClientPlayNetworking.registerGlobalReceiver(PacketCloseCart.TYPE, (payload, context) -> {
      int entityId = payload.entityId();
      context.client().execute(() -> {
        if (context.client().player != null && context.client().player.level() != null) {
          Entity potential = context.client().player.level().getEntity(entityId);
          if (potential instanceof LootrChestMinecartEntity cart) {
            cart.setClientOpened(false);
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
            cart.setClientOpened(true);
          }
        }
      });
    });

    ClientPlayNetworking.registerGlobalReceiver(PacketOpenContainer.TYPE, (payload, context) -> {
      BlockPos position = payload.blockPos();
      context.client().execute(() -> {
        if (context.client().player != null && context.client().player.level() != null) {
          BlockEntity potential = context.client().player.level().getBlockEntity(position);
          if (potential instanceof ILootrBlockEntity blockEntity) {
            blockEntity.setClientOpened(true);
            ClientHooks.clearCache(position);
          }
        }
      });
    });

    ClientPlayNetworking.registerGlobalReceiver(PacketCloseContainer.TYPE, (payload, context) -> {
      BlockPos position = payload.blockPos();
      context.client().execute(() -> {
        if (context.client().player != null && context.client().player.level() != null) {
          BlockEntity potential = context.client().player.level().getBlockEntity(position);
          if (potential instanceof ILootrBlockEntity blockEntity) {
            blockEntity.setClientOpened(false);
            ClientHooks.clearCache(position);
          }
        }
      });
    });
  }
}
