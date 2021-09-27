package noobanidus.mods.lootr.api;

import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class LootrHooks implements ILootrHooks {
  public static ILootrHooks INSTANCE;

  @Override
  public boolean clearPlayerLoot(ServerPlayer entity) {
    return INSTANCE.clearPlayerLoot(entity.getUUID());
  }

  @Override
  public boolean clearPlayerLoot(UUID id) {
    return INSTANCE.clearPlayerLoot(id);
  }
}
