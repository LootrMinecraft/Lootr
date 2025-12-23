package noobanidus.mods.lootr.common.api.adapter;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

/**
 * Data adapters provide access to the loot table and the
 * loot seed of a specific object, as well as the means of
 * setting them (although this is only used to wipe them).
 * <br />
 * Adapters are loaded via ServiceLoader. Implementations should be
 * listed in a file located at:
 * META-INF/services/noobanidus.mods.lootr.common.api.adapter.ILootrDataAdapter
 * <br />
 * For example implementations, see noobanidus.mods.lootr.common.impl.adapter.
 * <br />
 * Adapters are accessed via LootrAPI::getAdapter.
 */
public interface ILootrDataAdapter<T> {
  Class<T> getAssignableClass();

  @Nullable
  ResourceKey<LootTable> getLootTable(T entity);

  long getLootSeed(T entity);

  void setLootTable(T entity, ResourceKey<LootTable> table, long seed);

  default boolean hasCopyableComponentsViaItem(T entity) {
    return false;
  }

  default int priority () {
    return 0;
  }
}
