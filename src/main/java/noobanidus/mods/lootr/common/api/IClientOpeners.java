package noobanidus.mods.lootr.common.api;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface IClientOpeners extends IOpeners {
  // TODO: CLIENT ONLY
  @Nullable
  Set<UUID> getClientOpeners();

  boolean isClientOpened();

  void setClientOpened(boolean opened);

  default boolean hasClientOpened () {
    Player player = LootrAPI.getPlayer();
    if (player == null) {
      return false;
    }

    return hasClientOpened(player.getUUID());
  }

  default boolean hasClientOpened (UUID uuid) {
    Set<UUID> clientOpeners = getClientOpeners();
    if (clientOpeners != null && clientOpeners.contains(uuid)) {
      return true;
    }
    return isClientOpened();
  }
}
