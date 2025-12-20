package noobanidus.mods.lootr.common.api.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.stream.Stream;

public interface IPotDecorationsAdapter {
  ItemStack back();

  ItemStack front();

  ItemStack right();

  ItemStack left();

  default List<ItemStack> ordered() {
    return Stream.of(back(), left(), right(), front()).map(o -> o.isEmpty() ? new ItemStack(Items.BRICK) : o).toList();
  }

  CompoundTag save (CompoundTag tag);

  void load (CompoundTag tag);

  default boolean isEmpty () {
    return back().isEmpty() && front().isEmpty() && right().isEmpty() && left().isEmpty();
  }
}
