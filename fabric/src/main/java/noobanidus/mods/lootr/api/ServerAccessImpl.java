package noobanidus.mods.lootr.api;

import net.minecraft.server.MinecraftServer;
import noobanidus.mods.lootr.events.ServerStartedEvent;
import org.jetbrains.annotations.Nullable;

public class ServerAccessImpl {
  static {
    ServerStartedEvent.load();
  }

  @Nullable
  public static MinecraftServer getServer() {
    return ServerStartedEvent.serverInstance;
  }
}
