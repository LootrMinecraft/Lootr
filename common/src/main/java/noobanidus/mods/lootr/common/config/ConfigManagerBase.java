package noobanidus.mods.lootr.common.config;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import noobanidus.mods.lootr.common.api.LootrAPI;

import java.util.*;
import java.util.function.Function;

public class ConfigManagerBase {
  protected static Set<String> validateStringList(Collection<? extends String> incomingList, String listKey) {
    Set<String> validatedList = new HashSet<>();
    for (String entry : incomingList) {
      if (entry == null || entry.isEmpty()) {
        LootrAPI.LOG.error("Error found when validating a configuration list for '" + listKey + "'. One of the entries is null or empty and cannot be converted to a String.");
        continue;
      }
      validatedList.add(entry);
    }
    return validatedList;
  }

  protected static Set<ResourceKey<Level>> validateDimensions(Collection<? extends String> incomingList, String listKey) {
    return validateResourceKeyList(incomingList, listKey, o -> ResourceKey.create(Registries.DIMENSION, o));
  }

  protected static <T> Set<ResourceKey<T>> validateResourceKeyList (Collection<? extends String> incomingList, String listKey, Function<ResourceLocation, ResourceKey<T>> builder) {
    Set<ResourceKey<T>> validatedList = new HashSet<>();
    for (String entry : incomingList) {
      if (entry == null || entry.isEmpty()) {
        throw new RuntimeException("Error found when validating a configuration list for '" + listKey + "'. One of the entries is null or empty and cannot be converted to a ResourceLocation.");
      }
      ResourceLocation location;
      try {
        location = ResourceLocation.parse(entry);
      } catch (Exception e) {
        throw new RuntimeException("Error found when validating a configuration list for '" + listKey + "'. The value found in the list, '" + entry + "', is not a valid ResourceLocation.", e);
      }

      try {
        validatedList.add(builder.apply(location));
      } catch (Exception e) {
        throw new RuntimeException("Error found when validating a configuration list for '" + listKey + "'. The value found in the list, '" + entry + "', is not valid to create a ResourceKey.", e);
      }
    }
    return validatedList;
  }
}
