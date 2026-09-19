package noobanidus.mods.lootr.common.api;

import net.minecraft.world.entity.player.Player;
import noobanidus.mods.lootr.common.api.annotation.ClientOnly;
import noobanidus.mods.lootr.common.client.ClientHooks;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

@ApiStatus.Internal
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

  default boolean hasClientOpened (PlayerContext context) {
    if (isClientOpened()) {
      return true;
    }

    if (!context.hasPlayer()) {
      return false;
    }
    return hasClientOpened(context.player());
  }

  default boolean hasClientOpened (Player player) {
    if (isClientOpened()) {
      return true;
    }

    UUID id = LootrAPI.resolvePlayerTeam(player);

    var clientOpeners = getClientOpeners();

    return clientOpeners != null && !clientOpeners.isEmpty() && clientOpeners.contains(id);
  }

  @Deprecated(forRemoval = true)
  default boolean hasClientOpened (UUID uuid) {
    PlayerContext context = ClientHooks.getPlayerContext();
    if (!context.hasPlayer()) {
      LootrAPI.LOG.error("Called `hasClientOpened` with a uuid outside of the client context. Was looking for uuid '{}'.", uuid, new Exception());
      return false;
    }

    return hasClientOpened(context.player());
  }
}
