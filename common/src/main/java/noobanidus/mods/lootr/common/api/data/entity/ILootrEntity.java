package noobanidus.mods.lootr.common.api.data.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.PlatformAPI;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;

public interface ILootrEntity extends ILootrInfoProvider {
  @Override
  @Deprecated
  default LootrInfoType getInfoType() {
    return LootrInfoType.CONTAINER_ENTITY;
  }

  default Entity asEntity () {
    if (this instanceof Entity entity) {
      return entity;
    }

    throw new NullPointerException("ILootrEntity implementation is not an Entity and doesn't provide asEntity()!");
  }

  @Override
  default void performOpen(ServerPlayer player) {
    PlatformAPI.performEntityOpen(this, player);
  }

  @Override
  default void performOpen() {
    PlatformAPI.performEntityOpen(this);
  }

  @Override
  default void performClose(ServerPlayer player) {
    PlatformAPI.performEntityClose(this, player);
  }

  @Override
  default void performClose() {
    PlatformAPI.performEntityClose(this);
  }

  @Override
  default void performDecay() {
    Level level = getInfoLevel();
    if (level == null || level.isClientSide()) {
      return;
    }
    boolean replaceWhenDecayed = LootrAPI.shouldReplaceWhenDecayed();
    Entity entity = asEntity();
    if (replaceWhenDecayed) {
      EntityType<?> type = getInfoNewType().getReplacementEntity();
      if (type != null) {
        Entity newCart = type.create(level);
        if (newCart != null) {
          newCart.setPos(entity.position());
          newCart.setXRot(entity.getXRot());
          newCart.setYRot(entity.getYRot());
          level.addFreshEntity(newCart);
        }
      }
    }
    entity.discard();
  }
}
