package noobanidus.mods.lootr.api.inventory;

import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface ILootrInventory extends IInventory, IInteractionObject {
  @Nullable
  TileEntityLockableLoot getTile(World world);

  @Nullable
  EntityMinecartContainer getEntity(World world);

  @Nullable
  BlockPos getPos();

  NonNullList<ItemStack> getInventoryContents();

}
