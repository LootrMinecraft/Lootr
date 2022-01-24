package noobanidus.mods.lootr.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.Lootr;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class HandleChunk {
  public static final Set<ChunkPos> LOADED_CHUNKS = Collections.synchronizedSet(new HashSet<>());

  @SubscribeEvent
  public static void onChunkLoad(ChunkEvent.Load event) {
    if (!event.getWorld().isClientSide()) {
      ChunkAccess chunk = event.getChunk();
      if (chunk.getStatus().isOrAfter(ChunkStatus.FULL)) {
        synchronized (LOADED_CHUNKS) {
          LOADED_CHUNKS.add(chunk.getPos());
        }
      }
    }
  }

  @SubscribeEvent
  public static void onServerStarted(ServerStartedEvent event) {
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
