package noobanidus.mods.lootr.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.data.NewChestData;
import noobanidus.mods.lootr.tiles.ILootTile;

import javax.annotation.Nullable;
import java.util.Random;

@SuppressWarnings("unused")
public class ChestUtil {
  public static Random random = new Random();

/*  public static IInventory getInventory(BlockState state, World world, BlockPos pos, boolean allowBlocked) {
    return ChestBlock.func_226916_a_(state.getBlock(), state, world, pos, allowBlocked, ChestBlock.field_220109_i);
  }*/

/*  public static INamedContainerProvider getContainer(BlockState state, World world, BlockPos pos) {
    return ChestBlock.getChestInventory(state, world, pos, false, ChestBlock.field_220110_j);
  }*/

  @Nullable
  public static INamedContainerProvider getLootContainer(IWorld world, BlockPos pos, ServerPlayerEntity player) {
    return NewChestData.getInventory(world, pos, player);
  }

  public static boolean handleLootChest(World world, BlockPos pos, PlayerEntity player) {
    if (world.isRemote()) {
      return false;
    }
    INamedContainerProvider provider = ChestUtil.getLootContainer(world, pos, (ServerPlayerEntity) player);
    player.openContainer(provider);
    return true;
  }

  public static void setLootTable(LockableLootTileEntity tile, ResourceLocation table) {
    long seed = random.nextLong();
    if (!(tile instanceof ILootTile)) {
      DimensionType dim = null;
      if (tile.getWorld() != null) {
        dim = tile.getWorld().getDimension().getType();
      }
      TickManager.addTicker(tile, tile.getPos(), dim, table, seed);
      Lootr.LOG.debug("Added a ticker for: " + tile + " at " + tile.getPos() + " in " + (dim == null ? "null" : dim) + " with table " + table);
    } else {
      ILootTile te = (ILootTile) tile;
      te.setTable(table);
      te.setSeed(seed);
      Lootr.LOG.debug("Successfully set loot table of tile entity at " + tile.getPos() + " to " + table);
    }
  }

  public static void setLootTableStatic (IBlockReader reader, Random random, BlockPos pos, ResourceLocation location) {

  }
}
