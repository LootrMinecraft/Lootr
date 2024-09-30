package noobanidus.mods.lootr.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.ILootrBlockEntityConverter;
import noobanidus.mods.lootr.common.api.data.LootrBlockType;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;

public class LootrTrappedChestBlockEntity extends LootrChestBlockEntity {
  public LootrTrappedChestBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
    super(LootrRegistry.getTrappedChestBlockEntity(), pWorldPosition, pBlockState);
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

  @Override
  public LootrBlockType getInfoBlockType() {
    return LootrBlockType.TRAPPED_CHEST;
  }

  public static class DefaultBlockEntityConverter implements ILootrBlockEntityConverter<LootrTrappedChestBlockEntity> {
    @Override
    public ILootrBlockEntity apply(LootrTrappedChestBlockEntity blockEntity) {
      return blockEntity;
    }

    @Override
    public Class<? extends LootrTrappedChestBlockEntity> getClassType() {
      return LootrTrappedChestBlockEntity.class;
    }
  }
}

