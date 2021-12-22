package noobanidus.mods.lootr.api;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

public class ServerAccessImpl {
  @Nullable
  public static MinecraftServer getServer() {
    return ServerLifecycleHooks.getCurrentServer();
  }
}
