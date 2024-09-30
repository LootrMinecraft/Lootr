package noobanidus.mods.lootr.common.api.data.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import noobanidus.mods.lootr.common.api.PlatformAPI;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;

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
  default void performOpen(ServerPlayer player) {
    PlatformAPI.performCartOpen(this, player);
  }

  @Override
  default void performOpen() {
    PlatformAPI.performCartOpen(this);
  }

  @Override
  default void performClose(ServerPlayer player) {
    PlatformAPI.performCartClose(this, player);
  }

  @Override
  default void performClose() {
    PlatformAPI.performCartClose(this);
  }
}
