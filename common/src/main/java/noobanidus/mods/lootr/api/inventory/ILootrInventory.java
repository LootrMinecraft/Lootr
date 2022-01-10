package noobanidus.mods.lootr.api.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;

public interface ILootrInventory extends Container, MenuProvider {

  RandomizableContainerBlockEntity getTile(Level world);


  AbstractMinecartContainer getEntity(Level world);


  BlockPos getPos();

  NonNullList<ItemStack> getContents();

}
