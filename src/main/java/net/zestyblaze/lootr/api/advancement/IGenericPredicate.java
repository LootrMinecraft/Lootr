package net.zestyblaze.lootr.api.advancement;

import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface IGenericPredicate<T> {
    boolean test(ServerPlayerEntity player, T condition);

    IGenericPredicate<T> deserialize(@Nullable JsonObject element);
}
