package noobanidus.mods.lootr.client.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ClientGetter {

  public static Player getPlayer() {
    return Minecraft.getInstance().player;
  }
}
