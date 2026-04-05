package noobanidus.mods.lootr.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.ILootrContainerInstance;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.block.entity.LootrBarrelBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class LootrBarrelBlock extends BarrelBlock {
  public LootrBarrelBlock(Properties p_49046_) {
    super(p_49046_);
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
    } else {
      LootrAPI.handleInstanceOpen(ILootrContainerInstance.of(pos, level), serverPlayer);
    }
    return InteractionResult.SUCCESS;
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
    return new LootrBarrelBlockEntity(pos, state);
  }

  @Override
  @SuppressWarnings("deprecation")
  public boolean triggerEvent(@NonNull BlockState state, @NonNull Level world, @NonNull BlockPos pos, int id, int param) {
    super.triggerEvent(state, world, pos, id, param);
    BlockEntity blockEntity = world.getBlockEntity(pos);
    return blockEntity != null && blockEntity.triggerEvent(id, param);
  }

  @Override
  @Nullable
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level pLevel, @NonNull BlockState pState, @NonNull BlockEntityType<T> pBlockEntityType) {
    return ILootrBlockEntity::ticker;
  }

  @Override
  public void tick(@NonNull BlockState pState, ServerLevel pLevel, @NonNull BlockPos pPos, @NonNull RandomSource pRandom) {
    BlockEntity blockentity = pLevel.getBlockEntity(pPos);
    if (blockentity instanceof LootrBarrelBlockEntity barrel) {
      barrel.recheckOpen();
    }
  }

  @Override
  public boolean hasAnalogOutputSignal(@NonNull BlockState pState) {
    return true;
  }

  @Override
  public float getDestroyProgress(@NonNull BlockState pBlockState, @NonNull Player pPlayer, @NonNull BlockGetter pLevel, @NonNull BlockPos pPos) {
    return LootrAPI.getDestroyProgress(pBlockState, pPlayer, pLevel, pPos, super.getDestroyProgress(pBlockState, pPlayer, pLevel, pPos));
  }

  @Override
  public int getAnalogOutputSignal(@NonNull BlockState pBlockState, @NonNull Level pLevel, @NonNull BlockPos pPos, @NonNull Direction direction) {
    return LootrAPI.getAnalogOutputSignal(pBlockState, pLevel, pPos, 0, direction);
  }

  @Override
  public void playerDestroy(@NonNull Level level, @NonNull Player player, @NonNull BlockPos blockPos, @NonNull BlockState blockState, @Nullable BlockEntity blockEntity, @NonNull ItemStack itemStack) {
    super.playerDestroy(level, player, blockPos, blockState, blockEntity, itemStack);
    LootrAPI.playerDestroyed(level, player, blockPos, blockEntity);
  }
}
