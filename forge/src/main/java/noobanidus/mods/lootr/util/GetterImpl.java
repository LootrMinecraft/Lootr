package noobanidus.mods.lootr.util;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.DistExecutor;
import noobanidus.mods.lootr.client.impl.ClientGetter;
import noobanidus.mods.lootr.impl.ServerGetter;

public class GetterImpl {
  public static Player getPlayer() {
    return DistExecutor.safeRunForDist(() -> ClientGetter::getPlayer, () -> ServerGetter::getPlayer);
  }
}
