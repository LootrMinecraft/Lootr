package noobanidus.mods.lootr.api;

import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.UUID;

public class LootrHooks implements ILootrHooks {
  public static ILootrHooks INSTANCE;

  @Override
  public boolean clearPlayerLoot(ServerPlayerEntity entity) {
    return INSTANCE.clearPlayerLoot(entity.getUniqueID());
  }

  @Override
  public boolean clearPlayerLoot(UUID id) {
    return INSTANCE.clearPlayerLoot(id);
  }
}
