package noobanidus.mods.lootr.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TrophyBlock extends Block {
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
