package noobanidus.mods.lootr.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public abstract class LootrDecoratedPotBlock extends DecoratedPotBlock {
  public LootrDecoratedPotBlock(Properties properties) {
    super(properties);
  }

  @Override
  public BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
    return super.playerWillDestroy(level, blockPos, blockState, player);
  }

  @Override
  protected void onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Projectile projectile) {
    super.onProjectileHit(level, blockState, blockHitResult, projectile);
  }

  @Override
  public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
    return super.getCloneItemStack(levelReader, blockPos, blockState);
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
  public abstract @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState);
}
