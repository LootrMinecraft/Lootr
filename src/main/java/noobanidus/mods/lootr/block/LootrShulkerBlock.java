package noobanidus.mods.lootr.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.registry.LootrRegistry;
import noobanidus.mods.lootr.block.entity.LootrShulkerBlockEntity;
import noobanidus.mods.lootr.util.ChestUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LootrShulkerBlock extends ShulkerBoxBlock {
  public LootrShulkerBlock(Properties pProperties) {
    super(DyeColor.YELLOW, pProperties);
    this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.UP));
  }

  private static boolean canOpen(BlockState pState, Level pLevel, BlockPos pPos, LootrShulkerBlockEntity pBlockEntity) {
    if (pBlockEntity.getAnimationStatus() != ShulkerBoxBlockEntity.AnimationStatus.CLOSED) {
      return true;
    } else {
      AABB aabb = Shulker.getProgressDeltaAabb(1.0f, pState.getValue(FACING), 0.0F, 0.5F).move(pPos).deflate(1.0E-6D);
      return pLevel.noCollision(aabb);
    }
  }

  @Override
  public float getExplosionResistance() {
    return LootrAPI.getExplosionResistance(this, super.getExplosionResistance());
  }

  @Override
  public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult trace) {
    if (level.isClientSide() || player.isSpectator() || !(player instanceof ServerPlayer serverPlayer)) {
      return InteractionResult.CONSUME;
    }
    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (!(blockEntity instanceof LootrShulkerBlockEntity shulkerboxblockentity)) {
      return InteractionResult.PASS;
    }
    if (!canOpen(state, level, pos, shulkerboxblockentity)) {
      return InteractionResult.PASS;
    }
    if (serverPlayer.isShiftKeyDown()) {
      ChestUtil.handleLootSneak(this, level, pos, serverPlayer);
    } else {
      ChestUtil.handleLootChest(this, level, pos, serverPlayer);
      player.awardStat(Stats.OPEN_SHULKER_BOX);
    }
    return InteractionResult.SUCCESS;
  }

  @Override
  public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
    this.spawnDestroyParticles(pLevel, pPlayer, pPos, pState);
    if (pState.is(BlockTags.GUARDED_BY_PIGLINS)) {
      PiglinAi.angerNearbyPiglins(pPlayer, false);
    }

    pLevel.gameEvent(pPlayer, GameEvent.BLOCK_DESTROY, pPos);

    return pState;
  }

  @Override
  public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
    if (!pState.is(pNewState.getBlock())) {
      BlockEntity blockentity = pLevel.getBlockEntity(pPos);
      if (blockentity instanceof LootrShulkerBlockEntity) {
        pLevel.updateNeighbourForOutputSignal(pPos, pState.getBlock());
      }

      if (pState.hasBlockEntity() && (!pState.is(pNewState.getBlock()) || !pNewState.hasBlockEntity())) {
        pLevel.removeBlockEntity(pPos);
      }
    }
  }

  @Override
  public void appendHoverText(ItemStack p_56193_, Item.TooltipContext p_339693_, List<Component> p_56195_, TooltipFlag p_56196_) {
  }

  @Override
  public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    BlockEntity blockentity = pLevel.getBlockEntity(pPos);
    return blockentity instanceof LootrShulkerBlockEntity ? Shapes.create(((LootrShulkerBlockEntity) blockentity).getBoundingBox(pState)) : Shapes.block();
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState pState) {
    return true;
  }

  @Override
  @Nullable
  public DyeColor getColor() {
    return DyeColor.YELLOW;
  }

  @Override
  public float getDestroyProgress(BlockState p_60466_, Player p_60467_, BlockGetter p_60468_, BlockPos p_60469_) {
    return LootrAPI.getDestroyProgress(p_60466_, p_60467_, p_60468_, p_60469_, super.getDestroyProgress(p_60466_, p_60467_, p_60468_, p_60469_));
  }

  @Override
  public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
    return LootrAPI.getAnalogOutputSignal(pBlockState, pLevel, pPos, 0);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return new LootrShulkerBlockEntity(pPos, pState);
  }

  @Override
  @Nullable
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
    return createTickerHelper(pBlockEntityType, (BlockEntityType<LootrShulkerBlockEntity>) LootrRegistry.getShulkerBlockEntity(), LootrShulkerBlockEntity::tick);
  }
}
