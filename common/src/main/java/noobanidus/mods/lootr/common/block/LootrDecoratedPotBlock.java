package noobanidus.mods.lootr.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import noobanidus.mods.lootr.common.block.entity.LootrDecoratedPotBlockEntity;
import org.jetbrains.annotations.Nullable;

public class LootrDecoratedPotBlock extends DecoratedPotBlock {
  public LootrDecoratedPotBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected void attack(BlockState blockState, Level level, BlockPos blockPos, Player player) {
    super.attack(blockState, level, blockPos, player);
  }

  @Override
  public BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
    return super.playerWillDestroy(level, blockPos, blockState, player);
  }

  @Override
  protected ItemInteractionResult useItemOn(
      ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult
  ) {
    BlockEntity itemStack2 = level.getBlockEntity(blockPos);
    if (itemStack2 instanceof LootrDecoratedPotBlockEntity decoratedPotBlockEntity) {
      if (level.isClientSide) {
        return ItemInteractionResult.CONSUME;
      } else {
        ItemStack itemStack2x = decoratedPotBlockEntity.getTheItem();
        if (!itemStack.isEmpty()
            && (itemStack2x.isEmpty() || ItemStack.isSameItemSameComponents(itemStack2x, itemStack) && itemStack2x.getCount() < itemStack2x.getMaxStackSize())) {
          decoratedPotBlockEntity.wobble(DecoratedPotBlockEntity.WobbleStyle.POSITIVE);
          player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
          ItemStack itemStack3 = itemStack.consumeAndReturn(1, player);
          float f;
          if (decoratedPotBlockEntity.isEmpty()) {
            decoratedPotBlockEntity.setTheItem(itemStack3);
            f = (float)itemStack3.getCount() / (float)itemStack3.getMaxStackSize();
          } else {
            itemStack2x.grow(1);
            f = (float)itemStack2x.getCount() / (float)itemStack2x.getMaxStackSize();
          }

          level.playSound(null, blockPos, SoundEvents.DECORATED_POT_INSERT, SoundSource.BLOCKS, 1.0F, 0.7F + 0.5F * f);
          if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                ParticleTypes.DUST_PLUME, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 1.2, (double)blockPos.getZ() + 0.5, 7, 0.0, 0.0, 0.0, 0.0
            );
          }

          decoratedPotBlockEntity.setChanged();
          level.gameEvent(player, GameEvent.BLOCK_CHANGE, blockPos);
          return ItemInteractionResult.SUCCESS;
        } else {
          return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
      }
    } else {
      return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
    BlockEntity var7 = level.getBlockEntity(blockPos);
    if (var7 instanceof LootrDecoratedPotBlockEntity decoratedPotBlockEntity) {
      level.playSound(null, blockPos, SoundEvents.DECORATED_POT_INSERT_FAIL, SoundSource.BLOCKS, 1.0F, 1.0F);
      decoratedPotBlockEntity.wobble(DecoratedPotBlockEntity.WobbleStyle.NEGATIVE);
      level.gameEvent(player, GameEvent.BLOCK_CHANGE, blockPos);
      return InteractionResult.SUCCESS;
    } else {
      return InteractionResult.PASS;
    }
  }

  @Override
  protected void onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Projectile projectile) {
    super.onProjectileHit(level, blockState, blockHitResult, projectile);
  }

  @Override
  public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
    BlockEntity var5 = levelReader.getBlockEntity(blockPos);
    return var5 instanceof LootrDecoratedPotBlockEntity decoratedPotBlockEntity
        ? decoratedPotBlockEntity.getPotAsItem()
        : super.getCloneItemStack(levelReader, blockPos, blockState);
  }

  @Override
  protected boolean hasAnalogOutputSignal(BlockState blockState) {
    return super.hasAnalogOutputSignal(blockState);
  }

  @Override
  protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
    return super.getAnalogOutputSignal(blockState, level, blockPos);
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new LootrDecoratedPotBlockEntity(blockPos, blockState);
  }
}
