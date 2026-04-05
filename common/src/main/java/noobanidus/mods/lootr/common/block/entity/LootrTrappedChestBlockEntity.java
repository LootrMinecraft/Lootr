package noobanidus.mods.lootr.common.block.entity;

import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.type.BuiltInLootrTypes;
import noobanidus.mods.lootr.common.api.wrapper.ILootrBlockEntityWrapper;
import noobanidus.mods.lootr.common.api.type.ILootrType;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import org.jspecify.annotations.NonNull;

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
  public @NonNull ILootrType getDataType() {
    return BuiltInLootrTypes.TRAPPED_CHEST;
  }

  @AutoService(ILootrBlockEntityWrapper.class)
  public static class DefaultBlockEntityWrapper implements ILootrBlockEntityWrapper<LootrTrappedChestBlockEntity> {
    @Override
    public ILootrBlockEntity apply(LootrTrappedChestBlockEntity blockEntity) {
      return blockEntity;
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
      return LootrRegistry.getTrappedChestBlockEntity();
    }
  }
}

