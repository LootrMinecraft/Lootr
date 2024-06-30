package noobanidus.mods.lootr.util;

import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import noobanidus.mods.lootr.client.impl.ClientGetter;
import noobanidus.mods.lootr.impl.ServerGetter;
import org.jetbrains.annotations.Nullable;

public class Getter {
  @Nullable
  public static Player getPlayer() {
    if (FMLEnvironment.dist == Dist.CLIENT) {
      return ClientGetter.getPlayer();
    } else {
      return ServerGetter.getPlayer();
    }
  }
}
