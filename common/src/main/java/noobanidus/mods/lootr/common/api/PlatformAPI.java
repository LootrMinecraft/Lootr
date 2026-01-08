package noobanidus.mods.lootr.common.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.adapter.ILootrDataAdapter;
import noobanidus.mods.lootr.common.api.adapter.ILootrItemFrameAdapter;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrCart;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;

/**
 * This contains platform-specific (i.e., NeoForge/Fabric) methods.
 * <br />
 * It is separate from LootrAPI simply just because.
 **/
public class PlatformAPI {
  public static IPlatformAPI INSTANCE;

  public static void performEntityOpen(ILootrEntity entity, ServerPlayer player) {
    INSTANCE.performEntityOpen(entity, player);
  }

  @Deprecated
  public static void performCartOpen(ILootrCart cart, ServerPlayer player) {
    INSTANCE.performCartOpen(cart, player);
  }

  public static void performEntityOpen(ILootrEntity entity) {
    INSTANCE.performEntityOpen(entity);
  }

  @Deprecated
  public static void performCartOpen(ILootrCart cart) {
    INSTANCE.performCartOpen(cart);
  }

  public static void performEntityClose(ILootrEntity entity, ServerPlayer player) {
    INSTANCE.performEntityClose(entity, player);
  }

  @Deprecated
  public static void performCartClose(ILootrCart cart, ServerPlayer player) {
    INSTANCE.performCartClose(cart, player);
  }

  public static void performEntityClose(ILootrEntity entity) {
    INSTANCE.performEntityClose(entity);
  }

  @Deprecated
  public static void performCartClose(ILootrCart cart) {
    INSTANCE.performCartClose(cart);
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

  @Deprecated
  public static void copyEntityData(AbstractMinecartContainer entity1, AbstractMinecartContainer entity2) {
    INSTANCE.copyEntityData(entity1, entity2);
  }

  public static void copyEntityData(ILootrDataAdapter<Entity> adapter, Entity entity1, ILootrEntity entity2) {
    INSTANCE.copyEntityData(adapter, entity1, entity2);
  }

  public static void copyEntityData(ILootrItemFrameAdapter<Entity> adapter, Entity entity1, ILootrEntity entity2) {
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
