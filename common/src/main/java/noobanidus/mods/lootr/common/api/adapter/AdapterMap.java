package noobanidus.mods.lootr.common.api.adapter;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AdapterMap {
  private final Map<Class<?>, ILootrDataAdapter<?>> byClass = new ConcurrentHashMap<>();
  private final List<ILootrDataAdapter<?>> allAdapters = new ArrayList<>();
  private final Set<Class<?>> noAdapter = ConcurrentHashMap.newKeySet();

  public AdapterMap() {
  }

  public <T> void register(ILootrDataAdapter<T> adapter) {
    Class<T> clazz = adapter.getAssignableClass();
    for (ILootrDataAdapter<?> otherAdapter : allAdapters) {
      Class<?> otherClazz = otherAdapter.getAssignableClass();
      if (clazz.isAssignableFrom(otherClazz) || otherClazz.isAssignableFrom(clazz)) {
        throw new IllegalArgumentException("Adapter class '" + clazz + "' conflicts with already existing class '" + otherClazz + "'");
      }
    }
    allAdapters.add(adapter);
    byClass.clear();
    noAdapter.clear();
  }

  @Nullable
  public <T> ILootrDataAdapter<T> getAdapter(T type) {
    if (type == null) {
      return null;

    }
    Class<?> clazz = type.getClass();
    if (noAdapter.contains(clazz)) {
      return null;
    }
    ILootrDataAdapter<?> potentialAdapter = byClass.computeIfAbsent(clazz, clazz2 -> {
      for (ILootrDataAdapter<?> adapter : allAdapters) {
        if (adapter.getAssignableClass().isAssignableFrom(clazz2)) {
          return adapter;
        }
      }
      return null;
    });
    if (potentialAdapter == null) {
      noAdapter.add(clazz);
      return null;
    }
    //noinspection unchecked
    return (ILootrDataAdapter<T>) potentialAdapter;
  }
}
