package noobanidus.mods.lootr.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

public class ClientGetter {
  @Nullable
  public static PlayerEntity getPlayer() {
    return Minecraft.getInstance().player;
  }
}
