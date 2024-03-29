package noobanidus.mods.lootr.advancement;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import noobanidus.mods.lootr.api.advancement.IGenericPredicate;
import noobanidus.mods.lootr.data.DataStorage;

import javax.annotation.Nullable;
import java.util.UUID;

public class ContainerPredicate implements IGenericPredicate<UUID> {
  @Override
  public boolean test(ServerPlayerEntity player, UUID condition) {
    if (DataStorage.isAwarded(player.getUUID(), condition)) {
      return false;
    } else {
      DataStorage.award(player.getUUID(), condition);
      return true;
    }
  }

  @Override
  public IGenericPredicate<UUID> deserialize(@Nullable JsonObject element) {
    return new ContainerPredicate();
  }
}
