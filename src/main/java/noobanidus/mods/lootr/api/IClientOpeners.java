package noobanidus.mods.lootr.api;

import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface IClientOpeners {
  // TODO: CLIENT ONLY
  @Nullable
  Set<UUID> getClientOpeners();

  boolean isClientOpened();

  void setClientOpened(boolean opened);
}
