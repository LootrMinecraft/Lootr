package noobanidus.mods.lootr.common.api.filter;

import java.util.List;

/**
 * Filters are loaded via this class using services. Implementations of this
 * provider should be listed (fully qualified name) in a file located at:
 * META-INF/services/noobanidus.mods.lootr.common.api.filter.ILootrFilterProvider
 */
public interface ILootrFilterProvider {
  List<ILootrFilter> getFilters();
}
