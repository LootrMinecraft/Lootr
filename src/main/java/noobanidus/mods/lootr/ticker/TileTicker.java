package noobanidus.mods.lootr.ticker;

import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.ILootTile;
import noobanidus.mods.lootr.config.ConfigManager;

import java.util.*;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class TileTicker {
  private final static Object listLock = new Object();
  private final static Object worldLock = new Object();
  private static boolean tickingList = false;
  private final static Set<Entry> tileEntries = new LinkedHashSet<>();
  private final static Set<Entry> pendingEntries = new LinkedHashSet<>();

  public static void addEntry(RegistryKey<World> dimension, BlockPos position) {
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
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        for (Entry entry : copy) {
          ServerWorld level = server.getLevel(entry.getDimension());
          if (level == null) {
            throw new IllegalStateException("got a null world for tile ticker in dimension " + entry.getDimension() + " at " + entry.getPosition());
          }
          ServerChunkProvider provider = level.getChunkSource();
          ChunkPos pos = entry.getChunkPosition();
          Chunk chunk = (Chunk) provider.getChunk(pos.x, pos.z, ChunkStatus.FULL, false);
          if (chunk != null) {
            TileEntity tile = level.getBlockEntity(entry.getPosition());
            if (!(tile instanceof LockableLootTileEntity) || tile instanceof ILootTile) {
              toRemove.add(entry);
              continue;
            }
            LockableLootTileEntity te = (LockableLootTileEntity) tile;
            if (te.lootTable == null || ConfigManager.getLootBlacklist().contains(te.lootTable)) {
              toRemove.add(entry);
              continue;
            }
            ResourceLocation table = te.lootTable;
            long seed = te.lootTableSeed;
            BlockState stateAt = level.getBlockState(entry.getPosition());
            BlockState replacement = ConfigManager.replacement(stateAt);
            if (replacement == null) {
              toRemove.add(entry);
              continue;
            }
            chunk.pendingBlockEntities.remove(entry.getPosition());
            level.removeBlockEntity(entry.getPosition());
            level.destroyBlock(entry.getPosition(), false);
            level.setBlock(entry.getPosition(), replacement, 2);
            tile = level.getBlockEntity(entry.getPosition());
            if (tile instanceof ILootTile) {
              ((LockableLootTileEntity) tile).setLootTable(table, seed);
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
    private final RegistryKey<World> dimension;
    private final BlockPos position;
    private final ChunkPos chunkPos;

    public Entry(RegistryKey<World> dimension, BlockPos position) {
      this.dimension = dimension;
      this.position = position;
      this.chunkPos = new ChunkPos(position);
    }

    public RegistryKey<World> getDimension() {
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
