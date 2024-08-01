package noobanidus.mods.lootr.api.data.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import noobanidus.mods.lootr.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.common.mixins.MixinVehicleEntity;

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

  @Override
  default void performDecay() {
    if (!(this instanceof Entity entity1)) {
      return;
    }
    if (this instanceof AbstractMinecartContainer entity2) {
      entity2.destroy(entity1.level().damageSources().fellOutOfWorld());
    } else if (this instanceof VehicleEntity entity3) {
      entity3.destroy(((MixinVehicleEntity) entity3).invokeGetDropItem());
      if (this instanceof ContainerEntity entity4) {
        entity4.chestVehicleDestroyed(entity1.level().damageSources().fellOutOfWorld(), entity1.level(), entity1);
      }
    }
  }
}
