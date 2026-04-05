package noobanidus.mods.lootr.common.api.interfaces.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public non-sealed interface ILootrItemFrameAccessor<T> extends ILootrAccessor<T> {
  Direction getDirection(T object);

  ItemStack getItem (T object);

  int getRotation (T object);

  BlockPos getPos (T object);

  boolean isFixed (T object);

  boolean isInvisible (T object);
}
