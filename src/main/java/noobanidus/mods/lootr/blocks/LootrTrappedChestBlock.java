package noobanidus.mods.lootr.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TrappedChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import noobanidus.mods.lootr.data.NewChestData;
import noobanidus.mods.lootr.tiles.SpecialTrappedLootChestTile;
import noobanidus.mods.lootr.util.ChestUtil;

import javax.annotation.Nullable;

@SuppressWarnings({"NullableProblems"})
public class LootrTrappedChestBlock extends TrappedChestBlock {
  public LootrTrappedChestBlock(Properties properties) {
    super(properties);
  }

  @Override
  public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
    return new SpecialTrappedLootChestTile();
  }

  @Override
  public void onReplaced(BlockState oldState, World world, BlockPos pos, BlockState newState, boolean isMoving) {
    if (oldState.getBlock() != newState.getBlock() && world instanceof ServerWorld) {
      NewChestData.deleteLootChest((ServerWorld) world, pos);
    }
    super.onReplaced(oldState, world, pos, newState, isMoving);
  }

  @Override
  public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
    ChestUtil.handleLootChest(world, pos, player);
    return ActionResultType.SUCCESS;
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
    if (stateIn.get(WATERLOGGED)) {
      worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
    }

    return stateIn;
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return SHAPE_SINGLE;
  }

  @Override
  @Nullable
  public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
    return null;
  }

  @Override
  public String getTranslationKey() {
    return Blocks.TRAPPED_CHEST.getTranslationKey();
  }
}
