package noobanidus.mods.lootr.util;

import net.minecraft.server.MinecraftServer;
import noobanidus.mods.lootr.events.ServerEvents;

public class ServerAccessImpl {
  public static MinecraftServer getServer () {
    return ServerEvents.serverInstance;
  }
}
