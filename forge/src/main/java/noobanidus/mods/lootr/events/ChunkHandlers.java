package noobanidus.mods.lootr.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.util.forge.PlatformUtilsImpl;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = LootrAPI.MODID)
public class ChunkHandlers {
    @SubscribeEvent
    public void onChunkLoad(@NotNull ChunkDataEvent event) {
        if (event.getChunk() instanceof LevelChunk chunk && chunk.getLevel() instanceof ServerLevel level) {
            PlatformUtilsImpl.CHUNK_LOAD_HANDLERS.forEach(handler -> handler.accept(level, chunk));
        }
    }
}
