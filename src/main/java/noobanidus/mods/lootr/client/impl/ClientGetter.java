package noobanidus.mods.lootr.client.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class ClientGetter {
  @Nullable
  public static Player getPlayer() {
    return Minecraft.getInstance().player;
  }
}
