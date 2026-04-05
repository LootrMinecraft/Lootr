package noobanidus.mods.lootr.common.api.section;

import noobanidus.mods.lootr.common.api.data.ILootrData;
import noobanidus.mods.lootr.common.api.data.ILootrInventoryStore;

public interface ILootrSection {
  ILootrInventoryStore getStore (ILootrData data);
}
