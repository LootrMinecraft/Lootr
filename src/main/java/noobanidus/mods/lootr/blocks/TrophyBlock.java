package noobanidus.mods.lootr.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class TrophyBlock extends Block {
  public TrophyBlock(Properties properties) {
    super(properties);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.getDefaultState().with(HorizontalBlock.HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    super.fillStateContainer(builder);
    builder.add(HorizontalBlock.HORIZONTAL_FACING);
  }

  private static final VoxelShape EAST_WEST = Block.makeCuboidShape(1.5, 0, 4, 14.5, 14.5, 12);
  private static final VoxelShape NORTH_SOUTH = Block.makeCuboidShape(4, 0, 1.5, 12, 14.5, 14.5);

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    Direction facing = state.get(HorizontalBlock.HORIZONTAL_FACING);
    if (facing == Direction.EAST || facing == Direction.WEST) {
      return EAST_WEST;
    } else {
      return NORTH_SOUTH;
    }
  }
}
