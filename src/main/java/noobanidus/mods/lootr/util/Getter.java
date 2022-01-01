package noobanidus.mods.lootr.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.DistExecutor;
import noobanidus.mods.lootr.client.ClientGetter;
import noobanidus.mods.lootr.impl.ServerGetter;

import javax.annotation.Nullable;

public class Getter {
  @Nullable
  public static PlayerEntity getPlayer() {
    return DistExecutor.safeRunForDist(() -> ClientGetter::getPlayer, () -> ServerGetter::getPlayer);
  }
}
