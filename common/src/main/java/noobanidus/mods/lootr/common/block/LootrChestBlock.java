package noobanidus.mods.lootr.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.ILootrContainerInstance;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.LootrChestBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class LootrChestBlock extends ChestBlock {
  static final VoxelShape SHAPE = Block.column(14.0, 0.0, 14.0);

  public LootrChestBlock(Properties properties) {
    super(LootrRegistry::getChestBlockEntity, SoundEvents.CHEST_OPEN, SoundEvents.CHEST_CLOSE, properties);
  }

  @Override
  public float getExplosionResistance() {
    return LootrAPI.getExplosionResistance(this, super.getExplosionResistance());
  }

  @Override
  public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult trace) {
    if (level.isClientSide() || player.isSpectator() || !(player instanceof ServerPlayer serverPlayer)) {
      return InteractionResult.CONSUME;
    }
    if (serverPlayer.isShiftKeyDown()) {
      LootrAPI.handleInstanceSneak(ILootrContainerInstance.of(pos, level), serverPlayer);
    } else if (!isChestBlockedAt(level, pos)) {
      LootrAPI.handleInstanceOpen(ILootrContainerInstance.of(pos, level), serverPlayer);
    }
    return InteractionResult.SUCCESS;
  }

  @Override
  public @NonNull BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
    return new LootrChestBlockEntity(pos, state);
  }

  @Override
  protected @NonNull BlockState updateShape(BlockState blockState, @NonNull LevelReader levelReader, @NonNull ScheduledTickAccess scheduledTickAccess, @NonNull BlockPos blockPos, @NonNull Direction direction, @NonNull BlockPos blockPos2, @NonNull BlockState blockState2, @NonNull RandomSource randomSource) {
    if (blockState.getValue(WATERLOGGED)) {
      scheduledTickAccess.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
    }

    return blockState;
  }

  @Override
  public @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter worldIn, @NonNull BlockPos pos, @NonNull CollisionContext context) {
    return SHAPE;
  }

  @Override
  public @NonNull BlockState getStateForPlacement(BlockPlaceContext context) {
    Direction direction = context.getHorizontalDirection().getOpposite();
    FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
    return this.defaultBlockState().setValue(FACING, direction).setValue(TYPE, ChestType.SINGLE)
        .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
  }

  @Override
  public @NonNull FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  @Override
  @Nullable
  public MenuProvider getMenuProvider(@NonNull BlockState state, @NonNull Level worldIn, @NonNull BlockPos pos) {
    return null;
  }

  @Override
  public boolean hasAnalogOutputSignal(@NonNull BlockState pState) {
    return true;
  }

  @Override
  public float getDestroyProgress(@NonNull BlockState pState, @NonNull Player pPlayer, @NonNull BlockGetter pLevel, @NonNull BlockPos pPos) {
    return LootrAPI.getDestroyProgress(pState, pPlayer, pLevel, pPos, super.getDestroyProgress(pState, pPlayer, pLevel, pPos));
  }

  @Override
  public int getAnalogOutputSignal(@NonNull BlockState pBlockState, @NonNull Level pLevel, @NonNull BlockPos pPos, @NonNull Direction direction) {
    return LootrAPI.getAnalogOutputSignal(pBlockState, pLevel, pPos, 0, direction);
  }

  @Override
  @Nullable
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level pLevel, @NonNull BlockState pState, @NonNull BlockEntityType<T> pBlockEntityType) {
    return ILootrBlockEntity::ticker;
  }

  @Override
  public void tick(@NonNull BlockState pState, ServerLevel pLevel, @NonNull BlockPos pPos, @NonNull RandomSource pRandom) {
    BlockEntity blockentity = pLevel.getBlockEntity(pPos);
    if (blockentity instanceof LootrChestBlockEntity chest) {
      chest.recheckOpen();
    }
  }

  @Override
  public void playerDestroy(@NonNull Level level, @NonNull Player player, @NonNull BlockPos blockPos, @NonNull BlockState blockState, @Nullable BlockEntity blockEntity, @NonNull ItemStack itemStack) {
    super.playerDestroy(level, player, blockPos, blockState, blockEntity, itemStack);
    LootrAPI.playerDestroyed(level, player, blockPos, blockEntity);
  }
}
