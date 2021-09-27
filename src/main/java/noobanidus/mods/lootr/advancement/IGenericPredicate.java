package noobanidus.mods.lootr.advancement;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public interface IGenericPredicate<T> {
  boolean test(ServerPlayer player, T condition);

  IGenericPredicate<T> deserialize(@Nullable JsonObject element);
}
