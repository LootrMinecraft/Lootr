package noobanidus.mods.lootr.blocks;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import noobanidus.mods.lootr.data.NewChestData;
import noobanidus.mods.lootr.tiles.SpecialLootBarrelTile;
import noobanidus.mods.lootr.util.ChestUtil;

import javax.annotation.Nullable;
import java.util.Random;

public class LootrBarrelBlock extends BarrelBlock {
  public LootrBarrelBlock(Properties properties) {
    super(properties);
  }

  @Override
  public void onReplaced(BlockState oldState, World world, BlockPos pos, BlockState newState, boolean isMoving) {
    NewChestData.deleteLootChest(world, pos);
    super.onReplaced(oldState, world, pos, newState, isMoving);
  }

  @Override
  public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
    ChestUtil.handleLootChest(world, pos, player);
    return ActionResultType.SUCCESS;
  }

  @Nullable
  @Override
  public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
    return new SpecialLootBarrelTile();
  }

  @Override
  public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof SpecialLootBarrelTile) {
      ((SpecialLootBarrelTile) te).barrelTick();
    }
  }

  @Override
  public boolean eventReceived(BlockState state, World world, BlockPos pos, int id, int param) {
    super.eventReceived(state, world, pos, id, param);
    TileEntity tile = world.getTileEntity(pos);
    return tile != null && tile.receiveClientEvent(id, param);
  }

  @Override
  public String getTranslationKey() {
    return Blocks.BARREL.getTranslationKey();
  }
}
