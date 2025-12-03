package noobanidus.mods.lootr.common.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public interface ILootrDataAdapter<T> {
  Class<T> getAssignableClass ();
  ResourceKey<LootTable> getLootTable (T entity);
  long getLootSeed (T entity);
  void setLootTable (T entity, ResourceKey<LootTable> table, long seed);
}
