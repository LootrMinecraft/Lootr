package noobanidus.mods.lootr.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.MinecraftServer;

public class ServerAccess {
  @ExpectPlatform
  public static MinecraftServer getServer() {
    throw new AssertionError();
  }
}
