package noobanidus.mods.lootr.api;

import net.minecraft.entity.player.ServerPlayerEntity;
import noobanidus.mods.lootr.data.NewChestData;

import java.util.UUID;

public class LootrHooks {
  public static boolean clearPlayerLoot (ServerPlayerEntity entity) {
    return clearPlayerLoot(entity.getUniqueID());
  }

  public static boolean clearPlayerLoot (UUID id) {
    return NewChestData.clearInventories(id);
  }
}
