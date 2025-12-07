package noobanidus.mods.lootr.common.api.data.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.level.Level;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.PlatformAPI;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;

/**
 * The default entity-based implementation of ILootrInfo.
 * <br />
 * TODO: "VehicleEntity" as its most common base class might be problematic.
 */
public interface ILootrCart extends ILootrInfoProvider {
  @Override
  @Deprecated
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

  @Override
  default void performDecay() {
    Level level = getInfoLevel();
    if (level == null || level.isClientSide()) {
      return;
    }
    boolean replaceWhenDecayed = LootrAPI.shouldReplaceWhenDecayed();
    VehicleEntity entity = asEntity();
    // TODO: Handle custom entity de-conversion
    if (replaceWhenDecayed) {
      MinecartChest newCart = EntityType.CHEST_MINECART.create(level);
      if (newCart != null) {
        newCart.setPos(entity.position());
        newCart.setXRot(entity.getXRot());
        newCart.setYRot(entity.getYRot());
        level.addFreshEntity(newCart);
      }
    }
    entity.discard();
  }
}
