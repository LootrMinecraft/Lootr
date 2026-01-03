package noobanidus.mods.lootr.common.impl;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.ILootrAPI;
import noobanidus.mods.lootr.common.api.ILootrBlockEntityConverter;
import noobanidus.mods.lootr.common.api.ILootrEntityConverter;
import noobanidus.mods.lootr.common.api.ILootrType;
import noobanidus.mods.lootr.common.api.adapter.AdapterMap;
import noobanidus.mods.lootr.common.api.adapter.ILootrDataAdapter;
import noobanidus.mods.lootr.common.api.adapter.ILootrItemFrameAdapter;
import noobanidus.mods.lootr.common.api.client.ILootrFabricModelProvider;
import noobanidus.mods.lootr.common.api.command.ILootrCommandExtension;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;
import noobanidus.mods.lootr.common.api.filter.ILootrFilter;
import noobanidus.mods.lootr.common.api.filter.ILootrFilterProvider;
import noobanidus.mods.lootr.common.api.processor.ILootrBlockEntityProcessor;
import noobanidus.mods.lootr.common.api.processor.ILootrEntityProcessor;
import noobanidus.mods.lootr.common.api.replacement.BlockReplacementMap;
import noobanidus.mods.lootr.common.api.replacement.ILootrBlockReplacementProvider;
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
  private final AdapterMap<ILootrDataAdapter<?>> dataAdapterMap = new AdapterMap<>(AdapterMap.NONE_DATA_ADAPTER);
  private final AdapterMap<ILootrItemFrameAdapter<?>> itemFrameAdapterMap = new AdapterMap<>(AdapterMap.NONE_ITEM_FRAME_ADAPTER);
  private final BlockReplacementMap replacementMap = new BlockReplacementMap();
  private final Map<String, ILootrType> typeMap = new Object2ObjectOpenHashMap<>();
  // Only used on Fabric
  private final List<ILootrFabricModelProvider> fabricModelProviders = new ObjectArrayList<>();
  private final List<ILootrCommandExtension> commandExtensions = new ObjectArrayList<>();

  private final String commands;

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

    ServiceLoader<ILootrDataAdapter> loader6 = ServiceLoader.load(ILootrDataAdapter.class, classLoader);
    for (ILootrDataAdapter<?> adapter : loader6) {
      dataAdapterMap.register(adapter);
    }

    ServiceLoader<ILootrBlockReplacementProvider> loader9 = ServiceLoader.load(ILootrBlockReplacementProvider.class, classLoader);
    for (ILootrBlockReplacementProvider provider : loader9) {
      replacementMap.register(provider);
    }

    replacementMap.sort();

    ServiceLoader<ILootrType> loader10 = ServiceLoader.load(ILootrType.class, classLoader);
    for (ILootrType type : loader10) {
      typeMap.put(type.getName(), type);
      type.callback();
    }

    ServiceLoader<ILootrFabricModelProvider> loader11 = ServiceLoader.load(ILootrFabricModelProvider.class, classLoader);
    for (ILootrFabricModelProvider appender : loader11) {
      fabricModelProviders.add(appender);
    }

    StringJoiner commandsTemp = new StringJoiner(" | ");

    ServiceLoader<ILootrCommandExtension> loader12 = ServiceLoader.load(ILootrCommandExtension.class, classLoader);
    for (ILootrCommandExtension extension : loader12) {
      commandExtensions.add(extension);
      commandsTemp.add(extension.getId());
      commandsTemp.add(extension.getId() + " <loot-table>");
    }

    this.commands = commandsTemp.toString();

    ServiceLoader<ILootrItemFrameAdapter> loader13 = ServiceLoader.load(ILootrItemFrameAdapter.class, classLoader);
    for (ILootrItemFrameAdapter<?> adapter : loader13) {
      itemFrameAdapterMap.register(adapter);
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
  static <T extends BlockEntity> ILootrBlockEntity convertBlockEntity(T blockEntity) {
    if (blockEntity == null) {
      return null;
    }
    Function<T, ILootrBlockEntity> converter = getBlockEntity(blockEntity.getType());
    if (converter == null) {
      // TODO: Is it worth checking T instanceof ILootrBlockEntity? If it has a converted
      // registered then this check would fail, so it can properly provide whatever object
      // it wishes to.
      return null;
    }
    return converter.apply(blockEntity);
  }

  @Nullable
  static <T extends Entity> ILootrEntity convertEntity(T entity) {
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

  static BlockState getReplacementBlockState(BlockState block) {
    return getInstance().replacementMap.getReplacement(block);
  }

  public static void clearReplacements() {
    getInstance().replacementMap.clear();
  }

  @Nullable
  static <T> ILootrDataAdapter<T> getAdapter(T type) {
    return (ILootrDataAdapter<T>) getInstance().dataAdapterMap.getAdapter(type);
  }

  @Nullable
  static <T>ILootrItemFrameAdapter<T> getItemFrameAdapter (T type) {
    return (ILootrItemFrameAdapter<T>)  getInstance().itemFrameAdapterMap.getAdapter(type);
  }

  @Nullable
  static ILootrType getType(String type) {
    return getInstance().typeMap.get(type);
  }

  @ApiStatus.Internal
  public static List<ILootrFabricModelProvider> getModelAppenders() {
    return getInstance().fabricModelProviders;
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
