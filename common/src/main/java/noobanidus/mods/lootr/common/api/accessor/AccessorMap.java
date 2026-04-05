package noobanidus.mods.lootr.common.api.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccessorMap<A extends ILootrAccessor<?>> {
  public static final ILootrDataAccessor<Object> NONE_DATA_ADAPTER = new ILootrDataAccessor<>() {
    @Override
    public @Nullable ResourceKey<LootTable> getLootTable(Object entity) {
      return null;
    }

    @Override
    public long getLootSeed(Object entity) {
      return 0;
    }

    @Override
    public void setLootTable(Object entity, ResourceKey<LootTable> table, long seed) {
    }

    @Override
    public Class<Object> getAssignableClass() {
      return Object.class;
    }

    @Override
    public int priority() {
      return Integer.MIN_VALUE;
    }
  };

  public static final ILootrItemFrameAccessor<Object> NONE_ITEM_FRAME_ADAPTER = new ILootrItemFrameAccessor<>() {

    @Override
    public Class<Object> getAssignableClass() {
      return Object.class;
    }

    @Override
    public Direction getDirection(Object object) {
      return Direction.NORTH;
    }

    @Override
    public ItemStack getItem(Object object) {
      return ItemStack.EMPTY;
    }

    @Override
    public int getRotation(Object object) {
      return 0;
    }

    @Override
    public BlockPos getPos(Object object) {
      return BlockPos.ZERO;
    }

    @Override
    public boolean isFixed(Object object) {
      return false;
    }

    @Override
    public boolean isInvisible(Object object) {
      return false;
    }

    @Override
    public int priority() {
      return Integer.MIN_VALUE;
    }
  };


  private final A NONE;
  private final Map<Class<?>, A> byClass = new ConcurrentHashMap<>();
  private final List<A> allAccessors = new ArrayList<>();

  public AccessorMap(A none) {
    this.NONE = none;
  }

  public void register(A accessor) {
    allAccessors.add(accessor);
    byClass.clear();
  }

  @Nullable
  public A getAccessor(@Nullable Object type) {
    if (type == null) {
      return null;

    }
    Class<?> clazz = type.getClass();
    A potentialAccessor = byClass.computeIfAbsent(clazz, clazz2 -> {
      A best = null;
      int bestDistance = Integer.MAX_VALUE;
      int bestPriority = Integer.MIN_VALUE;

      for (A accessor : allAccessors) {
        if (!accessor.getAssignableClass().isAssignableFrom(clazz2)) {
          continue;
        }

        int d = distance(clazz2, accessor);
        int p = accessor.priority();

        if (d < bestDistance || (d == bestDistance && p > bestPriority)) {
          best = accessor;
          bestDistance = d;
          bestPriority = p;
        }
      }
      return best == null ? NONE : best;
    });
    if (potentialAccessor == NONE) {
      return null;
    }
    return potentialAccessor;
  }

  private static int distance(Class<?> runtime, ILootrAccessor<?> target) {
    Class<?> targetClass = target.getAssignableClass();
    int d = 0;

    for (Class<?> c = runtime; c != null; c = c.getSuperclass()) {
      if (c == targetClass) return d;
      d++;
    }

    return Integer.MAX_VALUE;
  }
}
