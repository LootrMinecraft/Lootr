package noobanidus.mods.lootr.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.block.entity.LootrBrushableBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class LootrBrushableBlock extends BrushableBlock {
  public static final IntegerProperty DUSTED = BlockStateProperties.DUSTED;

  public LootrBrushableBlock(Block pseudoReplacement, SoundEvent soundEvent, SoundEvent soundEvent2, BlockBehaviour.Properties properties) {
    super(pseudoReplacement, soundEvent, soundEvent2, properties);
  }

  @Override
  public float getExplosionResistance() {
    return LootrAPI.getExplosionResistance(this, super.getExplosionResistance());
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(DUSTED);
  }

  @Override
  public @NonNull RenderShape getRenderShape(@NonNull BlockState blockState) {
    return RenderShape.MODEL;
  }

  @Override
  public void onPlace(@NonNull BlockState blockState, Level level, @NonNull BlockPos blockPos, @NonNull BlockState blockState2, boolean bl) {
    level.scheduleTick(blockPos, this, 2);
  }

  @Override
  public @NonNull BlockState updateShape(@NonNull BlockState blockState, @NonNull LevelReader levelAccessor, ScheduledTickAccess scheduledTickAccess, @NonNull BlockPos blockPos, @NonNull Direction direction, @NonNull BlockPos blockPos2, @NonNull BlockState blockState2, @NonNull RandomSource random) {
    scheduledTickAccess.scheduleTick(blockPos, this, 2);
    return super.updateShape(blockState, levelAccessor, scheduledTickAccess, blockPos, direction, blockPos2, blockState2, random);
  }

  @Override
  public void tick(@NonNull BlockState blockState, ServerLevel serverLevel, @NonNull BlockPos blockPos, @NonNull RandomSource randomSource) {
    BlockEntity var6 = serverLevel.getBlockEntity(blockPos);
    if (!(var6 instanceof LootrBrushableBlockEntity brushableBlockEntity)) {
      return;
    }

    brushableBlockEntity.IBrushable$checkReset();

    if (LootrAPI.canBrushablesSelfSupport()) {
      return;
    }
    if (FallingBlock.isFree(serverLevel.getBlockState(blockPos.below())) && blockPos.getY() >= serverLevel.getMinY()) {
      LootrBrushableBlockEntity.fall(serverLevel, blockPos, blockState, brushableBlockEntity);
    }
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
    return new LootrBrushableBlockEntity(blockPos, blockState);
  }

  @Override
  @Nullable
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level pLevel, @NonNull BlockState pState, @NonNull BlockEntityType<T> pBlockEntityType) {
    return ILootrBlockEntity::ticker;
  }

  @Override
  protected void affectNeighborsAfterRemoval(@NonNull BlockState state, @NonNull ServerLevel level, @NonNull BlockPos pos, boolean movedByPiston) {
    Containers.updateNeighboursAfterDestroy(state, level, pos);
  }

  @Override
  protected boolean hasAnalogOutputSignal(BlockState state) {
    return true;
  }

  @Override
  public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos, Direction direction) {
    return LootrAPI.getAnalogOutputSignal(pBlockState, pLevel, pPos, 0, direction);
  }
}
