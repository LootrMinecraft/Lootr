package noobanidus.mods.lootr.block.entity;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.data.blockentity.ILootrBlockEntity;

import java.util.Set;

public class BlockEntityTicker {
  private final static Object listLock = new Object();
  private final static Object worldLock = new Object();
  private final static Set<Entry> blockEntityEntries = new ObjectLinkedOpenHashSet<>();
  private final static Set<Entry> pendingEntries = new ObjectLinkedOpenHashSet<>();
  private static boolean tickingList = false;

  public static void addEntry(Level level, BlockPos position) {
    if (LootrAPI.isDisabled()) {
      return;
    }

    if (LootrAPI.getServer() == null) {
      return;
    }

    ResourceKey<Level> dimension = level.dimension();
    if (LootrAPI.isDimensionBlocked(dimension)) {
      return;
    }

    ChunkPos chunkPos = new ChunkPos(position);

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
        if (!LootrAPI.isWorldBorderSafe(level, newPos)) {
          continue;
        }

        chunks.add(newPos);
      }
    }

    Entry newEntry = new Entry(dimension, position, chunks, LootrAPI.getCurrentTicks());
    synchronized (listLock) {
      if (tickingList) {
        pendingEntries.add(newEntry);
      } else {
        blockEntityEntries.add(newEntry);
      }
    }
  }

  public static void onServerTick() {
    if (LootrAPI.isDisabled()) {
      return;
    }
    Set<Entry> toRemove = new ObjectLinkedOpenHashSet<>();
    Set<Entry> copy;
    synchronized (listLock) {
      tickingList = true;
      copy = new ObjectLinkedOpenHashSet<>(blockEntityEntries);
      tickingList = false;
    }
    synchronized (worldLock) {
      MinecraftServer server = LootrAPI.getServer();
      if (server == null) {
        LootrAPI.LOG.error("MinecraftServer was null during ServerTickEvent!");
        return;
      }
      for (Entry entry : copy) {
        ServerLevel level = server.getLevel(entry.getDimension());
        if (level == null || LootrAPI.hasExpired(entry.age(server)) || (!LootrAPI.isWorldBorderSafe(level, entry.getPosition()))) {
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
        if (skip) {
          continue;
        }

        if (LootrAPI.anyUnloadedChunks(entry.getDimension(), entry.getChunkPositions())) {
          continue;
        }
        BlockEntity blockEntity = level.getBlockEntity(entry.getPosition());
        if (!(blockEntity instanceof RandomizableContainerBlockEntity be) || blockEntity instanceof ILootrBlockEntity) {
          toRemove.add(entry);
          continue;
        }
        if (be.getLootTable() == null || LootrAPI.isLootTableBlacklisted(be.getLootTable())) {
          toRemove.add(entry);
          continue;
        }
        BlockState stateAt = level.getBlockState(entry.getPosition());
        BlockState replacement = LootrAPI.replacementBlockState(stateAt);
        if (replacement == null) {
          toRemove.add(entry);
          continue;
        }
        ResourceKey<LootTable> table = be.getLootTable();
        long seed = be.getLootTableSeed();
        // Clear loot table to prevent loot drop
        be.setLootTable(null);
        CompoundTag oldData = be.getPersistentData();

        level.destroyBlock(entry.getPosition(), false);
        level.setBlock(entry.getPosition(), replacement, 2);
        BlockEntity newBlockEntity = level.getBlockEntity(entry.getPosition());
        if (newBlockEntity != null) {
          newBlockEntity.getPersistentData().merge(oldData);
        }
        if (newBlockEntity instanceof ILootrBlockEntity && newBlockEntity instanceof RandomizableContainerBlockEntity rbe) {
          rbe.setLootTable(table, seed);
        } else {
          LootrAPI.LOG.error("replacement " + replacement + " is not an ILootrBlockEntity " + entry.getDimension() + " at " + entry.getPosition());
        }

        toRemove.add(entry);
      }
    }
    synchronized (listLock) {
      tickingList = true;
      blockEntityEntries.removeAll(toRemove);
      blockEntityEntries.addAll(pendingEntries);
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
