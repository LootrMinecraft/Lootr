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

public class LootrDecoratedPotBlock extends DecoratedPotBlock {
  private static final VoxelShape BOUNDING_BOX = Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0);

  public LootrDecoratedPotBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected void attack(BlockState blockState, Level level, BlockPos blockPos, Player player) {
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
  public BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
    this.spawnDestroyParticles(level, player, blockPos, blockState);
    if (blockState.is(BlockTags.GUARDED_BY_PIGLINS) && level instanceof ServerLevel sLevel) {
      PiglinAi.angerNearbyPiglins(sLevel, player, false);
    }

    level.gameEvent(GameEvent.BLOCK_DESTROY, blockPos, GameEvent.Context.of(player, blockState));
    return blockState;
  }

  @Override
  protected InteractionResult useItemOn(
      ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult
  ) {
    return useWithoutItem(blockState, level, blockPos, player, blockHitResult);
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
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
  protected void onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Projectile projectile) {
  }

  @Override
  protected ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean boolValue) {
    // TODO: What is boolValue?
    BlockEntity var5 = levelReader.getBlockEntity(blockPos);
    return var5 instanceof LootrDecoratedPotBlockEntity decoratedPotBlockEntity
        ? decoratedPotBlockEntity.getPotAsItem()
        : super.getCloneItemStack(levelReader, blockPos, blockState, boolValue);
  }

  @Override
  protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
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
  protected VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
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

  // TODO:
  @Override
  protected boolean hasAnalogOutputSignal(BlockState blockState) {
    return super.hasAnalogOutputSignal(blockState);
  }

  // TODO:
  @Override
  protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos, Direction direction) {
    return super.getAnalogOutputSignal(blockState, level, blockPos, direction);
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new LootrDecoratedPotBlockEntity(blockPos, blockState);
  }

  @Override
  @Nullable
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
    return ILootrBlockEntity::ticker;
  }
}
