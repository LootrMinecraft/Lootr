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
      markChanged();
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
      markChanged();
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
    Set<UUID> openers = getVisualOpeners();
    if (openers == null) {
      return false;
    }
    UUID uuid = LootrAPI.resolveServerPlayerTeam(player);
    if (openers.remove(uuid)) {
      markChanged();
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
      markChanged();
      return true;
    }
    return false;
  }

  default boolean hasOpened(Player player) {
    return hasServerOpened(player);
  }

  default boolean hasServerOpened(Player player) {
    Set<UUID> openers = getActualOpeners();
    if (openers == null) {
      return false;
    }
    UUID uuid = LootrAPI.resolveServerPlayerTeam(player);
    return !openers.isEmpty() && openers.contains(uuid);
  }

  @Deprecated
  default boolean hasOpened(UUID uuid) {
    return hasServerOpened(uuid);
  }

  // So technically we have 3 types of openers. This is only for the
  // looted stat counting. So use `hasLootAvailable` instead.
  @Deprecated(forRemoval = true)
  default boolean hasServerOpened(UUID uuid) {
    return hasServerOpened(uuid);
  }

  @Deprecated(forRemoval = true)
  default boolean addActualOpener(UUID uuid) {
    return addActualOpener(uuid);
  }

  @Deprecated(forRemoval = true)
  default boolean addVisualOpener(UUID uuid) {
    return addVisualOpener(uuid);
  }

  @Deprecated(forRemoval = true)
  default boolean hasVisualOpened(UUID uuid) {
    return hasVisualOpened(uuid);
  }

  default boolean removeVisualOpener(UUID uuid) {
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
}
