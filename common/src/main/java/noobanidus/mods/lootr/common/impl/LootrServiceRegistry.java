package noobanidus.mods.lootr.common.impl;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import noobanidus.mods.lootr.common.api.ILootrBlockEntityConverter;
import noobanidus.mods.lootr.common.api.ILootrEntityConverter;
import noobanidus.mods.lootr.common.api.IReplaceableBlockEntityConverter;
import noobanidus.mods.lootr.common.api.IReplacementProvider;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrCart;
import noobanidus.mods.lootr.common.api.filter.ILootrFilter;
import noobanidus.mods.lootr.common.api.filter.ILootrFilterProvider;
import noobanidus.mods.lootr.common.api.replacement.IReplaceableBlockEntity;
import noobanidus.mods.lootr.common.api.replacement.RandomizableContainerWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;

public class LootrServiceRegistry {
  private static LootrServiceRegistry INSTANCE;

  private static final IReplaceableBlockEntityConverter NO_CONVERTER = new IReplaceableBlockEntityConverter() {
    @Override
    public IReplaceableBlockEntity apply(BlockEntity blockEntity) {
      return null;
    }

    @Override
    public boolean canConvert(BlockEntity blockEntity) {
      return false;
    }
  };

  private final Map<BlockEntityType<?>, Function<?, ?>> blockEntityConverterMap = new Object2ObjectOpenHashMap<>();
  private final Map<EntityType<?>, Function<?, ?>> entityConverterMap = new Object2ObjectOpenHashMap<>();
  private final Map<BlockEntityType<?>, IReplaceableBlockEntityConverter> replacementConversionMap = new Object2ObjectOpenHashMap<>();
  private final List<IReplaceableBlockEntityConverter> replacementConverters = new ObjectArrayList<>();
  private final List<IReplacementProvider> replacementProviders = new ObjectArrayList<>();

  private final List<ILootrFilter> filters = new ObjectArrayList<>();

  @SuppressWarnings("rawtypes")
  public LootrServiceRegistry() {
    ServiceLoader<ILootrBlockEntityConverter> loader = ServiceLoader.load(ILootrBlockEntityConverter.class);

    for (ILootrBlockEntityConverter<?> converter : loader) {
      blockEntityConverterMap.put(converter.getBlockEntityType(), converter);
    }

    ServiceLoader<ILootrEntityConverter> loader2 = ServiceLoader.load(ILootrEntityConverter.class);
    for (ILootrEntityConverter<?> converter2 : loader2) {
      entityConverterMap.put(converter2.getEntityType(), converter2);
    }

    ServiceLoader<ILootrFilterProvider> loader3 = ServiceLoader.load(ILootrFilterProvider.class);
    for (ILootrFilterProvider provider : loader3) {
      filters.addAll(provider.getFilters());
    }
    filters.sort(Comparator.comparingInt(ILootrFilter::getPriority));

    ServiceLoader<IReplaceableBlockEntityConverter> loader4 = ServiceLoader.load(IReplaceableBlockEntityConverter.class);
    for (IReplaceableBlockEntityConverter converter : loader4) {
      replacementConverters.add(converter);
      if (converter.getBlockEntityType() != null) {
        replacementConversionMap.put(converter.getBlockEntityType(), converter);
      }
    }

    ServiceLoader<IReplacementProvider> loader5 = ServiceLoader.load(IReplacementProvider.class);
    for (IReplacementProvider provider : loader5) {
      replacementProviders.add(provider);
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
  public static <T extends BlockEntity> ILootrBlockEntity convertBlockEntity(T blockEntity) {
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
  public static <T extends Entity> ILootrCart convertEntity(T entity) {
    if (entity == null) {
      return null;
    }
    Function<T, ILootrCart> converter = getEntity(entity.getType());
    if (converter == null) {
      return null;
    }
    return converter.apply(entity);
  }

  public static List<ILootrFilter> getFilters() {
    return getInstance().filters;
  }

  public static List<IReplaceableBlockEntityConverter> getConverters() {
    return getInstance().replacementConverters;
  }

  public static List<IReplacementProvider> getReplacementProviders () {
    return getInstance().replacementProviders;
  }

  public static boolean hasConverterForReplacement(BlockEntity blockEntity) {
    if (blockEntity instanceof RandomizableContainerBlockEntity) {
      return true;
    }

    if (getInstance().replacementConversionMap.containsKey(blockEntity.getType())) {
      return true;
    }

    for (IReplaceableBlockEntityConverter converter : getConverters()) {
      if (converter.canConvert(blockEntity)) {
        getInstance().replacementConversionMap.put(blockEntity.getType(), converter);
        return true;
      }
    }

    getInstance().replacementConversionMap.put(blockEntity.getType(), NO_CONVERTER);
    return false;
  }

  public static IReplaceableBlockEntity convertForReplacement(BlockEntity blockEntity) {
    if (blockEntity instanceof RandomizableContainerBlockEntity randomizableContainerBlockEntity) {
      return new RandomizableContainerWrapper(randomizableContainerBlockEntity);
    }

    if (getInstance().replacementConversionMap.containsKey(blockEntity.getType())) {
      return getInstance().replacementConversionMap.get(blockEntity.getType()).apply(blockEntity);
    }

    for (IReplaceableBlockEntityConverter converter : getConverters()) {
      if (converter.canConvert(blockEntity)) {
        getInstance().replacementConversionMap.put(blockEntity.getType(), converter);
        return converter.apply(blockEntity);
      }
    }

    getInstance().replacementConversionMap.put(blockEntity.getType(), NO_CONVERTER);
    return null;
  }
}
