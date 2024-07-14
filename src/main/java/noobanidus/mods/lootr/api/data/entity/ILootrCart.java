package noobanidus.mods.lootr.api.data.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import noobanidus.mods.lootr.api.IOpeners;
import noobanidus.mods.lootr.api.data.ILootrInfoProvider;

public interface ILootrCart extends ILootrInfoProvider {
  @Override
  default LootrInfoType getInfoType() {
    return LootrInfoType.CONTAINER_ENTITY;
  }

  default VehicleEntity asEntity () {
    if (this instanceof VehicleEntity entity) {
      return entity;
    }
    throw new IllegalStateException("asEntity called on non-VehicleEntity ILootrCart");
  }
}
