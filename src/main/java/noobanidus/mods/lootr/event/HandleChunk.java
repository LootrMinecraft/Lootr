package noobanidus.mods.lootr.event;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.event.server.*;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.Lootr;

import java.util.*;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class HandleChunk {
  public static final Map<ResourceKey<Level>, Set<ChunkPos>> LOADED_CHUNKS = Collections.synchronizedMap(new HashMap<>());

  @SubscribeEvent
  public static void onChunkLoad(ChunkEvent.Load event) {
    if (!event.getWorld().isClientSide()) {
      ChunkAccess chunk = event.getChunk();
      if (chunk.getStatus().isOrAfter(ChunkStatus.FULL) && chunk instanceof LevelChunk lChunk) {
        synchronized (LOADED_CHUNKS) {
          Set<ChunkPos> chunkSet = LOADED_CHUNKS.computeIfAbsent(lChunk.getLevel().dimension(), k -> Collections.synchronizedSet(new HashSet<>()));
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
