package noobanidus.mods.lootr.common.impl;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import noobanidus.mods.lootr.common.api.data.LootFiller;
import noobanidus.mods.lootr.common.api.filter.ILootrFilter;

public abstract class BooleanFilter implements ILootrFilter {
  public abstract boolean discard(ItemStack stack);

  @Override
  public boolean mutate(ObjectArrayList<ItemStack> toMutate, LootFiller.LootFillerState state, LootContext context, RandomSource random) {
    toMutate.removeIf(this::discard);
    return false;
  }
}
