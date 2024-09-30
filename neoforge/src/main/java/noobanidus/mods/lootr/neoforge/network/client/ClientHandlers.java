package noobanidus.mods.lootr.neoforge.network.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrCart;
import org.jetbrains.annotations.Nullable;


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

    if (cart instanceof ILootrCart lootrCart) {
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

    if (cart instanceof ILootrCart lootrCart) {
      lootrCart.setClientOpened(true);
      return;
    }

    LootrAPI.LOG.info("Unable to mark entity with id '" + entityId + "' as open as entity is not a Lootr-compatible entity.");
  }

  public static void handleOpenContainer(BlockPos pos) {
    BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
    if (LootrAPI.resolveBlockEntity(blockEntity) instanceof ILootrBlockEntity lootrBlockEntity) {
      lootrBlockEntity.setClientOpened(true);
      lootrBlockEntity.asBlockEntity().requestModelDataUpdate();
    }
    refreshModel(pos);
  }

  public static void handleCloseContainer(BlockPos pos) {
    BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
    if (LootrAPI.resolveBlockEntity(blockEntity) instanceof ILootrBlockEntity lootrBlockEntity) {
      lootrBlockEntity.setClientOpened(false);
      lootrBlockEntity.asBlockEntity().requestModelDataUpdate();
    }
    refreshModel(pos);
  }

  public static void refreshModel(BlockPos pos) {
    SectionPos sPos = SectionPos.of(pos);
    Minecraft.getInstance().levelRenderer.setSectionDirty(sPos.x(), sPos.y(), sPos.z());
  }

  @Nullable
  public static Player getPlayer() {
    return Minecraft.getInstance().player;
  }
}
