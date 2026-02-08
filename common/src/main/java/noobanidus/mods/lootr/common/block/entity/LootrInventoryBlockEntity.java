package noobanidus.mods.lootr.common.block.entity;

import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.*;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.api.type.BuiltInLootrTypes;
import noobanidus.mods.lootr.common.api.type.ILootrType;
import noobanidus.mods.lootr.common.api.wrapper.ILootrBlockEntityWrapper;
import org.jetbrains.annotations.Nullable;

public class LootrInventoryBlockEntity extends LootrChestBlockEntity {
  public LootrInventoryBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
    super(LootrRegistry.getInventoryBlockEntity(), pWorldPosition, pBlockState);
  }

  @Override
  @Nullable
  public NonNullList<ItemStack> getInfoReferenceInventory() {
    return simpleLootrInstance.getReferenceInventory();
  }

  public void setCustomInventory(NonNullList<ItemStack> customInventory) {
    simpleLootrInstance.setReferenceInventory(customInventory);
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
  public ILootrType getInfoType() {
    return BuiltInLootrTypes.INVENTORY;
  }

  @Override
  public boolean isInfoReferenceInventory() {
    return true;
  }

  @AutoService(ILootrBlockEntityWrapper.class)
  public static class DefaultBlockEntityWrapper implements ILootrBlockEntityWrapper<LootrInventoryBlockEntity> {

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
