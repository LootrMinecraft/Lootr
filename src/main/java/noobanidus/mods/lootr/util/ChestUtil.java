package noobanidus.mods.lootr.util;

import com.google.common.collect.Sets;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.Constants;
import noobanidus.mods.lootr.data.NewChestData;
import noobanidus.mods.lootr.events.HandleBreak;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.tiles.ILootTile;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@SuppressWarnings("unused")
public class ChestUtil {
  public static Random random = new Random();
  public static Set<Class<?>> tileClasses = new HashSet<>();

  public static boolean handleLootChest(World world, BlockPos pos, PlayerEntity player) {
    if (world.isRemote()) {
      return false;
    }
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof ILootTile) {
      INamedContainerProvider provider = NewChestData.getInventory(world, pos, (ServerPlayerEntity) player, ((ILootTile)te)::fillWithLoot);
      player.openContainer(provider);
      return true;
    } else {
      return false;
    }
  }

  public static void setLootTable(LockableLootTileEntity tile, ResourceLocation table) {
    long seed = random.nextLong();
    if (table == null) {
      tile.lootTable = null;
      tile.lootTableSeed = -1;
      return;
    } else {
      tile.lootTable = table;
      tile.lootTableSeed = seed;
    }
    if (isTileClass(tile)) {
      addNewTile(tile, table, seed);
      //Lootr.LOG.debug("(setLootTable) Added a ticker for: " + tile + " at " + tile.getPos() + " in " + (dim == null ? "null" : dim) + " with table " + table);
    } else if (tile instanceof ILootTile) {
      ILootTile te = (ILootTile) tile;
      te.setTable(table);
      te.setSeed(seed);
      TickManager.trackTile(tile, table);
      //Lootr.LOG.debug("Successfully set loot table of tile entity at " + tile.getPos() + " to " + table);
    }
  }

  public static void setLootTableStatic(IBlockReader reader, Random random, BlockPos pos, ResourceLocation location) {
    TileEntity te = reader.getTileEntity(pos);
    if (isTileClass(te)) {
      if (reader instanceof IWorld) {
        IWorld writer = (IWorld) reader;
        BlockState stateAt = reader.getBlockState(pos);

        if (hasReplacement(stateAt) && isTileClass(te)) {
          BlockState state = getReplacement(stateAt.getBlock(), stateAt);
          IChunk chunk = writer.getChunk(pos);
          chunk.removeTileEntity(pos);
          writer.setBlockState(pos, state, 2);
          te = reader.getTileEntity(pos);
          if (te instanceof ILootTile) {
            ((ILootTile) te).setTable(location);
            ((ILootTile) te).setSeed(random.nextLong());
            TickManager.trackTile(te, location, writer.getDimension().getType());
          }
        }
      }
    } else {
      if (te instanceof LockableLootTileEntity) {
        ((LockableLootTileEntity) te).lootTable = location;
        ((LockableLootTileEntity) te).lootTableSeed = random.nextLong();
      }
    }
  }

  public static boolean checkLootAndRead(LockableLootTileEntity tile, CompoundNBT tag) {
    if (tile instanceof ILootTile) {
      return false;
    }

    if (tag.contains("Items", Constants.NBT.TAG_LIST)) {
      ListNBT items = tag.getList("Items", Constants.NBT.TAG_COMPOUND);
      if (items.size() > 0) {
        return false;
      }
    }

    if (tag.contains("LootTable", Constants.NBT.TAG_STRING)) {
      ResourceLocation table = new ResourceLocation(tag.getString("LootTable"));
      long seed = tag.getLong("LootTableSeed");
      tile.lootTableSeed = seed;
      tile.lootTable = table;
      if (isTileClass(tile)) {
        addNewTile(tile, table, seed);
      }
      return true;
      //Lootr.LOG.debug("(checkLootAndRead) Added a ticker for: " + tile + " at " + tile.getPos() + " in " + (dim == null ? "null" : dim) + " with table " + table);
    }

    return false;
  }

  private static void addNewTile(LockableLootTileEntity tile, ResourceLocation table, long seed) {
    DimensionType dim = null;
    if (tile.getWorld() != null) {
      dim = tile.getWorld().getDimension().getType();
    }
    TickManager.addTicker(tile, tile.getPos(), dim, table, seed);
  }

  private static Set<Block> replacements = Sets.newHashSet(Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.BARREL);

  public static boolean hasReplacement(BlockState state) {
    return hasReplacement(state.getBlock());
  }

  public static boolean hasReplacement(Block block) {
    return replacements.contains(block);
  }

  public static BlockState getReplacement(Block block, BlockState state) {
    if (!HandleBreak.specialLootChests.contains(block)) {
      if (block == Blocks.CHEST) {
        return ModBlocks.CHEST.getDefaultState().with(ChestBlock.FACING, state.get(ChestBlock.FACING)).with(ChestBlock.WATERLOGGED, state.get(ChestBlock.WATERLOGGED));
      } else if (block == Blocks.TRAPPED_CHEST) {
        return ModBlocks.TRAPPED_CHEST.getDefaultState().with(ChestBlock.FACING, state.get(ChestBlock.FACING)).with(ChestBlock.WATERLOGGED, state.get(ChestBlock.WATERLOGGED));
      } else if (block == Blocks.BARREL) {
        return ModBlocks.BARREL.getDefaultState().with(BarrelBlock.PROPERTY_FACING, state.get(BarrelBlock.PROPERTY_FACING)).with(BarrelBlock.PROPERTY_OPEN, state.get(BarrelBlock.PROPERTY_OPEN));
      }
    }

    return state;
  }

  static {
    ChestUtil.tileClasses.add(ChestTileEntity.class);
    ChestUtil.tileClasses.add(BarrelTileEntity.class);
    ChestUtil.tileClasses.add(TrappedChestTileEntity.class);
  }

  public static boolean isTileClass(TileEntity te) {
    return tileClasses.contains(te.getClass());
  }
}
