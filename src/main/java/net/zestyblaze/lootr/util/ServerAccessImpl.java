package net.zestyblaze.lootr.util;

import net.minecraft.server.MinecraftServer;
import net.zestyblaze.lootr.event.LootrEventsInit;

public class ServerAccessImpl {
  public static MinecraftServer getServer() {
    return LootrEventsInit.serverInstance;
  }
}
