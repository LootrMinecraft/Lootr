package noobanidus.mods.lootr.util;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;

public class ServerAccessImpl {
  public static MinecraftServer getServer() {
    return ServerLifecycleHooks.getCurrentServer();
  }
}
