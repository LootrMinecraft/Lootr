package noobanidus.mods.lootr.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

public class TrophyBlock extends Block {
  public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

  public TrophyBlock(Properties properties) {
    super(properties);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(HorizontalDirectionalBlock.FACING);
  }

  @Override
  public BlockState mirror(BlockState blockState, Mirror mirror) {
    return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
  }

  @Override
  public BlockState rotate(BlockState blockState, Rotation rotation) {
    return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
  }

  private static final VoxelShape EAST_WEST = Block.box(1.5, 0, 4, 14.5, 14.5, 12);
  private static final VoxelShape NORTH_SOUTH = Block.box(4, 0, 1.5, 12, 14.5, 14.5);

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
    if (facing == Direction.EAST || facing == Direction.WEST) {
      return EAST_WEST;
    } else {
      return NORTH_SOUTH;
    }
  }
}
