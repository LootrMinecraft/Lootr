package noobanidus.mods.lootr.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import noobanidus.mods.lootr.blocks.entities.LootrBarrelBlockEntity;
import noobanidus.mods.lootr.config.LootrModConfig;
import noobanidus.mods.lootr.util.ChestUtil;

public class LootrBarrelBlock extends BarrelBlock {
    public LootrBarrelBlock(Properties p_49046_) {
        super(p_49046_);
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
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
        if (player.isShiftKeyDown()) {
            ChestUtil.handleLootSneak(this, world, pos, player);
        } else {
            ChestUtil.handleLootChest(this, world, pos, player);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LootrBarrelBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int id, int param) {
        super.triggerEvent(state, world, pos, id, param);
        BlockEntity tile = world.getBlockEntity(pos);
        return tile != null && tile.triggerEvent(id, param);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource source) {
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
    public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
        if (LootrModConfig.get().breaking.power_comparators) {
            return 1;
        } else {
            return 0;
        }
    }
}
