package noobanidus.mods.lootr.common.api;

import org.jetbrains.annotations.ApiStatus;

/**
 * The level of recursion within this interface is cursed.
 */
@ApiStatus.Internal
public interface IMarkChanged {
  // Mark the actual implementation object (i.e., block entity, entity)
  // as "changed".
  void markChanged ();

  // Mark the associated data (which may be separate to the actual
  // implementation object) as "changed".
  void markDataChanged ();
}
