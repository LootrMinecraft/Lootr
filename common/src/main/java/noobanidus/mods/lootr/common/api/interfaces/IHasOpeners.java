package noobanidus.mods.lootr.common.api.interfaces;

import net.minecraft.world.entity.player.Player;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.interfaces.annotation.ServerOnly;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface IHasOpeners extends IMarkChanged {
  @ServerOnly
  @Nullable
  Set<UUID> getVisualOpeners();

  @Nullable
  Set<UUID> getActualOpeners();

  default boolean addOpener(Player player) {
    boolean result1 = addVisualOpener(player);
    boolean result2 = addActualOpener(player);
    return result1 || result2;
  }

  default boolean clearOpeners() {
    Set<UUID> openers = getVisualOpeners();
    if (openers == null) {
      return false;
    }
    if (!openers.isEmpty()) {
      openers.clear();
      markInstanceChanged();
      return true;
    }
    return false;
  }

  default boolean addVisualOpener(Player player) {
    Set<UUID> openers = getVisualOpeners();
    if (openers == null) {
      return false;
    }

    UUID uuid = LootrAPI.resolveServerPlayerTeam(player);
    if (openers.add(uuid)) {
      markInstanceChanged();
      return true;
    }
    return false;
  }

  default boolean hasVisualOpened(Player player) {
    Set<UUID> openers = getVisualOpeners();
    if (openers == null) {
      return false;
    }

    UUID uuid = LootrAPI.resolveServerPlayerTeam(player);
    return !openers.isEmpty() && openers.contains(uuid);
  }

  default boolean removeVisualOpener(Player player) {
    return removeVisualOpener(LootrAPI.resolveServerPlayerTeam(player));
  }

  default boolean removeVisualOpener(UUID uuid) {
    Set<UUID> openers = getVisualOpeners();
    if (openers == null) {
      return false;
    }

    if (openers.remove(uuid)) {
      markInstanceChanged();
      return true;
    }
    return false;
  }


  default boolean addActualOpener(Player player) {
    Set<UUID> openers = getActualOpeners();
    if (openers == null) {
      return false;
    }

    UUID uuid = LootrAPI.resolveServerPlayerTeam(player);
    if (openers.add(uuid)) {
      markInstanceChanged();
      return true;
    }
    return false;
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  default boolean hasServerOpened(Player player) {
    Set<UUID> openers = getActualOpeners();
    if (openers == null) {
      return false;
    }
    UUID uuid = LootrAPI.resolveServerPlayerTeam(player);
    return !openers.isEmpty() && openers.contains(uuid);
  }


  @Deprecated(forRemoval = true)
  default boolean hasServerOpened(UUID uuid) {
    throw new NotImplementedException("hasServerOpened must be called with a player; do not use `uuid` variant.");
  }

  @Deprecated(forRemoval = true)
  default boolean addActualOpener(UUID uuid) {
    throw new NotImplementedException("addActualOpener must be called with a player; do not use `uuid` variant.");
  }

  @Deprecated(forRemoval = true)
  default boolean addVisualOpener(UUID uuid) {
    throw new NotImplementedException("addVisualOpener must be called with a player; do not use `uuid` variant.");
  }

  @Deprecated(forRemoval = true)
  default boolean hasVisualOpened(UUID uuid) {
    throw new NotImplementedException("hasVisualOpened must be called with a player; do not use `uuid` variant.");
  }
}
