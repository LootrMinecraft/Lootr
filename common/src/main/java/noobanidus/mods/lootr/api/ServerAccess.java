package noobanidus.mods.lootr.api;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public class ServerAccess {
  @ExpectPlatform
  @Nullable
  public static MinecraftServer getServer() {
    throw new AssertionError();
  }
}
