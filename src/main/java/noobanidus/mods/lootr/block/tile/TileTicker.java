package noobanidus.mods.lootr.block.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.server.FMLServerHandler;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.tile.ILootTile;
import noobanidus.mods.lootr.config.ConfigManager;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class TileTicker {
  private final static Object listLock = new Object();
  private final static Object worldLock = new Object();
  private static boolean tickingList = false;
  private final static Set<Entry> tileEntries = new LinkedHashSet<>();
  private final static Set<Entry> pendingEntries = new LinkedHashSet<>();

  public static void addEntry(DimensionType dimension, BlockPos position) {
    if (ConfigManager.isDimensionBlocked(dimension)) {
      return;
    }
    Entry newEntry = new Entry(dimension, position);
    synchronized (listLock) {
      if (tickingList) {
        pendingEntries.add(newEntry);
      } else {
        tileEntries.add(newEntry);
      }
    }
  }

  @SubscribeEvent
  public static void serverTick(TickEvent.ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      Set<Entry> toRemove = new HashSet<>();
      Set<Entry> copy;
      synchronized (listLock) {
        tickingList = true;
        copy = new HashSet<>(tileEntries);
        tickingList = false;
      }
      synchronized (worldLock) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if(server == null)
          return;
        for (Entry entry : copy) {
          WorldServer level = server.getWorld(entry.getDimension().getId());
          if (level == null) {
            toRemove.add(entry);
            continue;
          }
          ChunkProviderServer provider = level.getChunkProvider();
          ChunkPos pos = entry.getChunkPosition();
          Chunk chunk = (Chunk) provider.getLoadedChunk(pos.x, pos.z);
          if (chunk != null) {
            TileEntity tile = level.getTileEntity(entry.getPosition());
            if (!(tile instanceof TileEntityLockableLoot) || tile instanceof ILootTile) {
              toRemove.add(entry);
              continue;
            }
            TileEntityLockableLoot te = (TileEntityLockableLoot) tile;
            if (te.lootTable == null || ConfigManager.isBlacklisted(te.lootTable)) {
              toRemove.add(entry);
              continue;
            }
            ResourceLocation table = te.lootTable;
            long seed = te.lootTableSeed;
            IBlockState stateAt = level.getBlockState(entry.getPosition());
            IBlockState replacement = ConfigManager.replacement(stateAt);
            if (replacement == null) {
              toRemove.add(entry);
              continue;
            }
            level.removeTileEntity(entry.getPosition());
            level.setBlockState(entry.getPosition(), replacement, 2);
            tile = level.getTileEntity(entry.getPosition());
            if (tile instanceof ILootTile) {
              ((TileEntityLockableLoot) tile).setLootTable(table, seed);
            } else {
              Lootr.LOG.error("replacement " + replacement + " is not an ILootTile " + entry.getDimension() + " at " + entry.getPosition());
            }

            toRemove.add(entry);
          }
        }
      }
      synchronized (listLock) {
        tickingList = true;
        tileEntries.removeAll(toRemove);
        tileEntries.addAll(pendingEntries);
        tickingList = false;
        pendingEntries.clear();
      }
    }
  }

  public static class Entry {
    private final DimensionType dimension;
    private final BlockPos position;
    private final ChunkPos chunkPos;

    public Entry(DimensionType dimension, BlockPos position) {
      if(dimension == null)
        throw new IllegalArgumentException();
      this.dimension = dimension;
      this.position = position;
      this.chunkPos = new ChunkPos(position);
    }

    public DimensionType getDimension() {
      return dimension;
    }

    public BlockPos getPosition() {
      return position;
    }

    public ChunkPos getChunkPosition() {
      return chunkPos;
    }
  }
}
