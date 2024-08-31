package noobanidus.mods.lootr.fabric.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.ILootrOptional;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrCart;
import noobanidus.mods.lootr.common.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.fabric.ClientHooks;
import noobanidus.mods.lootr.fabric.network.to_client.PacketCloseCart;
import noobanidus.mods.lootr.fabric.network.to_client.PacketCloseContainer;
import noobanidus.mods.lootr.fabric.network.to_client.PacketOpenCart;
import noobanidus.mods.lootr.fabric.network.to_client.PacketOpenContainer;

public class LootrNetworkingInit {
  public static void registerClientNetwork() {
    ClientPlayNetworking.registerGlobalReceiver(PacketCloseCart.TYPE, (payload, context) -> {
      int entityId = payload.entityId();
      context.client().execute(() -> {
        if (context.client().player != null && context.client().player.level() != null) {
          Entity potential = context.client().player.level().getEntity(entityId);
          if (potential instanceof ILootrCart cart) {
            cart.setClientOpened(false);
          }
          if (potential instanceof ILootrOptional optional) {
            Object object = optional.getLootrObject();
            if (object instanceof ILootrCart cart2) {
              cart2.setClientOpened(false);
            }
          }
        }
      });
    });

    ClientPlayNetworking.registerGlobalReceiver(PacketOpenCart.TYPE, (payload, context) -> {
      int entityId = payload.entityId();
      context.client().execute(() -> {
        if (context.client().player != null && context.client().player.level() != null) {
          Entity potential = context.client().player.level().getEntity(entityId);
          if (potential instanceof ILootrCart cart) {
            cart.setClientOpened(true);
          }
          if (potential instanceof ILootrOptional optional) {
            Object object = optional.getLootrObject();
            if (object instanceof ILootrCart cart2) {
              cart2.setClientOpened(true);
            }
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
          if (potential instanceof ILootrOptional optional) {
            Object object = optional.getLootrObject();
            if (object instanceof ILootrBlockEntity blockEntity2) {
              blockEntity2.setClientOpened(true);
              ClientHooks.clearCache(position);
            }
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
          if (potential instanceof ILootrOptional optional) {
            Object object = optional.getLootrObject();
            if (object instanceof ILootrBlockEntity blockEntity2) {
              blockEntity2.setClientOpened(false);
              ClientHooks.clearCache(position);
            }
          }
        }
      });
    });
  }
}
