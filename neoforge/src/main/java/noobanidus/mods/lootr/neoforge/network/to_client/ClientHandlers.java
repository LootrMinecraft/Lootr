package noobanidus.mods.lootr.neoforge.network.to_client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.config.SyncedConfig;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;
import noobanidus.mods.lootr.common.client.ClientHooks;
import noobanidus.mods.lootr.common.mixin.accessor.AccessorMixinLevelRenderer;


public class ClientHandlers {
  public static void handleCloseCart(int entityId) {
    Level level = Minecraft.getInstance().level;
    if (level == null) {
      LootrAPI.LOG.info("Unable to mark entity with id '" + entityId + "' as closed as level is null.");
      return;
    }
    Entity cart = level.getEntity(entityId);
    if (cart == null) {
      LootrAPI.LOG.info("Unable to mark entity with id '" + entityId + "' as closed as entity is null.");
      return;
    }

    if (LootrAPI.wrapEntity(cart) instanceof ILootrEntity lootrCart) {
      lootrCart.setClientOpened(false);
      return;
    }

    LootrAPI.LOG.info("Unable to mark entity with id '" + entityId + "' as closed as entity is not a Lootr-compatible entity.");
  }

  public static void handleOpenCart(int entityId) {
    Level level = Minecraft.getInstance().level;
    if (level == null) {
      LootrAPI.LOG.info("Unable to mark entity with id '" + entityId + "' as opened as level is null.");
      return;
    }
    Entity cart = level.getEntity(entityId);
    if (cart == null) {
      LootrAPI.LOG.info("Unable to mark entity with id '" + entityId + "' as opened as entity is null.");
      return;
    }

    if (LootrAPI.wrapEntity(cart) instanceof ILootrEntity lootrCart) {
      lootrCart.setClientOpened(true);
      return;
    }

    LootrAPI.LOG.info("Unable to mark entity with id '" + entityId + "' as open as entity is not a Lootr-compatible entity.");
  }

  public static void handleOpenContainer(BlockPos pos) {
    var level = Minecraft.getInstance().level;
    if (level == null) {
      return;
    }
    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (LootrAPI.wrapBlockEntity(blockEntity) instanceof ILootrBlockEntity lootrBlockEntity) {
      lootrBlockEntity.setClientOpened(true);
      lootrBlockEntity.asBlockEntity().requestModelDataUpdate();
    }
    ClientHooks.clearCache(pos);
  }

  public static void handleCloseContainer(BlockPos pos) {
    var level = Minecraft.getInstance().level;
    if (level == null) {
      return;
    }
    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (LootrAPI.wrapBlockEntity(blockEntity) instanceof ILootrBlockEntity lootrBlockEntity) {
      lootrBlockEntity.setClientOpened(false);
      lootrBlockEntity.asBlockEntity().requestModelDataUpdate();
    }
    ClientHooks.clearCache(pos);
  }

  public static void handleRefresh() {
    Player player = Minecraft.getInstance().player;
    if (player != null) {
      ClientHooks.clearCache(player.blockPosition());
    }
  }

  public static void handleAreaSync(PacketAreaEntitySync packetAreaEntitySync) {
    Minecraft mc = Minecraft.getInstance();
    Player player = mc.player;
    if (player == null) {
      return;
    }

    Level level = mc.level;
    ;
    if (level == null) {
      return;
    }

    for (int open : packetAreaEntitySync.opened()) {
      if (level.getEntity(open) instanceof ILootrEntity entity) {
        entity.setClientOpened(true);
      }
    }
    for (int closed : packetAreaEntitySync.closed()) {
      if (level.getEntity(closed) instanceof ILootrEntity entity) {
        entity.setClientOpened(false);
      }
    }

    LevelRenderer lr = mc.levelRenderer;

    for (BlockEntity be : mc.level.getGloballyRenderedBlockEntities()) {
      if (be instanceof ILootrBlockEntity ibe) {
        ibe.setClientOpened(false);
        be.requestModelDataUpdate();
      }
    }

    for (SectionRenderDispatcher.RenderSection section : (((AccessorMixinLevelRenderer) lr).lootr$getVisibleSections())) {
      for (BlockEntity be : section.getSectionMesh().getRenderableBlockEntities()) {
        if (be instanceof ILootrBlockEntity ibe) {
          ibe.setClientOpened(false);
          be.requestModelDataUpdate();
        }
      }
    }
  }

  public static void handleConfigSync(SyncedConfig config) {
    LootrAPI.SYNCED_CONFIG = config;
  }
}

