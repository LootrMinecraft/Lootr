package net.zestyblaze.lootr.api.inventory;

import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface ILootrInventory extends Inventory, NamedScreenHandlerFactory {
    @Nullable LootableContainerBlockEntity getTile(World world);

    @Nullable StorageMinecartEntity getEntity(World world);

    @Nullable BlockPos getPos();

    DefaultedList<ItemStack> getContents();
}
