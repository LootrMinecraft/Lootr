package noobanidus.mods.lootr.neoforge.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.chunk.LoadedChunks;
import noobanidus.mods.lootr.common.data.LegacyMigrationRunner;

@EventBusSubscriber(modid = LootrAPI.MODID)
public class HandleChunk {
  @SubscribeEvent
  public static void onChunkLoad(ChunkEvent.Load event) {
    if (event.getChunk() instanceof LevelChunk levelChunk) {
      LevelAccessor level = event.getLevel();
      if (level instanceof ServerLevel level2) {
        LoadedChunks.onChunkLoad(level2, levelChunk, event.isNewChunk());
      }
    }
  }

  @SubscribeEvent
  public static void onChunkUnload(ChunkEvent.Unload event) {
    if (event.getChunk() instanceof LevelChunk levelChunk) {
      LevelAccessor level = event.getLevel();
      LoadedChunks.onChunkUnload(level, levelChunk);
    }
  }

  @SubscribeEvent
  public static void onServerStarted(ServerAboutToStartEvent event) {
    LoadedChunks.clear();
  }

  // Saved data storage only exists once the levels are loaded, and no player can join before the first tick.
  @SubscribeEvent
  public static void onServerStartedMigrate(ServerStartedEvent event) {
    LegacyMigrationRunner.runOnStartup(event.getServer());
  }

  @SubscribeEvent
  public static void onServerStopped(ServerStoppedEvent event) {
    LoadedChunks.clear();
  }
}
