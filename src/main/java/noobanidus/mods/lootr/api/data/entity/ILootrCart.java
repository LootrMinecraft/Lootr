package noobanidus.mods.lootr.api.data.entity;

import noobanidus.mods.lootr.api.IHasOpeners;
import noobanidus.mods.lootr.api.data.ILootrInfoProvider;

public interface ILootrCart extends IHasOpeners, ILootrInfoProvider {
  @Override
  default LootrInfoType getInfoType() {
    return LootrInfoType.CONTAINER_ENTITY;
  }
}
