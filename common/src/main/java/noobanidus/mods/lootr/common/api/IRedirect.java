package noobanidus.mods.lootr.common.api;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface IRedirect<T> {
  T getRedirect();
}
