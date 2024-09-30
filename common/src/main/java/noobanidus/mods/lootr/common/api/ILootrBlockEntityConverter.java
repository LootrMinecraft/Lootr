package noobanidus.mods.lootr.common.api;

import net.minecraft.world.level.block.entity.BlockEntityType;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;

import java.util.function.Function;

public interface ILootrBlockEntityConverter<T> extends Function<T, ILootrBlockEntity> {
  @Override
  ILootrBlockEntity apply (T blockEntity);

  BlockEntityType<?> getBlockEntityType ();
}
