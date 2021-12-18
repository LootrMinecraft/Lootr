package noobanidus.mods.lootr.blocks;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import noobanidus.mods.lootr.data.NewChestData;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;
import noobanidus.mods.lootr.tiles.SpecialTrappedLootChestTile;
import noobanidus.mods.lootr.util.ChestUtil;

import javax.annotation.Nullable;

@SuppressWarnings({"NullableProblems"})
public class LootrTrappedChestBlock extends TrappedChestBlock {
  public LootrTrappedChestBlock(Properties properties) {
    super(properties);
  }

  @Override
  public BlockEntity newBlockEntity(BlockGetter p_196283_1_) {
    return new SpecialTrappedLootChestTile();
  }

  @Override
  public void onRemove(BlockState oldState, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
    if (oldState.getBlock() != newState.getBlock() && world instanceof ServerLevel) {
      NewChestData.deleteLootChest((ServerLevel) world, pos);
    }
    super.onRemove(oldState, world, pos, newState, isMoving);
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
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
    if (stateIn.getValue(WATERLOGGED)) {
      worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
    }

    return stateIn;
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return AABB;
  }

  @Override
  @Nullable
  public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
    return null;
  }

  @SuppressWarnings("deprecation")
  @Override
  public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
    return Mth.clamp(SpecialLootChestTile.getPlayersUsing(blockAccess, pos), 0, 15);
  }
}
