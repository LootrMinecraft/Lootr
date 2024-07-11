package noobanidus.mods.lootr.api;

import net.minecraft.world.entity.player.Player;

import java.util.Set;
import java.util.UUID;

public interface IHasOpeners extends IMarkChanged {
  Set<UUID> getVisualOpeners();

  Set<UUID> getActualOpeners();

  default boolean addVisualOpener (Player player) {
    return addVisualOpener(player.getUUID());
  }

  default boolean addVisualOpener (UUID uuid) {
    if (getVisualOpeners().add(uuid)) {
      markChanged();
      return true;
    }
    return false;
  }

  default boolean hasVisuallyOpened (Player player) {
    return hasVisuallyOpened(player.getUUID());
  }

  default boolean hasVisuallyOpened (UUID uuid) {
    return getVisualOpeners().contains(uuid);
  }

  default boolean removeVisualOpener (Player player) {
    return removeVisualOpener(player.getUUID());
  }

  default boolean removeVisualOpener (UUID uuid) {
    if (getVisualOpeners().remove(uuid)) {
      markChanged();
      return true;
    }
    return false;
  }

  default boolean addActuallyOpened (Player player) {
    return addActuallyOpened(player.getUUID());
  }

  default boolean addActuallyOpened (UUID uuid) {
    if (getActualOpeners().add(uuid)) {
      markChanged();
      return true;
    }
    return false;
  }

  default boolean hasActuallyOpened (Player player) {
    return hasActuallyOpened(player.getUUID());
  }

  default boolean hasActuallyOpened (UUID uuid) {
    return getActualOpeners().contains(uuid);
  }
}
