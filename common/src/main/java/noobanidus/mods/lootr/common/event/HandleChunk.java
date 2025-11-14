package noobanidus.mods.lootr.common.event;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class HandleChunk {
  private static final Map<ResourceKey<Level>, Set<ChunkPos>> LOADED_CHUNKS = new ConcurrentHashMap<>();

  public static void addLoadedChunk (ResourceKey<Level> key, ChunkPos pos) {
    LOADED_CHUNKS.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(pos);
  }

  public static boolean anyUnloaded (ResourceKey<Level> key, Collection<ChunkPos> positions) {
    Set<ChunkPos> loaded = LOADED_CHUNKS.get(key);
    if (loaded == null || loaded.isEmpty()) {
      return true;
    }
    for (ChunkPos pos : positions) {
      if (!loaded.contains(pos)) {
        return true;
      }
    }
    return false;
  }

  public static void clear () {
    LOADED_CHUNKS.clear();
  }
}
