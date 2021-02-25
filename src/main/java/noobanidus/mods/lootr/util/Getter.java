package noobanidus.mods.lootr.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSidedProvider;
import noobanidus.mods.lootr.client.ClientGetter;

import javax.annotation.Nullable;

public class Getter {
  @Nullable
  public static PlayerEntity getPlayer () {
    return DistExecutor.safeRunForDist(() -> ClientGetter::getPlayer, () -> ServerGetter::getPlayer);
  }
}
