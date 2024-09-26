package noobanidus.mods.lootr.common.api;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// "Proof of concept"
public class LootrOptional {
  private static final Map<Class<? extends BlockEntity>, MethodHandle> blockEntityMap = new HashMap<>();
  private static final Set<Class<? extends BlockEntity>> invalidBlockEntities = new HashSet<>();
  private static final String GET_LOOTR_OBJECT = "getLootrObject";

  @Nullable
  public static ILootrBlockEntity getBlockEntity(BlockEntity blockEntity) {
    Holder<BlockEntityType<?>> holder = blockEntity.getType().builtInRegistryHolder();
    if (holder == null || !holder.is(LootrTags.BlockEntity.LOOTR_OBJECT)) {
      return null;
    }

    if (invalidBlockEntities.contains(blockEntity.getClass())) {
      return null;
    }

    MethodHandle handle = blockEntityMap.get(blockEntity.getClass());
    if (handle == null) {
      MethodType type = MethodType.methodType(Object.class);
      try {
        handle = MethodHandles.lookup().findVirtual(blockEntity.getClass(), GET_LOOTR_OBJECT, type);
        blockEntityMap.put(blockEntity.getClass(), handle);
      } catch (NoSuchMethodException | IllegalAccessException e) {
        return null;
      }
    }

    try {
      Object result = handle.invoke(blockEntity);
      if (result instanceof ILootrBlockEntity) {
        return (ILootrBlockEntity) result;
      } else {
        invalidBlockEntities.add(blockEntity.getClass());
      }
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }

    return null;
  }
}
