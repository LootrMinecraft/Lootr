package noobanidus.mods.lootr.common.impl;

import net.minecraft.world.item.ItemStack;
import noobanidus.mods.lootr.common.api.filter.ILootrFilter;
import noobanidus.mods.lootr.common.api.filter.ILootrFilterProvider;

import java.util.List;

public class LootrModFilter extends BooleanFilter {
  private static LootrModFilter INSTANCE = new LootrModFilter();

  @Override
  public int getPriority() {
    return 1000;
  }

  @Override
  public String getName() {
    return "Lootr Mod Filter";
  }

  @Override
  public boolean discard(ItemStack stack) {
    return stack.getItemHolder().unwrapKey().map(resourceKey -> resourceKey.location().getNamespace()).orElse("[unknown mod]").equals("minecraft");
  }

  public static class Provider implements ILootrFilterProvider {
    @Override
    public List<ILootrFilter> getFilters() {
      return List.of(INSTANCE);
    }
  }
}
