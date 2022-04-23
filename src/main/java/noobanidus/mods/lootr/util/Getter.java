package noobanidus.mods.lootr.util;

import net.minecraft.entity.player.EntityPlayer;
import noobanidus.mods.lootr.Lootr;

import javax.annotation.Nullable;

public class Getter {
  @Nullable
  public static EntityPlayer getPlayer() {
    return Lootr.proxy.getPlayer();
  }
}
