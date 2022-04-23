package noobanidus.mods.lootr.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;

public class ClientGetter {
  @Nullable
  public static EntityPlayer getPlayer() {
    return Minecraft.getMinecraft().player;
  }
}
