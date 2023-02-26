package noobanidus.mods.lootr.util;

import net.minecraft.server.MinecraftServer;
import noobanidus.mods.lootr.init.ModEvents;

public class ServerAccessImpl {
  public static MinecraftServer getServer() {
    return ModEvents.serverInstance;
  }
}
