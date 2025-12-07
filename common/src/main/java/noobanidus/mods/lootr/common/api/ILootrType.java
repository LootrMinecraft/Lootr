package noobanidus.mods.lootr.common.api;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.data.ILootrInfo;
import org.jetbrains.annotations.Nullable;

/**
 * Defines the type of a Lootr container.
 * <br />
 * Can be accessed via LootrAPI.getType(String name). Alternately, you can
 * include a `callback` function, which will be called when the service is
 * loaded and stored in the type map.
 * <br />
 * This supersedes the now deprecated LootrBlockType and LootrInfoType.
 */
public interface ILootrType {
  String getName();

  @Nullable
  Block getReplacementBlock();

  @Nullable
  EntityType<?> getReplacementEntity();

  default void callback() {

  }

  default boolean canDecay () {
    return true;
  }

  default boolean canRefresh () {
    return true;
  }

  default boolean isEntity() {
    return false;
  }

  @Nullable
  default Container getContainer (ILootrInfo info, ServerLevel level) {
    if (isEntity() && getReplacementEntity() != null) {
      Entity entity = level.getEntity(info.getInfoUUID());
      if (entity instanceof Container container) {
        return container;
      }
    } else if (!isEntity() && getReplacementBlock() != null) {
      BlockEntity be = level.getBlockEntity(info.getInfoPos());
      if (be instanceof Container container) {
        return container;
      }
    }

    return null;
  }
}
