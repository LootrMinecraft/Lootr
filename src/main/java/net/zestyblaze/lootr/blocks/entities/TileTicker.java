package net.zestyblaze.lootr.blocks.entities;

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
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.api.blockentity.ILootBlockEntity;
import net.zestyblaze.lootr.chunk.HandleChunk;
import net.zestyblaze.lootr.config.LootrModConfig;
import net.zestyblaze.lootr.util.ServerAccessImpl;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class TileTicker {
  private final static Object listLock = new Object();
  private final static Object worldLock = new Object();
  private static boolean tickingList = false;
  private final static Set<Entry> tileEntries = new LinkedHashSet<>();
  private final static Set<Entry> pendingEntries = new LinkedHashSet<>();

  public static void addEntry(ResourceKey<Level> dimension, BlockPos position) {
    // TODO: Dimension Blacklisting
    if (LootrModConfig.isDimensionBlacklisted(dimension)) {
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

  public static void serverTick() {
      Set<Entry> toRemove = new HashSet<>();
      Set<Entry> copy;
      synchronized (listLock) {
        tickingList = true;
        copy = new HashSet<>(tileEntries);
        tickingList = false;
      }
      synchronized (worldLock) {
        MinecraftServer server = ServerAccessImpl.getServer();
        for (Entry entry : copy) {
          ServerLevel level = server.getLevel(entry.getDimension());
          if (level == null) {
            toRemove.add(entry);
            continue;
          }
          synchronized (HandleChunk.LOADED_CHUNKS) {
            if (HandleChunk.LOADED_CHUNKS.get(entry.dimension) == null || !HandleChunk.LOADED_CHUNKS.get(entry.dimension).containsAll(entry.getChunkPositions())) {
              continue;
            }
          }
          BlockEntity blockEntity = level.getBlockEntity(entry.getPosition());
          if (!(blockEntity instanceof RandomizableContainerBlockEntity be) || blockEntity instanceof ILootBlockEntity) {
            toRemove.add(entry);
            continue;
          }
          if (be.lootTable == null || LootrModConfig.isBlacklisted(be.lootTable)) {
            toRemove.add(entry);
            continue;
          }
          // TODO: Structure blacklisting
/*          if (!ConfigManager.getLootStructureBlacklist().isEmpty()) {
            StructureFeature<?> startAt = StructureUtil.featureFor(level, entry.getPosition());
            if (startAt != null && ConfigManager.getLootStructureBlacklist().contains(startAt.getRegistryName())) {
              toRemove.add(entry);
              continue;
            }
          }*/
          // TODO: Replacement config
          BlockState stateAt = level.getBlockState(entry.getPosition());
          BlockState replacement = LootrModConfig.replacement(stateAt);
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
    private final Set<ChunkPos> chunks = new HashSet<>();

    public Entry(ResourceKey<Level> dimension, BlockPos position) {
      this.dimension = dimension;
      this.position = position;

      ChunkPos chunkPos = new ChunkPos(this.position);

      int oX = chunkPos.x;
      int oZ = chunkPos.z;
      chunks.add(chunkPos);

      for (int x = -2; x <= 2; x++) {
        for (int z = -2; z <= 2; z++) {
          chunks.add(new ChunkPos(oX + x, oZ + z));
        }
      }
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

    @Override
    public String toString() {
      return "Entry{" +
          "dimension=" + dimension +
          ", position=" + position +
          '}';
    }
  }
}
