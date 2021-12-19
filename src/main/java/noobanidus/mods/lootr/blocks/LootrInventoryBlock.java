package noobanidus.mods.lootr.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import noobanidus.mods.lootr.blocks.entities.LootrChestBlockEntity;
import noobanidus.mods.lootr.blocks.entities.LootrInventoryBlockEntity;
import noobanidus.mods.lootr.data.NewChestData;
import noobanidus.mods.lootr.init.ModBlockEntities;
import noobanidus.mods.lootr.util.ChestUtil;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Supplier;

public class LootrInventoryBlock extends ChestBlock {
  public LootrInventoryBlock(Properties properties) {
    super(properties, () -> ModBlockEntities.SPECIAL_LOOT_INVENTORY);
  }

  public LootrInventoryBlock(Properties properties, Supplier<BlockEntityType<? extends ChestBlockEntity>> type) {
    super(properties, type);
  }

  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
    if (player.isCrouching()) {
      ChestUtil.handleLootSneak(this, world, pos, player);
    } else if (!ChestBlock.isChestBlockedAt(world, pos)) {
      ChestUtil.handleLootInventory(this, world, pos, player);
    }
    return InteractionResult.SUCCESS;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new LootrInventoryBlockEntity(pos, state);
  }

  @Override
  public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
    if (stateIn.getValue(WATERLOGGED)) {
      worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
    }

    return stateIn;
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return AABB;
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
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
  public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
    return null;
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState pState) {
    return false;
  }

  @Override
  public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
    return 0;
  }

  @Override
  @Nullable
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
    return pLevel.isClientSide ? LootrChestBlockEntity::lootrLidAnimateTick : null;
  }

  @Override
  public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRandom) {
    BlockEntity blockentity = pLevel.getBlockEntity(pPos);
    if (blockentity instanceof LootrInventoryBlockEntity) {
      ((LootrInventoryBlockEntity) blockentity).recheckOpen();
    }
  }
}
