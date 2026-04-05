package noobanidus.mods.lootr.common.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.interfaces.accessor.ILootrDataAccessor;
import noobanidus.mods.lootr.common.api.interfaces.accessor.ILootrItemFrameAccessor;
import noobanidus.mods.lootr.common.api.data.DataToCopy;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;
import noobanidus.mods.lootr.common.api.interfaces.lootr.IPlatformAPI;

/**
 * This contains platform-specific (i.e., NeoForge/Fabric) methods.
 * <br />
 * It is separate from LootrAPI simply just because.
 **/
public final class PlatformAPI {
  public static IPlatformAPI INSTANCE;

  public static void performEntityOpen(ILootrEntity entity, ServerPlayer player) {
    INSTANCE.performEntityOpen(entity, player);
  }

  public static void performEntityOpen(ILootrEntity entity) {
    INSTANCE.performEntityOpen(entity);
  }

  public static void performEntityClose(ILootrEntity entity, ServerPlayer player) {
    INSTANCE.performEntityClose(entity, player);
  }

  public static void performEntityClose(ILootrEntity entity) {
    INSTANCE.performEntityClose(entity);
  }

  public static void performBlockOpen(ILootrBlockEntity blockEntity, ServerPlayer player) {
    INSTANCE.performBlockOpen(blockEntity, player);
  }

  public static void performBlockOpen(ILootrBlockEntity blockEntity) {
    INSTANCE.performBlockOpen(blockEntity);
  }

  public static void performBlockClose(ILootrBlockEntity blockEntity, ServerPlayer player) {
    INSTANCE.performBlockClose(blockEntity, player);
  }

  public static void performBlockClose(ILootrBlockEntity blockEntity) {
    INSTANCE.performBlockClose(blockEntity);
  }

  public static DataToCopy copySpecificData(BlockEntity oldBlockEntity) {
    return INSTANCE.copySpecificData(oldBlockEntity);
  }

  public static void restoreSpecificData(DataToCopy data, BlockEntity newBlockEntity) {
    INSTANCE.restoreSpecificData(data, newBlockEntity);
  }

  public static void copyEntityData(ILootrDataAccessor<Entity> adapter, Entity entity1, ILootrEntity entity2) {
    INSTANCE.copyEntityData(adapter, entity1, entity2);
  }

  public static void copyEntityData(ILootrItemFrameAccessor<Entity> adapter, Entity entity1, ILootrEntity entity2) {
    INSTANCE.copyEntityData(adapter, entity1, entity2);
  }

  public static void refreshPlayerSection(ServerPlayer player) {
    INSTANCE.refreshPlayerSection(player);
  }

  public static void performPotBreak(ILootrBlockEntity lootrDecoratedPotBlockEntity, ServerPlayer player) {
    INSTANCE.performPotBreak(lootrDecoratedPotBlockEntity, player);
  }

  public static boolean shouldDoInitialSave() {
    return INSTANCE.shouldDoInitialSave();
  }
}
