package noobanidus.mods.lootr.common.impl;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.ILootrAPI;
import noobanidus.mods.lootr.common.api.accessor.AccessorMap;
import noobanidus.mods.lootr.common.api.accessor.ILootrDataAccessor;
import noobanidus.mods.lootr.common.api.accessor.ILootrItemFrameAccessor;
import noobanidus.mods.lootr.common.api.command.ILootrCommandExtension;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;
import noobanidus.mods.lootr.common.api.filter.ILootrFilter;
import noobanidus.mods.lootr.common.api.filter.ILootrFilterProvider;
import noobanidus.mods.lootr.common.api.processor.ILootrBlockEntityProcessor;
import noobanidus.mods.lootr.common.api.processor.ILootrEntityProcessor;
import noobanidus.mods.lootr.common.api.conversion.BlockConversionMap;
import noobanidus.mods.lootr.common.api.conversion.ILootrBlockConversionProvider;
import noobanidus.mods.lootr.common.api.type.ILootrType;
import noobanidus.mods.lootr.common.api.wrapper.ILootrBlockEntityWrapper;
import noobanidus.mods.lootr.common.api.wrapper.ILootrEntityWrapper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class LootrServiceRegistry {
  private static LootrServiceRegistry INSTANCE;

  private final Map<BlockEntityType<?>, Function<?, ?>> blockEntityConverterMap = new Object2ObjectOpenHashMap<>();
  private final Map<EntityType<?>, Function<?, ?>> entityConverterMap = new Object2ObjectOpenHashMap<>();
  private final List<ILootrFilter> filters = new ObjectArrayList<>();
  private final List<ILootrBlockEntityProcessor.Post> blockEntityPostProcessors = new ObjectArrayList<>();
  private final List<ILootrBlockEntityProcessor.Pre> blockEntityPreProcessors = new ObjectArrayList<>();
  private final List<ILootrEntityProcessor.Pre> entityPreProcessors = new ObjectArrayList<>();
  private final List<ILootrEntityProcessor.Post> entityPostProcessors = new ObjectArrayList<>();
  private final AccessorMap<ILootrDataAccessor<?>> dataAccessorMap = new AccessorMap<>(AccessorMap.NONE_DATA_ADAPTER);
  private final AccessorMap<ILootrItemFrameAccessor<?>> itemFrameAccessorMap = new AccessorMap<>(AccessorMap.NONE_ITEM_FRAME_ADAPTER);
  private final BlockConversionMap replacementMap = new BlockConversionMap();
  private final Map<String, ILootrType> typeMap = new Object2ObjectOpenHashMap<>();
  // Only used on Fabric
  private final List<ILootrCommandExtension> commandExtensions = new ObjectArrayList<>();

  private final String commands;

  @SuppressWarnings("rawtypes")
  public LootrServiceRegistry() {
    ClassLoader classLoader = ILootrAPI.class.getClassLoader();
    ServiceLoader<ILootrBlockEntityWrapper> loader = ServiceLoader.load(ILootrBlockEntityWrapper.class, classLoader);

    for (ILootrBlockEntityWrapper<?> converter : loader) {
      blockEntityConverterMap.put(converter.getBlockEntityType(), converter);
    }

    ServiceLoader<ILootrEntityWrapper> loader2 = ServiceLoader.load(ILootrEntityWrapper.class, classLoader);
    for (ILootrEntityWrapper<?> converter2 : loader2) {
      entityConverterMap.put(converter2.getEntityType(), converter2);
    }

    ServiceLoader<ILootrFilterProvider> loader3 = ServiceLoader.load(ILootrFilterProvider.class, classLoader);
    for (ILootrFilterProvider provider : loader3) {
      filters.addAll(provider.getFilters());
    }
    filters.sort(Comparator.comparingInt(ILootrFilter::getPriority).reversed());

    ServiceLoader<ILootrBlockEntityProcessor.Post> loader4 = ServiceLoader.load(ILootrBlockEntityProcessor.Post.class, classLoader);
    for (ILootrBlockEntityProcessor.Post processor : loader4) {
      blockEntityPostProcessors.add(processor);
    }

    ServiceLoader<ILootrBlockEntityProcessor.Pre> loader7 = ServiceLoader.load(ILootrBlockEntityProcessor.Pre.class, classLoader);
    for (ILootrBlockEntityProcessor.Pre processor : loader7) {
      blockEntityPreProcessors.add(processor);
    }

    ServiceLoader<ILootrEntityProcessor.Pre> loader5 = ServiceLoader.load(ILootrEntityProcessor.Pre.class, classLoader);
    for (ILootrEntityProcessor.Pre processor : loader5) {
      entityPreProcessors.add(processor);
    }

    ServiceLoader<ILootrEntityProcessor.Post> loader8 = ServiceLoader.load(ILootrEntityProcessor.Post.class, classLoader);
    for (ILootrEntityProcessor.Post processor : loader8) {
      entityPostProcessors.add(processor);
    }

    ServiceLoader<ILootrDataAccessor> loader6 = ServiceLoader.load(ILootrDataAccessor.class, classLoader);
    for (ILootrDataAccessor<?> adapter : loader6) {
      dataAccessorMap.register(adapter);
    }

    ServiceLoader<ILootrBlockConversionProvider> loader9 = ServiceLoader.load(ILootrBlockConversionProvider.class, classLoader);
    for (ILootrBlockConversionProvider provider : loader9) {
      replacementMap.register(provider);
    }

    replacementMap.sort();

    ServiceLoader<ILootrType> loader10 = ServiceLoader.load(ILootrType.class, classLoader);
    for (ILootrType type : loader10) {
      typeMap.put(type.getName(), type);
      type.callback();
    }

    StringJoiner commandsTemp = new StringJoiner(" | ");

    ServiceLoader<ILootrCommandExtension> loader12 = ServiceLoader.load(ILootrCommandExtension.class, classLoader);
    for (ILootrCommandExtension extension : loader12) {
      commandExtensions.add(extension);
      commandsTemp.add(extension.getId());
      commandsTemp.add(extension.getId() + " <loot-table>");
    }

    this.commands = commandsTemp.toString();

    ServiceLoader<ILootrItemFrameAccessor> loader13 = ServiceLoader.load(ILootrItemFrameAccessor.class, classLoader);
    for (ILootrItemFrameAccessor<?> adapter : loader13) {
      itemFrameAccessorMap.register(adapter);
    }
  }

  public static LootrServiceRegistry getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new LootrServiceRegistry();
    }
    return INSTANCE;
  }

  @Nullable
  private static <T> Function<T, ILootrBlockEntity> getBlockEntity(BlockEntityType<?> clazz) {
    return (Function<T, ILootrBlockEntity>) getInstance().blockEntityConverterMap.get(clazz);
  }

  @Nullable
  private static <T> Function<T, ILootrEntity> getEntity(EntityType<?> clazz) {
    return (Function<T, ILootrEntity>) getInstance().entityConverterMap.get(clazz);
  }

  @Nullable
  static <T extends BlockEntity> ILootrBlockEntity wrapBlockEntity(T blockEntity) {
    if (blockEntity == null) {
      return null;
    }
    Function<T, ILootrBlockEntity> converter = getBlockEntity(blockEntity.getType());
    if (converter == null) {
      // Not worth checking if T is ILootrBlockEntity because they should always register a
      // wrapper.
      return null;
    }
    return converter.apply(blockEntity);
  }

  @Nullable
  static <T extends Entity> ILootrEntity wrapEntity(T entity) {
    if (entity == null) {
      return null;
    }
    Function<T, ILootrEntity> converter = getEntity(entity.getType());
    if (converter == null) {
      return null;
    }
    return converter.apply(entity);
  }

  static List<ILootrFilter> getFilters() {
    return getInstance().filters;
  }

  static List<ILootrEntityProcessor.Pre> getEntityPreProcessors() {
    return getInstance().entityPreProcessors;
  }

  static List<ILootrBlockEntityProcessor.Pre> getBlockEntityPreProcessors() {
    return getInstance().blockEntityPreProcessors;
  }

  static List<ILootrEntityProcessor.Post> getEntityPostProcessors() {
    return getInstance().entityPostProcessors;
  }

  static List<ILootrBlockEntityProcessor.Post> getBlockEntityPostProcessors() {
    return getInstance().blockEntityPostProcessors;
  }

  static BlockState getConvertedBlockState(BlockState block) {
    return getInstance().replacementMap.getReplacement(block);
  }

  public static void clearBlockConverters() {
    getInstance().replacementMap.clear();
  }

  @Nullable
  static <T> ILootrDataAccessor<T> getDataAccessor(T type) {
    return (ILootrDataAccessor<T>) getInstance().dataAccessorMap.getAccessor(type);
  }

  @Nullable
  static <T> ILootrItemFrameAccessor<T> getItemFrameDataAccessor(T type) {
    return (ILootrItemFrameAccessor<T>) getInstance().itemFrameAccessorMap.getAccessor(type);
  }

  @Nullable
  static ILootrType getType(String type) {
    return getInstance().typeMap.get(type);
  }

  @ApiStatus.Internal
  public static List<ILootrCommandExtension> getCommandExtensions() {
    return getInstance().commandExtensions;
  }

  @ApiStatus.Internal
  public static String getCommandExtensionsString() {
    return getInstance().commands;
  }
}
