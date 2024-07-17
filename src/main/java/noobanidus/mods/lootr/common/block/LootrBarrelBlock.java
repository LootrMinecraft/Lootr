package noobanidus.mods.lootr.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.common.block.entity.LootrBarrelBlockEntity;
import org.jetbrains.annotations.Nullable;

public class LootrBarrelBlock extends BarrelBlock {
  public static final ModelProperty<Boolean> OPENED = new ModelProperty<>();

  public LootrBarrelBlock(Properties p_49046_) {
    super(p_49046_);
  }

  @Override
  public float getExplosionResistance() {
    return LootrAPI.getExplosionResistance(this, super.getExplosionResistance());
  }

  @Override
  public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
    if (!pState.is(pNewState.getBlock())) {
      BlockEntity blockentity = pLevel.getBlockEntity(pPos);
      if (blockentity instanceof Container) {
        pLevel.updateNeighbourForOutputSignal(pPos, this);
      }

      if (pState.hasBlockEntity() && (!pState.is(pNewState.getBlock()) || !pNewState.hasBlockEntity())) {
        pLevel.removeBlockEntity(pPos);
      }
    }
  }

  @Override
  public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult trace) {
    if (level.isClientSide() || player.isSpectator() || !(player instanceof ServerPlayer serverPlayer)) {
      return InteractionResult.CONSUME;
    }
    if (serverPlayer.isShiftKeyDown()) {
      LootrAPI.handleProviderSneak(ILootrInfoProvider.of(pos, level), serverPlayer);
    } else {
      LootrAPI.handleProviderOpen(ILootrInfoProvider.of(pos, level), serverPlayer);
    }
    return InteractionResult.SUCCESS;
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new LootrBarrelBlockEntity(pos, state);
  }

  @Override
  @SuppressWarnings("deprecation")
  public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int id, int param) {
    super.triggerEvent(state, world, pos, id, param);
    BlockEntity blockEntity = world.getBlockEntity(pos);
    return blockEntity != null && blockEntity.triggerEvent(id, param);
  }

  @Override
  public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
    BlockEntity blockentity = pLevel.getBlockEntity(pPos);
    if (blockentity instanceof LootrBarrelBlockEntity barrel) {
      barrel.recheckOpen();
    }
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState pState) {
    return true;
  }

  @Override
  public float getDestroyProgress(BlockState p_60466_, Player p_60467_, BlockGetter p_60468_, BlockPos p_60469_) {
    return LootrAPI.getDestroyProgress(p_60466_, p_60467_, p_60468_, p_60469_, super.getDestroyProgress(p_60466_, p_60467_, p_60468_, p_60469_));
  }

  @Override
  public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
    return LootrAPI.getAnalogOutputSignal(pBlockState, pLevel, pPos, 0);
  }
}
