package noobanidus.mods.lootr.util;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.DistExecutor;
import noobanidus.mods.lootr.client.ClientGetter;
import noobanidus.mods.lootr.impl.ServerGetter;

import javax.annotation.Nullable;

public class Getter {
  @Nullable
  public static Player getPlayer() {
    return DistExecutor.safeRunForDist(() -> ClientGetter::getPlayer, () -> ServerGetter::getPlayer);
  }
}
