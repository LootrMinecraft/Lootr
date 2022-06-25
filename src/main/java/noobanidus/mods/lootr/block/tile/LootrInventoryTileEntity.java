package noobanidus.mods.lootr.block.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings({"Duplicates", "ConstantConditions", "NullableProblems", "WeakerAccess"})
public class LootrInventoryTileEntity extends LootrChestTileEntity {
    private NonNullList<ItemStack> customInventory;

    public LootrInventoryTileEntity() {
        super();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("customInventory") && compound.hasKey("customSize")) {
            int size = compound.getInteger("customSize");
            this.customInventory = NonNullList.withSize(size, ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(compound.getCompoundTag("customInventory"), this.customInventory);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        if (this.customInventory != null) {
            compound.setInteger("customSize", this.customInventory.size());
            compound.setTag("customInventory", ItemStackHelper.saveAllItems(new NBTTagCompound(), this.customInventory));
        }
        return compound;
    }

    @Nullable
    public NonNullList<ItemStack> getCustomInventory() {
        return customInventory;
    }

    public void setCustomInventory(NonNullList<ItemStack> customInventory) {
        this.customInventory = customInventory;
    }

    @Override
    public void onDataPacket(@Nonnull NetworkManager net, @Nonnull SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }
}
