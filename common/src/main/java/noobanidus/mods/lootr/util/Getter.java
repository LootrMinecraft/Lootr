package noobanidus.mods.lootr.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.player.Player;

public class Getter {
  @ExpectPlatform
  public static Player getPlayer() {
    throw new AssertionError();
  }
}
