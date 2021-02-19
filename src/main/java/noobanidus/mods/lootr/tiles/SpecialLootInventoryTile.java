package noobanidus.mods.lootr.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.NonNullList;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.init.ModTiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings({"Duplicates", "ConstantConditions", "NullableProblems", "WeakerAccess"})
public class SpecialLootInventoryTile extends SpecialLootChestTile {
  private NonNullList<ItemStack> customInventory;

  public SpecialLootInventoryTile() {
    super(ModTiles.SPECIAL_LOOT_INVENTORY);
  }

  @Override
  public void read(BlockState state, CompoundNBT compound) {
    super.read(state, compound);
    if (compound.contains("customInventory") && compound.contains("customSize")) {
      int size = compound.getInt("customSize");
      this.customInventory = NonNullList.withSize(size, ItemStack.EMPTY);
      ItemStackHelper.loadAllItems(compound.getCompound("customInventory"), this.customInventory);
    }
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    compound = super.write(compound);
    if (this.customInventory != null) {
      compound.putInt("customSize", this.customInventory.size());
      compound.put("customInventory", ItemStackHelper.saveAllItems(new CompoundNBT(), this.customInventory));
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
  public void onDataPacket(@Nonnull NetworkManager net, @Nonnull SUpdateTileEntityPacket pkt) {
    read(ModBlocks.INVENTORY.getDefaultState(), pkt.getNbtCompound());
  }
}
