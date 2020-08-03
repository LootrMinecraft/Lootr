package noobanidus.mods.lootr.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import noobanidus.mods.lootr.data.NewChestData;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;
import noobanidus.mods.lootr.util.ChestUtil;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
public class LootrChestBlock extends ChestBlock {
  public LootrChestBlock(Properties properties) {
    super(properties);
  }

  @Override
  public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
    return ChestUtil.handleLootChest(world, pos, player);
  }

  @Override
  public void onReplaced(BlockState oldState, World world, BlockPos pos, BlockState newState, boolean isMoving) {
    NewChestData.deleteLootChest(world, pos);
    super.onReplaced(oldState, world, pos, newState, isMoving);
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
    return new SpecialLootChestTile();
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
    return field_196315_B;
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    Direction direction = context.getPlacementHorizontalFacing().getOpposite();
    IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
    return this.getDefaultState().with(FACING, direction).with(TYPE, ChestType.SINGLE).with(WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER);
  }

  @Override
  @Nullable
  public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
    return ChestUtil.getContainer(state, worldIn, pos);
  }

  @Override
  public String getTranslationKey() {
    return Blocks.CHEST.getTranslationKey();
  }
}
