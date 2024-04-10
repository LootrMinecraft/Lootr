package noobanidus.mods.lootr.block.entities;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.event.HandleChunk;
import noobanidus.mods.lootr.util.ServerAccessImpl;

import java.util.HashSet;
import java.util.Set;

public class TileTicker {
  private final static Object listLock = new Object();
  private final static Object worldLock = new Object();
  private final static Set<Entry> tileEntries = new ObjectLinkedOpenHashSet<>();
  private final static Set<Entry> pendingEntries = new ObjectLinkedOpenHashSet<>();
  private static boolean tickingList = false;

  public static void addEntry(Level level, BlockPos position) {
    if (ConfigManager.get().conversion.disable) {
      return;
    }

    if (ServerAccessImpl.getServer() == null) {
      return;
    }

    ResourceKey<Level> dimension = level.dimension();
    if (ConfigManager.isDimensionBlacklisted(dimension)) {
      return;
    }

    ChunkPos chunkPos = new ChunkPos(position);

    WorldBorder border = level.getWorldBorder();

    Set<ChunkPos> chunks = new ObjectLinkedOpenHashSet<>();
    chunks.add(chunkPos);

    int oX = chunkPos.x;
    int oZ = chunkPos.z;
    chunks.add(chunkPos);

    for (int x = -2; x <= 2; x++) {
      for (int z = -2; z <= 2; z++) {
        ChunkPos newPos = new ChunkPos(oX + x, oZ + z);
        // This has the potential to force-load chunks on the main thread
        // by ignoring the loading state of chunks outside the world border.
        if (ConfigManager.get().conversion.world_border && !border.isWithinBounds(newPos)) {
          continue;
        }

        chunks.add(newPos);
      }
    }

    Entry newEntry = new Entry(dimension, position, chunks, ServerAccessImpl.getServer().getTickCount());
    synchronized (listLock) {
      if (tickingList) {
        pendingEntries.add(newEntry);
      } else {
        tileEntries.add(newEntry);
      }
    }
  }

  public static void serverTick() {
    if (ConfigManager.get().conversion.disable) {
      return;
    }
    Set<Entry> toRemove = new HashSet<>();
    Set<Entry> copy;
    synchronized (listLock) {
      tickingList = true;
      copy = new ObjectLinkedOpenHashSet<>(tileEntries);
      tickingList = false;
    }
    synchronized (worldLock) {
      MinecraftServer server = ServerAccessImpl.getServer();
      for (Entry entry : copy) {
        if (entry.age(server) > ConfigManager.get().conversion.max_entry_age) {
          toRemove.add(entry);
          continue;
        }
        ServerLevel level = server.getLevel(entry.getDimension());
        if (level == null) {
          toRemove.add(entry);
          continue;
        }
        if (!level.getChunkSource().hasChunk(entry.getPosition().getX() >> 4, entry.getPosition().getZ() >> 4)) {
          continue;
        }
        boolean skip = false;
        for (ChunkPos chunkPos : entry.getChunkPositions()) {
          if (!level.getChunkSource().hasChunk(chunkPos.x, chunkPos.z)) {
            skip = true;
            break;
          }
        }
        synchronized (HandleChunk.LOADED_CHUNKS) {
          Set<ChunkPos> loadedChunks = HandleChunk.LOADED_CHUNKS.get(entry.dimension);
          if (loadedChunks != null) {
            for (ChunkPos chunkPos : entry.getChunkPositions()) {
              if (!loadedChunks.contains(chunkPos)) {
                skip = true;
                break;
              }
            }
          }
        }
        if (skip) {
          continue;
        }
        BlockEntity blockEntity = level.getBlockEntity(entry.getPosition());
        if (!(blockEntity instanceof RandomizableContainerBlockEntity be) || blockEntity instanceof ILootBlockEntity) {
          toRemove.add(entry);
          continue;
        }
        if (be.lootTable == null || ConfigManager.isBlacklisted(be.lootTable)) {
          toRemove.add(entry);
          continue;
        }
        BlockState stateAt = level.getBlockState(entry.getPosition());
        BlockState replacement = ConfigManager.replacement(stateAt);
        if (replacement == null) {
          toRemove.add(entry);
          continue;
        }
        // Set loot table to null to prevent items dropping
        // Don't use Clearable.tryClear because otherwise some
        // chests that generate maps will cause massive amounts
        // of lag.
        ResourceLocation table = be.lootTable;
        long seed = be.lootTableSeed;
        be.lootTable = null;
        level.destroyBlock(entry.getPosition(), false);
        level.setBlock(entry.getPosition(), replacement, 2);
        blockEntity = level.getBlockEntity(entry.getPosition());
        if (blockEntity instanceof ILootBlockEntity) {
          ((RandomizableContainerBlockEntity) blockEntity).setLootTable(table, seed);
        } else {
          LootrAPI.LOG.error("replacement " + replacement + " is not an ILootTile " + entry.getDimension() + " at " + entry.getPosition());
        }

        toRemove.add(entry);
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

  public static class Entry {
    private final ResourceKey<Level> dimension;
    private final BlockPos position;
    private final Set<ChunkPos> chunks;
    private final long addedAt;

    public Entry(ResourceKey<Level> dimension, BlockPos position, Set<ChunkPos> chunks, long addedAt) {
      this.dimension = dimension;
      this.position = position;
      this.chunks = chunks;
      this.addedAt = addedAt;
    }

    public ResourceKey<Level> getDimension() {
      return dimension;
    }

    public BlockPos getPosition() {
      return position;
    }

    public Set<ChunkPos> getChunkPositions() {
      return chunks;
    }

    public long age(MinecraftServer server) {
      return server.getTickCount() - addedAt;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Entry entry = (Entry) o;

      if (!dimension.equals(entry.dimension)) return false;
      return position.equals(entry.position);
    }

    @Override
    public int hashCode() {
      int result = dimension.hashCode();
      result = 31 * result + position.hashCode();
      return result;
    }
  }
}
