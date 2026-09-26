package noobanidus.mods.lootr.fabric.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;
import noobanidus.mods.lootr.common.client.ClientHooks;
import noobanidus.mods.lootr.common.mixin.accessor.AccessorMixinLevelRenderer;
import noobanidus.mods.lootr.fabric.network.to_client.*;

public class LootrClientNetworkingInit {
  public static void register() {
    ClientPlayNetworking.registerGlobalReceiver(PacketContainerStatus.TYPE,
        (payload, context) -> {
          context.client().execute(() -> {
            ClientHooks.handleContainerStatus(payload.status(), payload.statusType(), payload.remaining());
          });
        });

    ClientPlayNetworking.registerGlobalReceiver(PacketSyncConfig.TYPE,
        (payload, context) -> {
          context.client().execute(() -> {
            LootrAPI.SYNCED_CONFIG = payload.config();
          });
        });

    ClientPlayNetworking.registerGlobalReceiver(PacketAreaEntitySync.TYPE, (payload, context) -> {
      context.client().execute(() -> {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) {
          return;
        }

        Level level = mc.level;

        if (level == null) {
          return;
        }

        for (int open : payload.opened()) {
          if (level.getEntity(open) instanceof ILootrEntity entity) {
            entity.setClientOpened(true);
          }
        }
        for (int closed : payload.closed()) {
          if (level.getEntity(closed) instanceof ILootrEntity entity) {
            entity.setClientOpened(false);
          }
        }

        LevelRenderer lr = mc.levelRenderer;

        for (BlockEntity be : mc.level.getGloballyRenderedBlockEntities()) {
          if (be instanceof ILootrBlockEntity ibe) {
            ibe.setClientOpened(false);
          }
        }

        for (SectionRenderDispatcher.RenderSection section : (((AccessorMixinLevelRenderer) lr).lootr$getVisibleSections())) {
          for (BlockEntity be : section.getSectionMesh().getRenderableBlockEntities()) {
            if (be instanceof ILootrBlockEntity ibe) {
              ibe.setClientOpened(false);
            }
          }
        }
      });
    });

    ClientPlayNetworking.registerGlobalReceiver(PacketCloseCart.TYPE, (payload, context) -> {
      int entityId = payload.entityId();
      context.client().execute(() -> {
        if (context.client().player != null && context.client().player.level() != null) {
          Entity potential = context.client().player.level().getEntity(entityId);
          if (LootrAPI.wrapEntity(potential) instanceof ILootrEntity cart) {
            cart.setClientOpened(false);
          }
        }
      });
    });

    ClientPlayNetworking.registerGlobalReceiver(PacketOpenCart.TYPE, (payload, context) ->

    {
      int entityId = payload.entityId();
      context.client().execute(() -> {
        if (context.client().player != null && context.client().player.level() != null) {
          Entity potential = context.client().player.level().getEntity(entityId);
          if (LootrAPI.wrapEntity(potential) instanceof ILootrEntity cart) {
            cart.setClientOpened(true);
          }
        }
      });
    });

    ClientPlayNetworking.registerGlobalReceiver(PacketOpenContainer.TYPE, (payload, context) ->

    {
      BlockPos position = payload.blockPos();
      context.client().execute(() -> {
        if (context.client().player != null && context.client().player.level() != null) {
          BlockEntity potential = context.client().player.level().getBlockEntity(position);
          if (LootrAPI.wrapBlockEntity(potential) instanceof ILootrBlockEntity blockEntity) {
            blockEntity.setClientOpened(true);
            ClientHooks.clearCache(position);
          }
        }
      });
    });

    ClientPlayNetworking.registerGlobalReceiver(PacketRefreshSection.TYPE, (payload, context) ->

    {
      context.client().execute(() -> {
        if (context.client().player != null && context.client().player.level() != null) {
          BlockPos position = context.client().player.blockPosition();
          ClientHooks.clearCache(position);
        }
      });
    });

    ClientPlayNetworking.registerGlobalReceiver(PacketCloseContainer.TYPE, (payload, context) ->

    {
      BlockPos position = payload.blockPos();
      context.client().execute(() -> {
        if (context.client().player != null && context.client().player.level() != null) {
          BlockEntity potential = context.client().player.level().getBlockEntity(position);
          if (LootrAPI.wrapBlockEntity(potential) instanceof ILootrBlockEntity blockEntity) {
            blockEntity.setClientOpened(false);
            ClientHooks.clearCache(position);
          }
        }
      });
    });

    ClientPlayNetworking.registerGlobalReceiver(PacketPerformBreakEffect.TYPE, ((payload, context) ->

    {
      int entityId = payload.entityId();
      BlockPos pos = payload.pos();
      context.client().execute(() -> {
        ClientHooks.performBreakEffect(entityId, pos);
      });
    }));
  }
}
