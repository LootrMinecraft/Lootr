package net.zestyblaze.lootr.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
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
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.zestyblaze.lootr.blocks.entities.LootrShulkerBlockEntity;
import net.zestyblaze.lootr.config.LootrModConfig;
import net.zestyblaze.lootr.registry.LootrBlockEntityInit;
import net.zestyblaze.lootr.registry.LootrItemInit;
import net.zestyblaze.lootr.util.ChestUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class LootrShulkerBlock extends ShulkerBoxBlock {
    public LootrShulkerBlock(Properties properties) {
        super(DyeColor.YELLOW, properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.UP));
    }

    @Override
    public float getExplosionResistance() {
        if (LootrModConfig.get().breaking.blast_immune) {
            return Float.MAX_VALUE;
        } else if (LootrModConfig.get().breaking.blast_resistant) {
            return 16.0f;
        } else {
            return super.getExplosionResistance();
        }
    }

    private static boolean canOpen(BlockState state, Level level, BlockPos pos, LootrShulkerBlockEntity blockEntity) {
        if (blockEntity.getAnimationStatus() != ShulkerBoxBlockEntity.AnimationStatus.CLOSED) {
            return true;
        } else {
            AABB aabb = Shulker.getProgressDeltaAabb(state.getValue(FACING), 0.0F, 0.5F).move(pos).deflate(1.0E-6D);
            return level.noCollision(aabb);
        }
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        } else if (pPlayer.isSpectator()) {
            return InteractionResult.CONSUME;
        } else {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof LootrShulkerBlockEntity lootrShulkerBox) {
                if (canOpen(pState, pLevel, pPos, lootrShulkerBox)) {
                    if (pPlayer.isShiftKeyDown()) {
                        ChestUtil.handleLootSneak(this, pLevel, pPos, pPlayer);
                    } else {
                        ChestUtil.handleLootChest(this, pLevel, pPos, pPlayer);
                    }
                    pPlayer.awardStat(Stats.OPEN_SHULKER_BOX);
                }

                return InteractionResult.CONSUME;
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);

        if (pState.is(BlockTags.GUARDED_BY_PIGLINS)) {
            PiglinAi.angerNearbyPiglins(pPlayer, false);
        }

        pLevel.gameEvent(pPlayer, GameEvent.BLOCK_DESTROY, pPos);
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
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
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
    public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
        if (LootrModConfig.get().breaking.power_comparators) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
        return new ItemStack(LootrItemInit.SHULKER);
    }

    @Override
    @Nullable
    public DyeColor getColor() {
        return DyeColor.YELLOW;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new LootrShulkerBlockEntity(LootrBlockEntityInit.SPECIAL_LOOT_SHULKER, pPos, pState);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, LootrBlockEntityInit.SPECIAL_LOOT_SHULKER, LootrShulkerBlockEntity::tick);
    }
}
