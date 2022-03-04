package net.zestyblaze.lootr.registry;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.MinecraftServer;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.blocks.entities.TileTicker;
import net.zestyblaze.lootr.chunk.HandleChunk;
import net.zestyblaze.lootr.config.LootrModConfig;

public class LootrEventsInit {
    public static MinecraftServer serverInstance;

    public static void registerEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> serverInstance = server);
        ServerLifecycleEvents.SERVER_STARTING.register(minecraftServer1 -> HandleChunk.onServerStarted());

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> serverInstance = null);
        ServerLifecycleEvents.SERVER_STOPPED.register(minecraftServer -> HandleChunk.onServerStarted());

        ServerTickEvents.END_SERVER_TICK.register(server -> TileTicker.serverTick());

        ServerChunkEvents.CHUNK_LOAD.register(HandleChunk::onChunkLoad);

        //TODO: Actually finish this lmao
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> false);

        if(LootrModConfig.get().debug.debugMode) {
            LootrAPI.LOG.info("Lootr: Common Registry - Events Registered");
        }
    }
}
