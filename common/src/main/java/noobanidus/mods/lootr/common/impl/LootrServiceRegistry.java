package noobanidus.mods.lootr.common.impl;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import noobanidus.mods.lootr.common.api.ILootrAPI;
import noobanidus.mods.lootr.common.api.ILootrBlockEntityConverter;
import noobanidus.mods.lootr.common.api.ILootrEntityConverter;
import noobanidus.mods.lootr.common.api.adapter.AdapterMap;
import noobanidus.mods.lootr.common.api.adapter.ILootrDataAdapter;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrCart;
import noobanidus.mods.lootr.common.api.filter.ILootrFilter;
import noobanidus.mods.lootr.common.api.filter.ILootrFilterProvider;
import noobanidus.mods.lootr.common.api.processor.ILootrPostProcessor;
import noobanidus.mods.lootr.common.api.processor.ILootrPreProcessor;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;

public class LootrServiceRegistry {
  private static LootrServiceRegistry INSTANCE;

  private final Map<BlockEntityType<?>, Function<?, ?>> blockEntityConverterMap = new Object2ObjectOpenHashMap<>();
  private final Map<EntityType<?>, Function<?, ?>> entityConverterMap = new Object2ObjectOpenHashMap<>();
  private final List<ILootrFilter> filters = new ObjectArrayList<>();
  private final List<ILootrPostProcessor> postProcessors = new ObjectArrayList<>();
  private final List<ILootrPreProcessor> preProcessors = new ObjectArrayList<>();
  private final AdapterMap adapterMap = new AdapterMap();

  @SuppressWarnings("rawtypes")
  public LootrServiceRegistry() {
    ClassLoader classLoader = ILootrAPI.class.getClassLoader();
    ServiceLoader<ILootrBlockEntityConverter> loader = ServiceLoader.load(ILootrBlockEntityConverter.class, classLoader);

    for (ILootrBlockEntityConverter<?> converter : loader) {
      blockEntityConverterMap.put(converter.getBlockEntityType(), converter);
    }

    ServiceLoader<ILootrEntityConverter> loader2 = ServiceLoader.load(ILootrEntityConverter.class, classLoader);
    for (ILootrEntityConverter<?> converter2 : loader2) {
      entityConverterMap.put(converter2.getEntityType(), converter2);
    }

    ServiceLoader<ILootrFilterProvider> loader3 = ServiceLoader.load(ILootrFilterProvider.class, classLoader);
    for (ILootrFilterProvider provider : loader3) {
      filters.addAll(provider.getFilters());
    }
    filters.sort(Comparator.comparingInt(ILootrFilter::getPriority));

    ServiceLoader<ILootrPostProcessor> loader4 = ServiceLoader.load(ILootrPostProcessor.class, classLoader);
    for (ILootrPostProcessor processor : loader4) {
      postProcessors.add(processor);
    }

    ServiceLoader<ILootrPreProcessor> loader5 = ServiceLoader.load(ILootrPreProcessor.class, classLoader);
    for (ILootrPreProcessor processor : loader5) {
      preProcessors.add(processor);
    }

    ServiceLoader<ILootrDataAdapter> loader6 = ServiceLoader.load(ILootrDataAdapter.class, classLoader);
    for (ILootrDataAdapter<?> adapter : loader6) {
      adapterMap.register(adapter);
    }
  }

  public static LootrServiceRegistry getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new LootrServiceRegistry();
    }
    return INSTANCE;
  }

  @Nullable
  @SuppressWarnings("unchecked")
  private static <T> Function<T, ILootrBlockEntity> getBlockEntity(BlockEntityType<?> clazz) {
    return (Function<T, ILootrBlockEntity>) getInstance().blockEntityConverterMap.get(clazz);
  }

  @SuppressWarnings("unchecked")
  @Nullable
  private static <T> Function<T, ILootrCart> getEntity(EntityType<?> clazz) {
    return (Function<T, ILootrCart>) getInstance().entityConverterMap.get(clazz);
  }

  @Nullable
  static <T extends BlockEntity> ILootrBlockEntity convertBlockEntity(T blockEntity) {
    if (blockEntity == null) {
      return null;
    }
    Function<T, ILootrBlockEntity> converter = getBlockEntity(blockEntity.getType());
    if (converter == null) {
      return null;
    }
    return converter.apply(blockEntity);
  }

  @Nullable
  static <T extends Entity> ILootrCart convertEntity(T entity) {
    if (entity == null) {
      return null;
    }
    Function<T, ILootrCart> converter = getEntity(entity.getType());
    if (converter == null) {
      return null;
    }
    return converter.apply(entity);
  }

  static List<ILootrFilter> getFilters() {
    return getInstance().filters;
  }

  static List<ILootrPreProcessor> getPreProcessors() {
    return getInstance().preProcessors;
  }

  static List<ILootrPostProcessor> getPostProcessors() {
    return getInstance().postProcessors;
  }

  @Nullable
  static <T> ILootrDataAdapter<T> findAdapter (T type) {
    return getInstance().adapterMap.findAdapter(type);
  }
}
