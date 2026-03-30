package noobanidus.mods.lootr.neoforge.impl;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.common.impl.DefaultLootrAPIImpl;
import org.jetbrains.annotations.Nullable;

public class LootrAPIImpl extends DefaultLootrAPIImpl {
  @Override
  @Nullable
  public MinecraftServer getServer() {
    return ServerLifecycleHooks.getCurrentServer();
  }

  @Override
  public boolean isFakePlayer(Player player) {
    if (player instanceof ServerPlayer sPlayer) {
      //noinspection ConstantValue
      if (sPlayer.connection == null) {
        return true;
      }
    }
    return player instanceof FakePlayer;
  }
}
