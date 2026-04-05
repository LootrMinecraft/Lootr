package noobanidus.mods.lootr.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.ILootrContainerInstance;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.LootrChestBlockEntity;
import noobanidus.mods.lootr.common.block.entity.LootrTrappedChestBlockEntity;
import org.jetbrains.annotations.Nullable;

public class LootrTrappedChestBlock extends ChestBlock {
  public LootrTrappedChestBlock(Properties properties) {
    super(LootrRegistry::getTrappedChestBlockEntity, SoundEvents.CHEST_OPEN, SoundEvents.CHEST_CLOSE, properties);
  }

  @Override
  public float getExplosionResistance() {
    return LootrAPI.getExplosionResistance(this, super.getExplosionResistance());
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return new LootrTrappedChestBlockEntity(pPos, pState);
  }

  @Override
  public boolean isSignalSource(BlockState pState) {
    return true;
  }

  @Override
  public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
    return Mth.clamp(LootrChestBlockEntity.getOpenCount(pBlockAccess, pPos), 0, 15);
  }

  @Override
  public int getDirectSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
    return pSide == Direction.UP ? pBlockState.getSignal(pBlockAccess, pPos, pSide) : 0;
  }

  @Override
  public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult trace) {
    if (level.isClientSide() || player.isSpectator() || !(player instanceof ServerPlayer serverPlayer)) {
      return InteractionResult.CONSUME;
    }
    if (serverPlayer.isShiftKeyDown()) {
      LootrAPI.handleProviderSneak(ILootrContainerInstance.of(pos, level), serverPlayer);
    } else if (!isChestBlockedAt(level, pos)) {
      LootrAPI.handleProviderOpen(ILootrContainerInstance.of(pos, level), serverPlayer);
    }
    return InteractionResult.SUCCESS;
  }

  @Override
  protected BlockState updateShape(BlockState blockState, LevelReader levelReader, ScheduledTickAccess scheduledTickAccess, BlockPos blockPos, Direction direction, BlockPos blockPos2, BlockState blockState2, RandomSource randomSource) {
    if (blockState.getValue(WATERLOGGED)) {
      scheduledTickAccess.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
    }

    return blockState;
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return LootrChestBlock.SHAPE;
  }

  @Override
  public float getDestroyProgress(BlockState p_60466_, Player p_60467_, BlockGetter p_60468_, BlockPos p_60469_) {
    return LootrAPI.getDestroyProgress(p_60466_, p_60467_, p_60468_, p_60469_, super.getDestroyProgress(p_60466_, p_60467_, p_60468_, p_60469_));
  }

  @Override
  public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos, Direction direction) {
    return LootrAPI.getAnalogOutputSignal(pBlockState, pLevel, pPos, 0, direction);
  }

  @Override
  @Nullable
  public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
    return null;
  }

  @Override
  @Nullable
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
    return ILootrBlockEntity::ticker;
  }

  @Override
  public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
    super.playerDestroy(level, player, blockPos, blockState, blockEntity, itemStack);
    LootrAPI.playerDestroyed(level, player, blockPos, blockEntity);
  }
}
