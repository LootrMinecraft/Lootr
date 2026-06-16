package noobanidus.mods.lootr.common.api;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public record PlayerContext (@Nullable Player player) {
  public boolean hasPlayer () {
    return player != null;
  }
}
