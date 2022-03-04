package net.zestyblaze.lootr.chunk;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.*;

public class HandleChunk {
  public static final Map<ResourceKey<Level>, Set<ChunkPos>> LOADED_CHUNKS = Collections.synchronizedMap(new HashMap<>());

  public static void onChunkLoad(ServerLevel level, LevelChunk chunk) {
    if (!level.isClientSide()) {
      if (chunk.getStatus().isOrAfter(ChunkStatus.FULL)) {
        synchronized (LOADED_CHUNKS) {
          Set<ChunkPos> chunkSet = LOADED_CHUNKS.computeIfAbsent(chunk.getLevel().dimension(), k -> Collections.synchronizedSet(new HashSet<>()));
          chunkSet.add(chunk.getPos());
        }
      }
    }
  }

  public static void onServerStarted() {
    synchronized (LOADED_CHUNKS) {
      LOADED_CHUNKS.clear();
    }
  }
}
