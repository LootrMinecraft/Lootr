package noobanidus.mods.lootr.common.api.data;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface LootFiller {
  /**
   * Used to fill newly created inventories in `ChestData::createInventory` and variants.
   *
   * @param provider  The provider that contains loot table, seed, etc.
   * @param player    The player that is opening the container. This is never null.
   * @param inventory The new inventory that has been created for this player.
   *                  <p>
   *                  In general, the correct implementation for this would duplicate the functionality of `RandomizableContainerBlockEntity::unpackLootTable(Player player)`, except that the player is guaranteed to be non-null.
   *                  <p>
   *                  In every instance of its use, the provided loot table should be relied upon as the correct loot table.
   *                  <p>
   *                  Example implementations can be found in `LootrChestblockEntity::unpackLootTable`.
   */
  void unpackLootTable(@NotNull ILootrInfoProvider provider, @NotNull Player player, Container inventory);

  default void fill (ILootrInfoProvider provider, Player player, ResourceKey<LootTable> lootTableKey, LootTable lootTable, Container container, LootParams parameters, long seed) {
    DefaultLootFiller.performFill(provider, player, lootTableKey, lootTable, container, parameters, seed);
  }

  record LootFillerState (ILootrInfoProvider provider, Player player, ResourceKey<LootTable> lootTableKey, LootTable lootTable, Container container, LootParams parameters, long seed) {
  }
}
