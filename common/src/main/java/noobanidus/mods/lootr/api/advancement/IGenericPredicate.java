package noobanidus.mods.lootr.api.advancement;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;

public interface IGenericPredicate<T> {
  boolean test(ServerPlayer player, T condition);

  IGenericPredicate<T> deserialize(JsonObject element);
}
