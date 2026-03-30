package noobanidus.mods.lootr.fabric.impl;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import noobanidus.mods.lootr.common.impl.DefaultLootrAPIImpl;
import noobanidus.mods.lootr.fabric.event.LootrEventsInit;

public class LootrAPIImpl extends DefaultLootrAPIImpl {
  @Override
  public MinecraftServer getServer() {
    return LootrEventsInit.serverInstance;
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
