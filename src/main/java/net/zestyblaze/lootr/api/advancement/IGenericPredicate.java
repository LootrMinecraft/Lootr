package net.zestyblaze.lootr.api.advancement;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public interface IGenericPredicate<T> {
  boolean test(ServerPlayer player, T condition);

  IGenericPredicate<T> deserialize(@Nullable JsonObject element);
}
