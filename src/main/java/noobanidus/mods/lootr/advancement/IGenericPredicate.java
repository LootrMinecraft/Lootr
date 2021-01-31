package noobanidus.mods.lootr.advancement;

import com.google.gson.JsonElement;
import net.minecraft.entity.player.ServerPlayerEntity;

import javax.annotation.Nullable;

public interface IGenericPredicate<T> {
  boolean test(ServerPlayerEntity player, T condition);

  IGenericPredicate<T> deserialize(@Nullable JsonElement element);
}
