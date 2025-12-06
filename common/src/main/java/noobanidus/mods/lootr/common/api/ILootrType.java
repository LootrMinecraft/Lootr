package noobanidus.mods.lootr.common.api;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public interface ILootrType {
  String getName();

  @Nullable
  Block getReplacementBlock();

  @Nullable
  EntityType<?> getReplacementEntity();

  default void callback() {

  }
}
