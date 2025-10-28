package noobanidus.mods.lootr.fabric.event;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class HandleChunk {
  public static final Map<ResourceKey<Level>, Set<ChunkPos>> LOADED_CHUNKS = new ConcurrentHashMap<>();

  public static void onChunkLoad(ServerLevel level, LevelChunk chunk) {
    if (!level.isClientSide()) {
      if (chunk.getFullStatus().isOrAfter(FullChunkStatus.FULL)) {
        Set<ChunkPos> chunkSet = LOADED_CHUNKS.computeIfAbsent(chunk.getLevel()
            .dimension(), k -> ConcurrentHashMap.newKeySet());
        chunkSet.add(chunk.getPos());
      }
    }
  }

  public static void onServerStarted() {
    LOADED_CHUNKS.clear();
  }
}
