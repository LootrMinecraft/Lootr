package noobanidus.mods.lootr.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.block.entity.LootrBrushableBlockEntity;
import org.jetbrains.annotations.Nullable;

// Abstract to compensate for platform-specific block entities which exist
// in order to dynamically handle client-side rendering.
public abstract class LootrBrushableBlock extends BrushableBlock {
  public static final IntegerProperty DUSTED = BlockStateProperties.DUSTED;

  public LootrBrushableBlock(Block pseudoReplacement, SoundEvent soundEvent, SoundEvent soundEvent2, BlockBehaviour.Properties properties) {
    super(pseudoReplacement, soundEvent, soundEvent2, properties);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(DUSTED);
  }

  @Override
  public RenderShape getRenderShape(BlockState blockState) {
    return RenderShape.MODEL;
  }

  @Override
  public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
    level.scheduleTick(blockPos, this, 2);
  }

  @Override
  public BlockState updateShape(
      BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2
  ) {
    levelAccessor.scheduleTick(blockPos, this, 2);
    return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
  }

  @Override
  public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
    BlockEntity var6 = serverLevel.getBlockEntity(blockPos);
    if (!(var6 instanceof LootrBrushableBlockEntity brushableBlockEntity)) {
      return;
    }

    brushableBlockEntity.IBrushable$checkReset();

    if (FallingBlock.isFree(serverLevel.getBlockState(blockPos.below())) && blockPos.getY() >= serverLevel.getMinBuildHeight()) {
      LootrBrushableBlockEntity.fall(serverLevel, blockPos, blockState, brushableBlockEntity);
    }
  }

  @Nullable
  @Override
  public abstract BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState);
}
