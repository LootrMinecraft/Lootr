package noobanidus.mods.lootr.common.impl;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.ILootrDataAdapter;

public class RandomizableContainerBlockEntityAdapter implements ILootrDataAdapter<RandomizableContainerBlockEntity> {

  @Override
  public Class<RandomizableContainerBlockEntity> getAssignableClass() {
    return RandomizableContainerBlockEntity.class;
  }

  @Override
  public ResourceKey<LootTable> getLootTable(RandomizableContainerBlockEntity entity) {
    return entity.getLootTable();
  }

  @Override
  public long getLootSeed(RandomizableContainerBlockEntity entity) {
    return entity.getLootTableSeed();
  }

  @Override
  public void setLootTable(RandomizableContainerBlockEntity entity, ResourceKey<LootTable> table, long seed) {
    entity.setLootTable(table, seed);
  }
}
