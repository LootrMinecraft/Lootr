package noobanidus.mods.lootr.common.api;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import noobanidus.mods.lootr.common.api.replacement.IReplaceableBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface IReplaceableBlockEntityConverter extends Function<BlockEntity, IReplaceableBlockEntity> {
  @Override
  @Nullable
  IReplaceableBlockEntity apply(BlockEntity blockEntity);

  boolean canConvert (BlockEntity blockEntity);

  @Nullable
  default BlockEntityType<?> getBlockEntityType() {
    return null;
  }
}
