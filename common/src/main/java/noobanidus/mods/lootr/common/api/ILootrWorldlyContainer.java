package noobanidus.mods.lootr.common.api;

import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ILootrWorldlyContainer extends WorldlyContainer {
  int[] SLOTS = new int[0];

  @Override
  default int[] getSlotsForFace(@NotNull Direction side) {
    return SLOTS;
  }

  @Override
  default boolean canPlaceItemThroughFace(int index, @NotNull ItemStack itemStack, @Nullable Direction direction) {
    return false;
  }

  @Override
  default boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
    return false;
  }
}
