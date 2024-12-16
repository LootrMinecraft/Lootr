package noobanidus.mods.lootr.fabric.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrCart;
import noobanidus.mods.lootr.common.client.ClientHooks;
import noobanidus.mods.lootr.fabric.network.to_client.*;

public class LootrNetworkingInit {
  public static void registerClientNetwork() {
    ClientPlayNetworking.registerGlobalReceiver(PacketCloseCart.TYPE, (payload, context) -> {
      int entityId = payload.entityId();
      context.client().execute(() -> {
        if (context.client().player != null && context.client().player.level() != null) {
          Entity potential = context.client().player.level().getEntity(entityId);
          if (LootrAPI.resolveEntity(potential) instanceof ILootrCart cart) {
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
          if (LootrAPI.resolveEntity(potential) instanceof ILootrCart cart) {
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
          if (LootrAPI.resolveBlockEntity(potential) instanceof ILootrBlockEntity blockEntity) {
            blockEntity.setClientOpened(true);
            ClientHooks.clearCache(position);
          }
        }
      });
    });

    ClientPlayNetworking.registerGlobalReceiver(PacketRefreshSection.TYPE, (payload, context) -> {
      context.client().execute(() -> {
        if (context.client().player != null && context.client().player.level() != null) {
          BlockPos position = context.client().player.blockPosition();
          ClientHooks.clearCache(position);
        }
      });
    });

    ClientPlayNetworking.registerGlobalReceiver(PacketCloseContainer.TYPE, (payload, context) -> {
      BlockPos position = payload.blockPos();
      context.client().execute(() -> {
        if (context.client().player != null && context.client().player.level() != null) {
          BlockEntity potential = context.client().player.level().getBlockEntity(position);
          if (LootrAPI.resolveBlockEntity(potential) instanceof ILootrBlockEntity blockEntity) {
            blockEntity.setClientOpened(false);
            ClientHooks.clearCache(position);
          }
        }
      });
    });
  }
}
