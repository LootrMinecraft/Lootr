package noobanidus.mods.lootr.common.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecartContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.accessor.ILootrDataAccessor;
import noobanidus.mods.lootr.common.api.accessor.ILootrItemFrameAccessor;
import noobanidus.mods.lootr.common.api.data.DataToCopy;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface IPlatformAPI {
  void performEntityOpen(ILootrEntity entity, ServerPlayer player);

  void performEntityOpen(ILootrEntity entity);

  void performEntityClose(ILootrEntity cart, ServerPlayer player);

  void performEntityClose(ILootrEntity entity);

  void performBlockOpen(ILootrBlockEntity blockEntity, ServerPlayer player);

  void performBlockOpen(ILootrBlockEntity blockEntity);

  void performBlockClose(ILootrBlockEntity blockEntity, ServerPlayer player);

  void performBlockClose(ILootrBlockEntity blockEntity);

  DataToCopy copySpecificData(BlockEntity oldBlockEntity);

  void restoreSpecificData(DataToCopy data, BlockEntity newBlockEntity);

  default void copyEntityData(ILootrDataAccessor<Entity> adapter, Entity entity1, ILootrEntity entity3) {
    Entity entity2 = entity3.asEntity();
    entity2.setXRot(entity1.getXRot());
    entity2.setYRot(entity1.getYRot());
    entity2.setYHeadRot(entity1.getYHeadRot());
    ResourceKey<LootTable> lootTable = adapter.getLootTable(entity1);
    long seed = adapter.getLootSeed(entity1);
    if (lootTable != null && entity2 instanceof AbstractMinecartContainer entity4) {
      // TODO: Shim this into ILootrEntity
      entity4.setLootTable(lootTable, seed);
    }
  }

  default void copyEntityData(ILootrItemFrameAccessor<Entity> adapter, Entity entity1, ILootrEntity entity3) {
    Entity entity2 = entity3.asEntity();
    entity2.setXRot(entity1.getXRot());
    entity2.setYRot(entity1.getYRot());
    entity2.setYHeadRot(entity1.getYHeadRot());
  }

  void refreshPlayerSection(ServerPlayer player);

  void performPotBreak(ILootrBlockEntity lootrDecoratedPotBlockEntity, ServerPlayer player);

  boolean shouldDoInitialSave();
}
