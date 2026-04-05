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
import noobanidus.mods.lootr.common.api.LootrRegistry;
import noobanidus.mods.lootr.common.api.BuiltInLootrTypes;
import noobanidus.mods.lootr.common.api.interfaces.type.ILootrType;
import noobanidus.mods.lootr.common.api.interfaces.wrapper.ILootrBlockEntityWrapper;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class LootrInventoryBlockEntity extends LootrChestBlockEntity {
  public LootrInventoryBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
    super(LootrRegistry.getInventoryBlockEntity(), pWorldPosition, pBlockState);
  }

  @Override
  @Nullable
  public NonNullList<ItemStack> getDataReferenceInventory() {
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
  public @NonNull ILootrType getDataType() {
    return BuiltInLootrTypes.INVENTORY;
  }

  @Override
  public boolean isDataReferenceInventory() {
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
