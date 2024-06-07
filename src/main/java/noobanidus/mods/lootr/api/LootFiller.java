package noobanidus.mods.lootr.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface LootFiller {
  /**
   * Used to fill newly created inventories in `ChestData::createInventory` and variants.
   *
   * @param player    The player that is opening the container. This is never null.
   * @param inventory The new inventory that has been created for this player.
   * @param table     The ResourceLocation containing the table.
   * @param seed      The generated seed.
   *                  <p>
   *                  In general, the correct implementation for this would duplicate the functionality of `RandomizableContainerBlockEntity::unpackLootTable(Player player)`, except that the player is guaranteed to be non-null.
   *                  <p>
   *                  In every instance of its use, the provided loot table should be relied upon as the correct loot table.
   *                  <p>
   *                  Example implementations can be found in `LootrChestblockEntity::unpackLootTable`.
   */
  void unpackLootTable(@NotNull Player player, Container inventory, ResourceLocation table, long seed);
}
