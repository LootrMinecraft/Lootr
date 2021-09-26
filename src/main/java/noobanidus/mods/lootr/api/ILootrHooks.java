package noobanidus.mods.lootr.api;

import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.UUID;

public interface ILootrHooks {
  default boolean clearPlayerLoot(ServerPlayerEntity entity) {
    return clearPlayerLoot(entity.getUUID());
  }

  boolean clearPlayerLoot(UUID id);
}
