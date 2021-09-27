package noobanidus.mods.lootr.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ClientGetter {
  public static Player getPlayer() {
    return Minecraft.getInstance().player;
  }
}
