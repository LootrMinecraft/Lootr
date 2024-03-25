package noobanidus.mods.lootr.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import noobanidus.mods.lootr.block.entities.LootrChestBlockEntity;
import noobanidus.mods.lootr.block.entities.LootrTrappedChestBlockEntity;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModBlockEntities;
import noobanidus.mods.lootr.util.ChestUtil;

import javax.annotation.Nullable;

public class LootrTrappedChestBlock extends ChestBlock {
  public LootrTrappedChestBlock(Properties properties) {
    super(properties, () -> ModBlockEntities.SPECIAL_TRAPPED_LOOT_CHEST);
  }

  @Override
  public float getExplosionResistance() {
    if (ConfigManager.BLAST_IMMUNE.get()) {
      return Float.MAX_VALUE;
    } else if (ConfigManager.BLAST_RESISTANT.get()) {
      return 16.0f;
    } else {
      return super.getExplosionResistance();
    }
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
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
    if (player.isShiftKeyDown()) {
      ChestUtil.handleLootSneak(this, world, pos, player);
    } else if (!ChestBlock.isChestBlockedAt(world, pos)) {
      ChestUtil.handleLootChest(this, world, pos, player);
    }
    return InteractionResult.SUCCESS;
  }

  @Override
  public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
    if (stateIn.getValue(WATERLOGGED)) {
      worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
    }

    return stateIn;
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return AABB;
  }

  @Override
  public float getDestroyProgress(BlockState p_60466_, Player p_60467_, BlockGetter p_60468_, BlockPos p_60469_) {
    if (ConfigManager.DISABLE_BREAK.get()) {
      return 0f;
    }
    return super.getDestroyProgress(p_60466_, p_60467_, p_60468_, p_60469_);
  }

  @Override
  @Nullable
  public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
    return null;
  }

  @Override
  @Nullable
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
    return pLevel.isClientSide ? LootrChestBlockEntity::lootrLidAnimateTick : null;
  }
}
