package net.zestyblaze.lootr.util;

import net.minecraft.server.MinecraftServer;
import net.zestyblaze.lootr.events.ServerEvents;

public class ServerAccessImpl {
    public static MinecraftServer getServer() {
        return ServerEvents.serverInstance;
    }
}
