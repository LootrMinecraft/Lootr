package net.zestyblaze.lootr.block.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.zestyblaze.lootr.init.ModBlockEntities;

public class LootrTrappedChestBlockEntity extends LootrChestBlockEntity {
  protected LootrTrappedChestBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  public LootrTrappedChestBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
    this(ModBlockEntities.SPECIAL_TRAPPED_LOOT_CHEST, pWorldPosition, pBlockState);
  }

  @Override
  protected void signalOpenCount(Level level, BlockPos pos, BlockState state, int p_155868_, int p_155869_) {
    super.signalOpenCount(level, pos, state, p_155868_, p_155869_);
    if (p_155868_ != p_155869_) {
      Block block = state.getBlock();
      level.updateNeighborsAt(pos, block);
      level.updateNeighborsAt(pos.below(), block);
    }
  }
}
