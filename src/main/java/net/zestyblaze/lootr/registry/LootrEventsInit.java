package net.zestyblaze.lootr.registry;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.config.LootrModConfig;

public class LootrEventsInit {
    public static MinecraftServer serverInstance;

    public static void registerEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> serverInstance = server);

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> serverInstance = null);

        if(LootrModConfig.get().debug.debugMode) {
            LootrAPI.LOG.info("Lootr: Common Registry - Events Registered");
        }
    }
}
