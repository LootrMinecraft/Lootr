package noobanidus.mods.lootr.common.api.interfaces;

import net.minecraft.world.entity.player.Player;
import noobanidus.mods.lootr.common.api.interfaces.annotation.ClientOnly;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

@ApiStatus.Internal
public interface IClientHasOpeners extends IHasOpeners {
  @ClientOnly
  @Nullable
  Set<UUID> getClientOpeners();

  boolean isClientOpened();

  void setClientOpened(boolean opened);

  @Override
  default boolean clearOpeners () {
    boolean result = IHasOpeners.super.clearOpeners();
    Set<UUID> clientOpeners = getClientOpeners();
    if (clientOpeners != null && !clientOpeners.isEmpty()) {
      clientOpeners.clear();
      markInstanceChanged();
      return true;
    }
    return result;
  }

  default boolean hasClientOpened (Player player) {
    return hasClientOpened(player.getUUID());
  }

  default boolean hasClientOpened (UUID uuid) {
    if (isClientOpened()) {
      return true;
    }
    Set<UUID> clientOpeners = getClientOpeners();
    return clientOpeners != null && !clientOpeners.isEmpty() && clientOpeners.contains(uuid);
  }
}
