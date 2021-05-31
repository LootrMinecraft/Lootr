package noobanidus.mods.lootr.impl;

import noobanidus.mods.lootr.api.ILootrHooks;
import noobanidus.mods.lootr.data.NewChestData;

import java.util.UUID;

public class LootrHooksImpl implements ILootrHooks {
  @Override
  public boolean clearPlayerLoot(UUID id) {
    return NewChestData.clearInventories(id);
  }
}
