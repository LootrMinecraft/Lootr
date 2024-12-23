package noobanidus.mods.lootr.common.api.filter;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import noobanidus.mods.lootr.common.api.data.LootFiller;

public interface ILootrFilter {
  int getPriority();
  String getName();

  // Returns true if filtering should stop
  boolean mutate (ObjectArrayList<ItemStack> toMutate, LootFiller.LootFillerState state, LootContext context, RandomSource random);
}
