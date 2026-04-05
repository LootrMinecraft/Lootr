package noobanidus.mods.lootr.common.api.filter;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import noobanidus.mods.lootr.common.api.filler.ILootFiller;

/**
 * Filters that can modify loot before it is placed in an inventory.
 * <br />
 * In theory, these are fully functional (although they will not work with Clavis)
 * and can be registered via ServiceLoader (via ILootrFilterProvider), but
 * in practice they have never been used.
 */
public interface ILootrFilter {
  int getPriority();
  String getName();

  // Returns true if filtering should stop
  boolean mutate (ObjectArrayList<ItemStack> toMutate, ILootFiller.LootFillerState state, LootContext context, RandomSource random);

  default boolean mutate (ObjectArrayList<ItemStack> toMutate, ILootFiller.LootFillerState state, LootContext context) {
    return mutate(toMutate, state, context, context.getRandom());
  }
}
