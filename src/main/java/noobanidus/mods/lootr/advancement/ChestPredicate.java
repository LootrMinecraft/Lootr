package noobanidus.mods.lootr.advancement;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public class ChestPredicate implements IGenericPredicate<Void> {
  @Override
  public boolean test(ServerPlayer player, Void condition) {
    return true;
  }

  @Override
  public IGenericPredicate<Void> deserialize(@Nullable JsonObject element) {
    return new ChestPredicate();
  }
}
