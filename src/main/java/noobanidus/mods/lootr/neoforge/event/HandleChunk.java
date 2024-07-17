package noobanidus.mods.lootr.neoforge.event;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import noobanidus.mods.lootr.api.LootrAPI;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@EventBusSubscriber(modid = LootrAPI.MODID)
public class HandleChunk {
  public static final Map<ResourceKey<Level>, Set<ChunkPos>> LOADED_CHUNKS = Collections.synchronizedMap(new Object2ObjectLinkedOpenHashMap<>());

  @SubscribeEvent
  public static void onChunkLoad(ChunkEvent.Load event) {
    if (!event.getLevel().isClientSide()) {
      ChunkAccess chunk = event.getChunk();
      if (chunk.getPersistedStatus().isOrAfter(ChunkStatus.FULL) && chunk instanceof LevelChunk lChunk) {
        synchronized (LOADED_CHUNKS) {
          Set<ChunkPos> chunkSet = LOADED_CHUNKS.computeIfAbsent(lChunk.getLevel().dimension(), k -> Collections.synchronizedSet(new ObjectLinkedOpenHashSet<>()));
          chunkSet.add(chunk.getPos());
        }
      }
    }
  }

  @SubscribeEvent
  public static void onServerStarted(ServerAboutToStartEvent event) {
    synchronized (LOADED_CHUNKS) {
      LOADED_CHUNKS.clear();
    }
  }

  @SubscribeEvent
  public static void onServerStopped(ServerStoppedEvent event) {
    synchronized (LOADED_CHUNKS) {
      LOADED_CHUNKS.clear();
    }
  }
}
