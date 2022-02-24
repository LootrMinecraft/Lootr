package net.zestyblaze.lootr.api;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public interface ILootrHooks {
    default boolean clearPlayerLoot(ServerPlayerEntity entity) {
        return clearPlayerLoot(entity.getUuid());
    }

    boolean clearPlayerLoot(UUID id);
}
