package noobanidus.mods.lootr.event;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
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
}
