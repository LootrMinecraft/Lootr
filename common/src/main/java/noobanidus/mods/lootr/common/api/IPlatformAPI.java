package noobanidus.mods.lootr.common.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrCart;

public interface IPlatformAPI {
  void performCartOpen(ILootrCart cart, ServerPlayer player);

  void performCartOpen(ILootrCart cart);

  void performCartClose(ILootrCart cart, ServerPlayer player);

  void performCartClose(ILootrCart cart);

  void performBlockOpen(ILootrBlockEntity blockEntity, ServerPlayer player);

  void performBlockOpen(ILootrBlockEntity blockEntity);

  void performBlockClose(ILootrBlockEntity blockEntity, ServerPlayer player);

  void performBlockClose(ILootrBlockEntity blockEntity);

  DataToCopy copySpecificData(BlockEntity oldBlockEntity);

  void restoreSpecificData(DataToCopy data, BlockEntity newBlockEntity);

  default void copyEntityData (AbstractMinecartContainer entity1, AbstractMinecartContainer entity2) {
    entity2.setXRot(entity1.getXRot());
    entity2.setYRot(entity1.getYRot());
    entity2.setYHeadRot(entity1.getYHeadRot());
    entity2.setLootTable(entity1.getLootTable(), entity1.getLootTableSeed());
  }
}
