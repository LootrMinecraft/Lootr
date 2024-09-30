package noobanidus.mods.lootr.common.api;

import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;

import java.util.function.Function;

public interface ILootrConverter<T> extends Function<T, ILootrBlockEntity> {
  @Override
  ILootrBlockEntity apply (T blockEntity);

  Class<? extends T> getClassType ();
}
