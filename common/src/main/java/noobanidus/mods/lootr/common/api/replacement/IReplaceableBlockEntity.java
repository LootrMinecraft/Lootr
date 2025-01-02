package noobanidus.mods.lootr.common.api.replacement;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;

public interface IReplaceableBlockEntity {
  default BlockEntity getBlockEntity () {
    return (BlockEntity) this;
  }

  ResourceKey<LootTable> getLootTable ();
  long getSeed ();

  void setLootTable (ResourceKey<LootTable> table);

  default void setLootTable (ResourceKey<LootTable> table, long seed) {
    setLootTable(table);
    setLootSeed(seed);
  }

  void setLootSeed (long seed);
}
