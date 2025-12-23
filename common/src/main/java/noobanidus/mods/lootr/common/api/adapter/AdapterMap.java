package noobanidus.mods.lootr.common.api.adapter;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class AdapterMap {
  private static final ILootrDataAdapter<Object> NONE = new ILootrDataAdapter<>() {
    @Override
    public Class<Object> getAssignableClass() {
      return Object.class;
    }

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
    public int priority() {
      return Integer.MIN_VALUE;
    }
  };

  private final Map<Class<?>, ILootrDataAdapter<?>> byClass = new ConcurrentHashMap<>();
  private final List<ILootrDataAdapter<?>> allAdapters = new ArrayList<>();


  public AdapterMap() {
  }

  public <T> void register(ILootrDataAdapter<T> adapter) {
    allAdapters.add(adapter);
    byClass.clear();
  }

  @Nullable
  public <T> ILootrDataAdapter<T> getAdapter(T type) {
    if (type == null) {
      return null;

    }
    Class<?> clazz = type.getClass();
    ILootrDataAdapter<?> potentialAdapter = byClass.computeIfAbsent(clazz, clazz2 -> {
      ILootrDataAdapter<?> best = null;
      int bestDistance = Integer.MAX_VALUE;
      int bestPriority = Integer.MIN_VALUE;

      for (ILootrDataAdapter<?> adapter : allAdapters) {
        if (!adapter.getAssignableClass().isAssignableFrom(clazz2)) {
          continue;
        }

        int d = distance(clazz2, adapter);
        int p = adapter.priority();

        if (d < bestDistance || (d == bestDistance && p > bestPriority)) {
          best = adapter;
          bestDistance = d;
          bestPriority = p;
        }
      }
      return best == null ? NONE : best;
    });
    if (potentialAdapter == NONE) {
      return null;
    }
    return (ILootrDataAdapter<T>) potentialAdapter;
  }

  private static int distance(Class<?> runtime, ILootrDataAdapter<?> target) {
    Class<?> targetClass = target.getAssignableClass();
    int d = 0;

    for (Class<?> c = runtime; c != null; c = c.getSuperclass()) {
      if (c == targetClass) return d;
      d++;
    }

    return Integer.MAX_VALUE;
  }
}
