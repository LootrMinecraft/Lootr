package net.zestyblaze.lootr.api;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class LootrHooks implements ILootrHooks {
    public static ILootrHooks INSTANCE;

    @Override
    public boolean clearPlayerLoot(ServerPlayerEntity entity) {
        return INSTANCE.clearPlayerLoot(entity.getUuid());
    }

    @Override
    public boolean clearPlayerLoot(UUID id) {
        return INSTANCE.clearPlayerLoot(id);
    }
}
