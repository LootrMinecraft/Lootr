package noobanidus.mods.lootr.util;

import net.minecraft.server.MinecraftServer;
import noobanidus.mods.lootr.init.LootrEventsInit;

public class ServerAccessImpl {
  public static MinecraftServer getServer() {
    return LootrEventsInit.serverInstance;
  }
}
