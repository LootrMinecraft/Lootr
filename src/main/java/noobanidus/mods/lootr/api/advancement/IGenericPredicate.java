package noobanidus.mods.lootr.api.advancement;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nullable;

public interface IGenericPredicate<T> {
  boolean test(EntityPlayerMP player, T condition);

  IGenericPredicate<T> deserialize(@Nullable JsonObject element);
}
