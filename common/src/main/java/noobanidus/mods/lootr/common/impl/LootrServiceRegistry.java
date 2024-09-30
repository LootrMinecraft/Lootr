package noobanidus.mods.lootr.common.impl;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import noobanidus.mods.lootr.common.api.ILootrBlockEntityConverter;
import noobanidus.mods.lootr.common.api.ILootrEntityConverter;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrCart;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;

public class LootrServiceRegistry {
  private static LootrServiceRegistry INSTANCE;

  private final Map<BlockEntityType<?>, Function<?, ?>> blockEntityConverterMap = new Object2ObjectOpenHashMap<>();
  private final Map<EntityType<?>, Function<?, ?>> entityConverterMap = new Object2ObjectOpenHashMap<>();

  @SuppressWarnings("rawtypes")
  public LootrServiceRegistry () {
    ServiceLoader<ILootrBlockEntityConverter> loader = ServiceLoader.load(ILootrBlockEntityConverter.class);

    for (ILootrBlockEntityConverter<?> converter : loader) {
      blockEntityConverterMap.put(converter.getBlockEntityType(), converter);
    }

    ServiceLoader<ILootrEntityConverter> loader2 = ServiceLoader.load(ILootrEntityConverter.class);
    for (ILootrEntityConverter<?> converter2 : loader2) {
      entityConverterMap.put(converter2.getEntityType(), converter2);
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
    Function<T, ILootrBlockEntity> converter = getBlockEntity( blockEntity.getType());
    if (converter == null) {
      return null;
    }
    return converter.apply(blockEntity);
  }

  @Nullable
  public static <T extends Entity> ILootrCart convertEntity (T entity) {
    Function<T, ILootrCart> converter = getEntity(entity.getType());
    if (converter == null) {
      return null;
    }
    return converter.apply(entity);
  }
}
