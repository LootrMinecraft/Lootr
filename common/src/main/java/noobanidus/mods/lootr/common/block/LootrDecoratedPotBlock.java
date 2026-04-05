package noobanidus.mods.lootr.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.block.entity.LootrDecoratedPotBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class LootrDecoratedPotBlock extends DecoratedPotBlock {
  private static final VoxelShape BOUNDING_BOX = Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0);

  public LootrDecoratedPotBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected void attack(@NonNull BlockState blockState, Level level, @NonNull BlockPos blockPos, @NonNull Player player) {
    BlockEntity var7 = level.getBlockEntity(blockPos);
    if (var7 instanceof LootrDecoratedPotBlockEntity decoratedPotBlockEntity) {
      decoratedPotBlockEntity.wobble(DecoratedPotBlockEntity.WobbleStyle.NEGATIVE);
      level.gameEvent(player, GameEvent.BLOCK_CHANGE, blockPos);
      // TODO: Only do this for the breaking player
      if (!level.isClientSide()) {
        decoratedPotBlockEntity.dropContent((ServerPlayer) player);
      }
    }
  }

  @Override
  public @NonNull BlockState playerWillDestroy(@NonNull Level level, @NonNull BlockPos blockPos, @NonNull BlockState blockState, @NonNull Player player) {
    this.spawnDestroyParticles(level, player, blockPos, blockState);
    if (blockState.is(BlockTags.GUARDED_BY_PIGLINS) && level instanceof ServerLevel sLevel) {
      PiglinAi.angerNearbyPiglins(sLevel, player, false);
    }

    level.gameEvent(GameEvent.BLOCK_DESTROY, blockPos, GameEvent.Context.of(player, blockState));
    return blockState;
  }

  @Override
  protected @NonNull InteractionResult useItemOn(
      @NonNull ItemStack itemStack, @NonNull BlockState blockState, @NonNull Level level, @NonNull BlockPos blockPos, @NonNull Player player, @NonNull InteractionHand interactionHand, @NonNull BlockHitResult blockHitResult
  ) {
    return useWithoutItem(blockState, level, blockPos, player, blockHitResult);
  }

  @Override
  protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState blockState, Level level, @NonNull BlockPos blockPos, @NonNull Player player, @NonNull BlockHitResult blockHitResult) {
    BlockEntity var7 = level.getBlockEntity(blockPos);
    if (var7 instanceof LootrDecoratedPotBlockEntity decoratedPotBlockEntity) {
      level.playSound(null, blockPos, SoundEvents.DECORATED_POT_INSERT_FAIL, SoundSource.BLOCKS, 1.0F, 1.0F);
      decoratedPotBlockEntity.wobble(DecoratedPotBlockEntity.WobbleStyle.NEGATIVE);
      level.gameEvent(player, GameEvent.BLOCK_CHANGE, blockPos);
      if (!level.isClientSide()) {
        decoratedPotBlockEntity.dropContent((ServerPlayer) player);
      }
      return InteractionResult.SUCCESS;
    } else {
      return InteractionResult.PASS;
    }
  }

  @Override
  protected void onProjectileHit(@NonNull Level level, @NonNull BlockState blockState, @NonNull BlockHitResult blockHitResult, @NonNull Projectile projectile) {
  }

  @Override
  protected @NonNull ItemStack getCloneItemStack(LevelReader levelReader, @NonNull BlockPos blockPos, @NonNull BlockState blockState, boolean boolValue) {
    // boolValue is "include data"
    BlockEntity var5 = levelReader.getBlockEntity(blockPos);
    return var5 instanceof LootrDecoratedPotBlockEntity decoratedPotBlockEntity
        ? decoratedPotBlockEntity.getPotAsItem()
        : super.getCloneItemStack(levelReader, blockPos, blockState, boolValue);
  }

  @Override
  protected @NonNull VoxelShape getShape(@NonNull BlockState blockState, @NonNull BlockGetter blockGetter, @NonNull BlockPos blockPos, @NonNull CollisionContext collisionContext) {
    switch (getCollisionState(blockGetter, blockPos, collisionContext)) {
      case PLAYER_OPEN, ITEM_ENTITY -> {
        return BOUNDING_BOX;
      }
      case PLAYER_CLOSED, OTHER -> {
        return super.getShape(blockState, blockGetter, blockPos, collisionContext);
      }
    }

    return super.getShape(blockState, blockGetter, blockPos, collisionContext);
  }

  @Override
  protected @NonNull VoxelShape getCollisionShape(@NonNull BlockState blockState, @NonNull BlockGetter blockGetter, @NonNull BlockPos blockPos, @NonNull CollisionContext collisionContext) {
    switch (getCollisionState(blockGetter, blockPos, collisionContext)) {
      case PLAYER_OPEN, ITEM_ENTITY -> {
        return BOUNDING_BOX;
      }
      case PLAYER_CLOSED, OTHER -> {
        return super.getShape(blockState, blockGetter, blockPos, collisionContext);
      }
    }

    return super.getShape(blockState, blockGetter, blockPos, collisionContext);
  }

  private CollisionState getCollisionState(BlockGetter getter, BlockPos pos, CollisionContext context) {
    if (!(getter.getBlockEntity(pos) instanceof LootrDecoratedPotBlockEntity potBlockEntity)) {
      return CollisionState.OTHER;
    }

    if (!(context instanceof EntityCollisionContext entityContext)) {
      return CollisionState.OTHER;
    }

    Entity entity = entityContext.getEntity();
    if (entity == null) {
      return CollisionState.OTHER;
    }

    if (entity instanceof ItemEntity) {
      return CollisionState.ITEM_ENTITY;
    }

    if (!(entity instanceof Player player)) {
      return CollisionState.OTHER;
    }

    if (player.level().isClientSide()) {
      if (potBlockEntity.hasClientOpened(player)) {
        return CollisionState.PLAYER_OPEN;
      } else {
        return CollisionState.PLAYER_CLOSED;
      }
    } else {
      if (potBlockEntity.hasVisualOpened(player)) {
        return CollisionState.PLAYER_OPEN;
      } else {
        return CollisionState.PLAYER_CLOSED;
      }
    }
  }

  enum CollisionState {
    PLAYER_OPEN,
    PLAYER_CLOSED,
    ITEM_ENTITY,
    OTHER
  }

  @Override
  protected boolean hasAnalogOutputSignal(@NonNull BlockState blockState) {
    return false;
  }

  @Override
  protected int getAnalogOutputSignal(@NonNull BlockState blockState, @NonNull Level level, @NonNull BlockPos blockPos, @NonNull Direction direction) {
    return 0;
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
    return new LootrDecoratedPotBlockEntity(blockPos, blockState);
  }

  @Override
  @Nullable
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level pLevel, @NonNull BlockState pState, @NonNull BlockEntityType<T> pBlockEntityType) {
    return ILootrBlockEntity::ticker;
  }
}
