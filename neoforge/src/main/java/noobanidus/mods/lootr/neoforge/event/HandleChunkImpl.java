package noobanidus.mods.lootr.neoforge.event;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.event.HandleChunk;

@EventBusSubscriber(modid = LootrAPI.MODID)
public class HandleChunkImpl {
  @SubscribeEvent
  public static void onChunkLoad(ChunkEvent.Load event) {
    if (!event.getLevel().isClientSide()) {
      ChunkAccess chunk = event.getChunk();
      if (chunk.getPersistedStatus().isOrAfter(ChunkStatus.FULL) && chunk instanceof LevelChunk lChunk) {
        HandleChunk.addLoadedChunk(lChunk.getLevel().dimension(), lChunk.getPos());
      }
    }
  }

  @SubscribeEvent
  public static void onServerStarted(ServerAboutToStartEvent event) {
    HandleChunk.clear();
  }

  @SubscribeEvent
  public static void onServerStopped(ServerStoppedEvent event) {
    HandleChunk.clear();
  }
}
