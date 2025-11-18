package noobanidus.mods.lootr.common.block.entity;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.DataToCopy;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.PlatformAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BlockEntityTicker {
  private final static Set<Entry> blockEntityEntries = new ObjectOpenHashSet<>();
  private final static Map<Entry, Entry> pendingEntries = new Object2ObjectOpenHashMap<>();

  public static void addEntity(BlockEntity entity, Level level, ChunkPos chunkPos) {
    if (LootrAPI.isDisabled()) {
      return;
    }
    ResourceKey<Level> dimension = getServerDimensionIfValid(level);
    if (dimension == null) {
      return;
    }
    if (!LootrAPI.isWorldBorderSafe(level, chunkPos)) {
      return;
    }
    if (!isValidEntity(entity)) {
      return;
    }

    Entry entry = new Entry(dimension, chunkPos, new HashSet<>());
    synchronized (pendingEntries) {
      Entry previousEntry = pendingEntries.get(entry);
      if (previousEntry != null) {
        previousEntry.entityPositions.add(entity.getBlockPos());
      } else {
        entry.entityPositions.add(entity.getBlockPos());
        pendingEntries.put(entry, entry);
      }
    }
  }

  private static boolean isValidEntity(BlockEntity entity) {
    if (!(entity instanceof RandomizableContainerBlockEntity validEntity)) {
      return false;
    }
    if (LootrAPI.resolveBlockEntity(validEntity) instanceof ILootrBlockEntity) {
      return false;
    }
    return validEntity.getLootTable() != null && !LootrAPI.isLootTableBlacklisted(validEntity.getLootTable());
  }

  public static void onServerTick(MinecraftServer server) {
    if (LootrAPI.isDisabled()) {
      return;
    }

    Iterator<Entry> iterator = blockEntityEntries.iterator();
    while (iterator.hasNext()) {
      Entry entry = iterator.next();

      ServerLevel level = server.getLevel(entry.dimension());
      if (level == null) {
        iterator.remove();
        continue;
      }

      switch (entry.getChunkLoadStatus(level)) {
        case UNLOADED -> {
          // the chunk has unloaded. this entry is no longer valid, and it will be added again if the chunk loads again.
          iterator.remove();
        }
        case SURROUNDING_CHUNKS_NOT_LOADED -> {
          // keep waiting for the surrounding chunks to load
        }
        case FULLY_LOADED -> {
          replaceEntitiesInChunk(level, entry);
          iterator.remove();
        }
      }
    }

    synchronized (pendingEntries) {
      blockEntityEntries.addAll(pendingEntries.keySet());
      pendingEntries.clear();
    }
  }

  private static boolean checkStructureValidity(ServerLevel level, ChunkPos chunkPos, BlockPos position) {
    if (!level.getServer().getWorldData().worldGenOptions().generateStructures()) {
      return true;
    }
    Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
    if (registry.getTag(LootrTags.Structure.STRUCTURE_BLACKLIST).filter(tag -> tag.size() != 0).isPresent()) {
      return !LootrAPI.isTaggedStructurePresent(level, chunkPos, LootrTags.Structure.STRUCTURE_BLACKLIST, position);
    } else if (registry.getTag(LootrTags.Structure.STRUCTURE_WHITELIST).filter(tag -> tag.size() != 0).isPresent()) {
      return LootrAPI.isTaggedStructurePresent(level, chunkPos, LootrTags.Structure.STRUCTURE_WHITELIST, position);
    }
    return true;
  }

  private static void replaceEntitiesInChunk(ServerLevel level, Entry entry) {
    for (BlockPos entityPos : entry.entityPositions()) {
      if (!checkStructureValidity(level, entry.chunkPos(), entityPos)) {
          continue;
      }
      BlockEntity blockEntity = level.getBlockEntity(entityPos);
      if (!(blockEntity instanceof RandomizableContainerBlockEntity be) || LootrAPI.resolveBlockEntity(blockEntity) instanceof ILootrBlockEntity) {
          continue;
      }
      ResourceKey<LootTable> table = be.getLootTable();
      if (table == null || LootrAPI.isLootTableBlacklisted(table)) {
          continue;
      }
      BlockState stateAt = level.getBlockState(entityPos);
      BlockState replacement = LootrAPI.replacementBlockState(stateAt);
      if (replacement == null) {
          continue;
      }

      replaceEntity(level, entityPos, be, replacement, table);
    }
  }

  private static void replaceEntity(Level level, BlockPos entityPos, RandomizableContainerBlockEntity be, BlockState replacement, ResourceKey<LootTable> table) {
    // Save specific data. Currently, this includes the LockCode (all platforms), along with NeoForge's getPersistentData.
    DataToCopy data = PlatformAPI.copySpecificData(be);
    long seed = be.getLootTableSeed();
    // IMPORTANT: Clear loot table to prevent loot drop when container is destroyed
    be.setLootTable(null);
    level.destroyBlock(entityPos, false);
    level.setBlock(entityPos, replacement, 2);
    BlockEntity newBlockEntity = level.getBlockEntity(entityPos);
    PlatformAPI.restoreSpecificData(data, newBlockEntity);
    if (LootrAPI.resolveBlockEntity(newBlockEntity) instanceof ILootrBlockEntity && newBlockEntity instanceof RandomizableContainerBlockEntity rbe) {
      rbe.setLootTable(table, seed);
    } else {
      LootrAPI.LOG.error("replacement {} is not an ILootrBlockEntity {} at {}", replacement, level.dimension(), entityPos);
    }
  }

  @Nullable
  private static ResourceKey<Level> getServerDimensionIfValid(Level level) {
    if (LootrAPI.getServer() == null || level.isClientSide()) {
      return null;
    }
    ResourceKey<Level> dimension = level.dimension();
    if (LootrAPI.isDimensionBlocked(dimension)) {
      return null;
    }
    return dimension;
  }

  public record Entry(ResourceKey<Level> dimension, ChunkPos chunkPos, Set<BlockPos> entityPositions) {
    public ChunkLoadStatus getChunkLoadStatus(ServerLevel level) {
      ChunkSource chunkSource = level.getChunkSource();
      if (!LootrAPI.isWorldBorderSafe(level, chunkPos) || !isChunkLoadedAndTicking(level, chunkSource, chunkPos.x, chunkPos.z)) {
        return ChunkLoadStatus.UNLOADED;
      }

      for (int x = chunkPos.x - 2; x <= chunkPos.x + 2; x++) {
        for (int z = chunkPos.z - 2; z <= chunkPos.z + 2; z++) {
          if (x == chunkPos.x && z == chunkPos.z) {
            // this case is already checked above
            continue;
          }
          ChunkPos pos = new ChunkPos(x, z);
          // This has the potential to force-load chunks on the main thread
          // by ignoring the loading state of chunks outside the world border.
          if (!LootrAPI.isWorldBorderSafe(level, pos)) {
            continue;
          }
          if (!isChunkLoadedAndTicking(level, chunkSource, x, z)) {
            return ChunkLoadStatus.SURROUNDING_CHUNKS_NOT_LOADED;
          }
        }
      }
      return ChunkLoadStatus.FULLY_LOADED;
    }

    private static boolean isChunkLoadedAndTicking(ServerLevel level, ChunkSource chunkSource, int chunkX, int chunkZ) {
      if (!chunkSource.hasChunk(chunkX, chunkZ)) {
        return false;
      }
      LevelChunk chunk = chunkSource.getChunkNow(chunkX, chunkZ);
      if (chunk == null) {
        return false;
      }
      return chunk.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING) && level.areEntitiesLoaded(ChunkPos.asLong(chunkX, chunkZ));
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Entry entry = (Entry) o;

      if (!dimension.equals(entry.dimension)) return false;
      return chunkPos.equals(entry.chunkPos);
    }

    @Override
    public int hashCode() {
      int result = dimension.hashCode();
      result = 31 * result + chunkPos.hashCode();
      return result;
    }
  }

  public enum ChunkLoadStatus {
    UNLOADED,
    SURROUNDING_CHUNKS_NOT_LOADED,
    FULLY_LOADED,
  }
}
