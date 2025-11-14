package noobanidus.mods.lootr.common.chunk;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LoadedChunks {
  private static final Map<ResourceKey<Level>, Set<ChunkPos>> LOADED_CHUNKS = new ConcurrentHashMap<>();

  public static void onChunkLoad(LevelAccessor level, LevelChunk chunk) {
    if (!level.isClientSide()) {
      if (chunk.getFullStatus().isOrAfter(FullChunkStatus.FULL)) {
        ResourceKey<Level> dimension = chunk.getLevel().dimension();
        Set<ChunkPos> chunkSet = LOADED_CHUNKS.computeIfAbsent(dimension, k -> ConcurrentHashMap.newKeySet());
        chunkSet.add(chunk.getPos());
      }
    }
  }

  public static boolean anyUnloadedChunks(ResourceKey<Level> dimension, Collection<ChunkPos> chunks) {
    Set<ChunkPos> syncedChunks = LOADED_CHUNKS.get(dimension);
    return syncedChunks == null || !syncedChunks.containsAll(chunks);
  }

  public static void clear() {
    LOADED_CHUNKS.clear();
  }
}
