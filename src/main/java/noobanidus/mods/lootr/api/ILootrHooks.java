package noobanidus.mods.lootr.api;

import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public interface ILootrHooks {
  default boolean clearPlayerLoot(ServerPlayer entity) {
    return clearPlayerLoot(entity.getUUID());
  }

  boolean clearPlayerLoot(UUID id);
}
