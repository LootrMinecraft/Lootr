package noobanidus.mods.lootr.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

public class ClientGetter {
  public static PlayerEntity getPlayer() {
    return Minecraft.getInstance().player;
  }
}
