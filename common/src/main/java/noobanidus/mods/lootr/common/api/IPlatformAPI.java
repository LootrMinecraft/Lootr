package noobanidus.mods.lootr.common.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.adapter.ILootrDataAdapter;
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
    if (entity1.getLootTable() != null) {
      entity2.setLootTable(entity1.getLootTable(), entity1.getLootTableSeed());
    }
  }

  default void copyEntityData (ILootrDataAdapter<Entity> adapter, Entity entity1, Entity entity2) {
    entity2.setXRot(entity1.getXRot());
    entity2.setYRot(entity1.getYRot());
    entity2.setYHeadRot(entity1.getYHeadRot());
    ResourceKey<LootTable> lootTable = adapter.getLootTable(entity1);
    long seed = adapter.getLootSeed(entity1);
    if (lootTable != null) {
      adapter.setLootTable(entity2, lootTable, seed);
    }
  }

  void refreshPlayerSection (ServerPlayer player);
}
