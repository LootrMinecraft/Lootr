package noobanidus.mods.lootr.common.impl;

import noobanidus.mods.lootr.common.api.ILootrBlockEntityConverter;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;

public class LootrServiceRegistry {
  private static LootrServiceRegistry INSTANCE;

  private Map<Class<?>, Function<?, ?>> converterMap = new HashMap<>();

  public LootrServiceRegistry () {
    @SuppressWarnings("rawtypes") ServiceLoader<ILootrBlockEntityConverter> loader = ServiceLoader.load(ILootrBlockEntityConverter.class);

    for (ILootrBlockEntityConverter<?> converter : loader) {
      converterMap.put(converter.getClassType(), converter);
    }
  }

  public static LootrServiceRegistry getInstance () {
    if (INSTANCE == null) {
      INSTANCE = new LootrServiceRegistry();
    }
    return INSTANCE;
  }

  @Nullable
  @SuppressWarnings("unchecked")
  private static <T> Function<T, ILootrBlockEntity> getConverter (Class<?> clazz) {
    return (Function<T, ILootrBlockEntity>) getInstance().converterMap.get(clazz);
  }

  @Nullable
  public static <T> ILootrBlockEntity convert (T blockEntity) {
    Function<T, ILootrBlockEntity> converter = getConverter( blockEntity.getClass());
    if (converter == null) {
      return null;
    }
    return converter.apply(blockEntity);
  }
}
