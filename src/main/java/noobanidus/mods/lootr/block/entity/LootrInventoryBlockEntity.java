package noobanidus.mods.lootr.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.registry.LootrRegistry;
import org.jetbrains.annotations.Nullable;

public class LootrInventoryBlockEntity extends LootrChestBlockEntity {
  private NonNullList<ItemStack> customInventory;

  public LootrInventoryBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
    super(LootrRegistry.getInventoryBlockEntity(), pWorldPosition, pBlockState);
  }

  @Override
  public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
    super.loadAdditional(compound, provider);
    if (compound.contains("customInventory") && compound.contains("customSize")) {
      int size = compound.getInt("customSize");
      this.customInventory = NonNullList.withSize(size, ItemStack.EMPTY);
      ContainerHelper.loadAllItems(compound.getCompound("customInventory"), this.customInventory, provider);
    }
  }

  @Override
  protected void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
    super.saveAdditional(compound, provider);
    if (this.customInventory != null) {
      compound.putInt("customSize", this.customInventory.size());
      compound.put("customInventory", ContainerHelper.saveAllItems(new CompoundTag(), this.customInventory, provider));
    }
  }

  @Nullable
  public NonNullList<ItemStack> getInfoReferenceInventory() {
    return customInventory;
  }

  public void setCustomInventory(NonNullList<ItemStack> customInventory) {
    this.customInventory = customInventory;
  }

  @Override
  protected void signalOpenCount(Level level, BlockPos pos, BlockState state, int p_155868_, int p_155869_) {
    super.signalOpenCount(level, pos, state, p_155868_, p_155869_);
    if (LootrAPI.isCustomTrapped() && p_155868_ != p_155869_) {
      Block block = state.getBlock();
      level.updateNeighborsAt(pos, block);
      level.updateNeighborsAt(pos.below(), block);
    }
  }


  @Override
  public boolean isInfoReferenceInventory() {
    return true;
  }
}
