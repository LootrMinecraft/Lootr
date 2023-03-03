package noobanidus.mods.lootr.api;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public interface IServerAccess {
  @Nullable
  MinecraftServer getServer ();
}
