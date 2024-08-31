package noobanidus.mods.lootr.common.api.data.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import noobanidus.mods.lootr.common.api.ILootrOptional;
import noobanidus.mods.lootr.common.api.PlatformAPI;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;
import org.jetbrains.annotations.Nullable;

public interface ILootrCart extends ILootrInfoProvider {
/*  @Nullable
  static ILootrCart of (ILootrOptional optionalProvider) {
    Object object = optionalProvider.getLootrObject();
    if (object instanceof ILootrCart provider) {
      return provider;
    }
    return null;
  }*/


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
