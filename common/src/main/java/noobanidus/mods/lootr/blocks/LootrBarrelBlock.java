package noobanidus.mods.lootr.blocks;

import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.client.model.data.ModelProperty;
import noobanidus.mods.lootr.data.NewChestData;
import noobanidus.mods.lootr.tiles.SpecialLootBarrelTile;
import noobanidus.mods.lootr.util.ChestUtil;

import javax.annotation.Nullable;
import java.util.Random;

public class LootrBarrelBlock extends BarrelBlock {
  public static final ModelProperty<Boolean> OPENED = new ModelProperty<>();

  public LootrBarrelBlock(Properties properties) {
    super(properties);
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
    } else {
      ChestUtil.handleLootChest(this, world, pos, player);
    }
    return InteractionResult.SUCCESS;
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockGetter p_196283_1_) {
    return new SpecialLootBarrelTile();
  }

  @Override
  public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
    BlockEntity te = world.getBlockEntity(pos);
    if (te instanceof SpecialLootBarrelTile) {
      ((SpecialLootBarrelTile) te).recheckOpen();
    }
  }

  @Override
  @SuppressWarnings("deprecation")
  public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int id, int param) {
    super.triggerEvent(state, world, pos, id, param);
    BlockEntity tile = world.getBlockEntity(pos);
    return tile != null && tile.triggerEvent(id, param);
  }
}
