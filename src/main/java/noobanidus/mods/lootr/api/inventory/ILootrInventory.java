package noobanidus.mods.lootr.api.inventory;

import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface ILootrInventory extends Container, MenuProvider {

  BaseContainerBlockEntity getBlockEntity (Level level);

  @Deprecated
  @Nullable
  default BaseContainerBlockEntity getTile(Level level) {
    return getBlockEntity(level);
  }

  @Nullable
  AbstractMinecartContainer getEntity(Level world);

  @Nullable
  BlockPos getPos();

  NonNullList<ItemStack> getInventoryContents();

}
