package noobanidus.mods.lootr.blocks;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
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
    ChestUtil.handleLootChestReplaced(world, pos, oldState, newState);
    super.onReplaced(oldState, world, pos, newState, isMoving);
  }

  @Override
  public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
    return ChestUtil.handleLootChest(world, pos, player);
  }

  @Nullable
  @Override
  public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
    return new SpecialLootBarrelTile();
  }

  @Override
  public void tick(BlockState state, World world, BlockPos pos, Random random) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof SpecialLootBarrelTile) {
      ((SpecialLootBarrelTile) te).barrelTick();
    }
  }

  @Override
  public boolean eventReceived(BlockState state, World world, BlockPos pos, int id, int param) {
    super.eventReceived(state, world, pos, id, param);
    TileEntity tile = world.getTileEntity(pos);
    return tile == null ? false : tile.receiveClientEvent(id, param);
  }
}
