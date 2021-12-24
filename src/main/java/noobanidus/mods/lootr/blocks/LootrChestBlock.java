package noobanidus.mods.lootr.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
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
import noobanidus.mods.lootr.data.DataStorage;
import noobanidus.mods.lootr.init.ModTiles;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;
import noobanidus.mods.lootr.util.ChestUtil;

import javax.annotation.Nullable;
import java.util.function.Supplier;

@SuppressWarnings("NullableProblems")
public class LootrChestBlock extends ChestBlock {
  public LootrChestBlock(Properties properties) {
    this(properties, () -> ModTiles.SPECIAL_LOOT_CHEST);
  }

  public LootrChestBlock(Properties builder, Supplier<TileEntityType<? extends ChestTileEntity>> tileEntityTypeIn) {
    super(builder, tileEntityTypeIn);
  }

  @Override
  public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
    if (player.isShiftKeyDown()) {
      ChestUtil.handleLootSneak(this, world, pos, player);
    } else if (!ChestBlock.isChestBlockedAt(world, pos)) {
      ChestUtil.handleLootChest(this, world, pos, player);
    }
    return ActionResultType.SUCCESS;
  }

  @Override
  public void onRemove(BlockState oldState, World world, BlockPos pos, BlockState newState, boolean isMoving) {
    if (oldState.getBlock() != newState.getBlock() && world instanceof ServerWorld) {
      DataStorage.deleteLootChest((ServerWorld) world, pos);
    }
    super.onRemove(oldState, world, pos, newState, isMoving);
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
    return new SpecialLootChestTile();
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new SpecialLootChestTile();
  }

  @Override
  public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
    if (stateIn.getValue(WATERLOGGED)) {
      worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
    }

    return stateIn;
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return AABB;
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    Direction direction = context.getHorizontalDirection().getOpposite();
    FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
    return this.defaultBlockState().setValue(FACING, direction).setValue(TYPE, ChestType.SINGLE).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
  }

  @Override
  public FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  @Override
  @Nullable
  public INamedContainerProvider getMenuProvider(BlockState state, World worldIn, BlockPos pos) {
    return null;
  }
}
