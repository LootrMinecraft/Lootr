package noobanidus.mods.lootr.common.api;

import net.minecraft.world.entity.player.Player;

public record PlayerContext (Player player) {
  public boolean hasPlayer () {
    return player != null;
  }

  public Player player () {
    if (this.player == null) {
      throw new NullPointerException("Check PlayerContext::hasPlayer before accessing `player`");
    }

    return this.player;
  }
}
