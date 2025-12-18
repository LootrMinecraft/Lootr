package noobanidus.mods.lootr.common.api;

import net.minecraft.world.entity.player.Player;
import noobanidus.mods.lootr.common.api.annotation.ServerOnly;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface IOpeners extends IMarkChanged {
  @ServerOnly
  @Nullable
  Set<UUID> getVisualOpeners();

  @Nullable
  Set<UUID> getActualOpeners();

  default boolean addOpener (Player player) {
    boolean result1 = addVisualOpener(player);
    boolean result2 = addActualOpener(player);
    return result1 || result2;
  }

  default boolean clearOpeners () {
    Set<UUID> openers = getVisualOpeners();
    if (openers == null) {
      return false;
    }
    if (!openers.isEmpty()) {
      openers.clear();
      markChanged();
      return true;
    }
    return false;
  }

  default boolean addVisualOpener (UUID uuid) {
    Set<UUID> openers = getVisualOpeners();
    if (openers == null) {
      return false;
    }
    if (openers.add(uuid)) {
      markChanged();
      return true;
    }
    return false;
  }

  default boolean hasVisualOpened(UUID uuid) {
    Set<UUID> openers = getVisualOpeners();
    if (openers == null) {
      return false;
    }
    return !openers.isEmpty() && openers.contains(uuid);
  }

  default boolean removeVisualOpener (UUID uuid) {
    Set<UUID> openers = getVisualOpeners();
    if (openers == null) {
      return false;
    }
    if (openers.remove(uuid)) {
      markChanged();
      return true;
    }
    return false;
  }

  default boolean addActualOpener(UUID uuid) {
    Set<UUID> openers = getActualOpeners();
    if (openers == null) {
      return false;
    }
    if (openers.add(uuid)) {
      markChanged();
      return true;
    }
    return false;
  }

  @Deprecated
  default boolean hasOpened(UUID uuid) {
    return hasServerOpened(uuid);
  }

  default boolean hasServerOpened (UUID uuid) {
    Set<UUID> openers = getActualOpeners();
    if (openers == null) {
      return false;
    }
    return !openers.isEmpty() && openers.contains(uuid);
  }

  @Deprecated
  default boolean hasOpened(Player player) {
    return hasServerOpened(player.getUUID());
  }

  default boolean hasServerOpened (Player player) {
    return hasServerOpened(player.getUUID());
  }

  default boolean addActualOpener(Player player) {
    return addActualOpener(player.getUUID());
  }

  default boolean addVisualOpener (Player player) {
    return addVisualOpener(player.getUUID());
  }

  default boolean hasVisualOpened(Player player) {
    return hasVisualOpened(player.getUUID());
  }

  default boolean removeVisualOpener (Player player) {
    return removeVisualOpener(player.getUUID());
  }
}
