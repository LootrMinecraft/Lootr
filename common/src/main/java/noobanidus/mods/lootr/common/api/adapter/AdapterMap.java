package noobanidus.mods.lootr.common.api.adapter;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AdapterMap {
  private final Map<Class<?>, ILootrDataAdapter<?>> byClass = new Object2ObjectOpenHashMap<>();
  private final List<ILootrDataAdapter<?>> allAdapters = new ArrayList<>();
  private final Set<Class<?>> noAdapter = new HashSet<>();

  public AdapterMap() {
  }

  public void register (ILootrDataAdapter<?> adapter) {
    Class<?> clazz = adapter.getAssignableClass();
    for (ILootrDataAdapter<?> otherAdapter : allAdapters) {
      Class<?> otherClazz = otherAdapter.getAssignableClass();
      if (clazz.isAssignableFrom(otherClazz) || otherClazz.isAssignableFrom(clazz)) {
        throw new IllegalArgumentException("Adapter class '" + clazz + "' conflicts with already existing class '" + otherClazz + "'");
      }
    }
    allAdapters.add(adapter);
  }

  @Nullable
  public <T> ILootrDataAdapter<T> findAdapter (T type) {
    Class<?> clazz = type.getClass();
    if (noAdapter.contains(clazz)) {
      return null;
    }
    ILootrDataAdapter<?> potentialAdapter = byClass.get(clazz);
    if (potentialAdapter == null) {
      for (ILootrDataAdapter<?> adapter : allAdapters) {
        if (adapter.getAssignableClass().isInstance(type)) {
          byClass.put(clazz, adapter);
          potentialAdapter = adapter;
          break;
        }
      }
    }
    if (potentialAdapter == null) {
      noAdapter.add(clazz);
      return null;
    }
    //noinspection unchecked
    return (ILootrDataAdapter<T>) potentialAdapter;
  }
}
