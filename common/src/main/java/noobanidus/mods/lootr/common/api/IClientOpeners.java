package noobanidus.mods.lootr.common.api;

import net.minecraft.world.entity.player.Player;
import noobanidus.mods.lootr.common.api.annotation.ClientOnly;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface IClientOpeners extends IOpeners {
  @ClientOnly
  @Nullable
  Set<UUID> getClientOpeners();

  boolean isClientOpened();

  void setClientOpened(boolean opened);

  @Override
  default boolean clearOpeners () {
    boolean result = IOpeners.super.clearOpeners();
    Set<UUID> clientOpeners = getClientOpeners();
    if (clientOpeners != null && !clientOpeners.isEmpty()) {
      clientOpeners.clear();
      markChanged();
      return true;
    }
    return result;
  }

  default boolean hasClientOpened (UUID uuid) {
    Set<UUID> clientOpeners = getClientOpeners();
    if (clientOpeners != null && !clientOpeners.isEmpty() && clientOpeners.contains(uuid)) {
      return true;
    }
    return isClientOpened();
  }
}
