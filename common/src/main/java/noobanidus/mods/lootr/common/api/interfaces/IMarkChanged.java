package noobanidus.mods.lootr.common.api.interfaces;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface IMarkChanged {
  // Mark the instance (ILootrContainerInstance) as "changed"
  void markInstanceChanged();

  // Mark the section (SavedData) as "changed"
  void markSectionChanged();
}
