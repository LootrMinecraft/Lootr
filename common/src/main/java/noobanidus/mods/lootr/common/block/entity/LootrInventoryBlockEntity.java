package noobanidus.mods.lootr.common.block.entity;

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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import noobanidus.mods.lootr.common.api.ILootrBlockEntityConverter;
import noobanidus.mods.lootr.common.api.LootrAPI;
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
  protected void loadAdditional(ValueInput compound) {
    super.loadAdditional(compound);
    compound.getInt("customSize").ifPresent(size -> {
      this.customInventory = NonNullList.withSize(size, ItemStack.EMPTY);
      LootrAPI.loadAllItems(compound, this.customInventory, "customInventory");
    });
  }

  @Override
  protected void saveAdditional(ValueOutput compound) {
    super.saveAdditional(compound);
    if (this.customInventory != null) {
      compound.putInt("customSize", this.customInventory.size());
      LootrAPI.saveAllItems(compound, this.customInventory, true, "customInventory");
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

  @Override
  public LootrBlockType getInfoBlockType() {
    return LootrBlockType.INVENTORY;
  }

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
