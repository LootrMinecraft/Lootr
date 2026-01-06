package noobanidus.mods.lootr.common.block.entity;

import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.*;
import noobanidus.mods.lootr.common.api.data.LootrBlockType;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import org.jetbrains.annotations.Nullable;

public class LootrInventoryBlockEntity extends LootrChestBlockEntity {
  private NonNullList<ItemStack> customInventory;

  public LootrInventoryBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
    super(LootrRegistry.getInventoryBlockEntity(), pWorldPosition, pBlockState);
  }

  @Override
  public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
    super.loadAdditional(compound, provider);
    if (compound.contains(NBTConstants.CUSTOM_INVENTORY) && compound.contains(NBTConstants.CUSTOM_SIZE)) {
      int size = compound.getInt(NBTConstants.CUSTOM_SIZE);
      this.customInventory = NonNullList.withSize(size, ItemStack.EMPTY);
      ContainerHelper.loadAllItems(compound.getCompound(NBTConstants.CUSTOM_INVENTORY), this.customInventory, provider);
    }
  }

  @Override
  protected void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
    super.saveAdditional(compound, provider);
    if (this.customInventory != null) {
      compound.putInt(NBTConstants.CUSTOM_SIZE, this.customInventory.size());
      compound.put(NBTConstants.CUSTOM_INVENTORY, ContainerHelper.saveAllItems(new CompoundTag(), this.customInventory, provider));
    }
  }

  @Override
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
  public ILootrType getInfoNewType() {
    return BuiltInLootrTypes.INVENTORY;
  }

  @Override
  public boolean isInfoReferenceInventory() {
    return true;
  }

  @Override
  @Deprecated
  public LootrBlockType getInfoBlockType() {
    return LootrBlockType.INVENTORY;
  }

  @AutoService(ILootrBlockEntityConverter.class)
  public static class DefaultBlockEntityConverter implements ILootrBlockEntityConverter<LootrInventoryBlockEntity> {

    @Override
    public ILootrBlockEntity apply(LootrInventoryBlockEntity blockEntity) {
      return blockEntity;
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
      return LootrRegistry.getInventoryBlockEntity();
    }
  }
}
