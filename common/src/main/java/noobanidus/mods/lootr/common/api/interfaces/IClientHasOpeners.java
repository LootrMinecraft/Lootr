package noobanidus.mods.lootr.common.api.interfaces;

import net.minecraft.world.entity.player.Player;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.PlayerContext;
import noobanidus.mods.lootr.common.api.interfaces.annotation.ClientOnly;
import noobanidus.mods.lootr.common.client.ClientHooks;
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
    if (isClientOpened()) {
      return true;
    }

    var id = LootrAPI.resolveClientPlayerTeam(player);

    Set<UUID> clientOpeners = getClientOpeners();

    return clientOpeners != null && !clientOpeners.isEmpty() && clientOpeners.contains(id);
  }


  default boolean hasClientOpened (UUID uuid) {
    PlayerContext context = ClientHooks.getPlayerContext();
    if (!context.hasPlayer()) {
      LootrAPI.LOG.error("Called `hasClientOpened` UUID variant outside of the client context. Was looking for uuid `{}`.", uuid, new Exception());
      return false;
    }

    return hasClientOpened(context.player());
  }
}
