package noobanidus.mods.lootr.api.inventory;

import net.minecraft.entity.item.minecart.ContainerMinecartEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface ILootrInventory extends IInventory, INamedContainerProvider {
  @Nullable
  LockableLootTileEntity getTile(World world);

  @Nullable
  ContainerMinecartEntity getEntity(World world);

  @Nullable
  BlockPos getPos();

  NonNullList<ItemStack> getContents();

}
