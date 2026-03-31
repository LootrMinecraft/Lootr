package noobanidus.mods.lootr.common.api.config;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class ConfigManager {
  private static Set<String> DECAY_MODS = null;
  private static Set<ResourceKey<LootTable>> DECAY_TABLES = null;
  private static Set<String> REFRESH_MODS = null;
  private static Set<ResourceKey<LootTable>> REFRESH_TABLES = null;

  private static Set<ResourceKey<Level>> DIM_WHITELIST = null;
  private static Set<String> MODID_DIM_WHITELIST = null;
  private static Set<ResourceKey<Level>> DIM_BLACKLIST = null;
  private static Set<String> MODID_DIM_BLACKLIST = null;
  private static Set<ResourceKey<Level>> DECAY_DIMS = null;
  private static Set<ResourceKey<Level>> REFRESH_DIMS = null;
  private static Set<ResourceKey<LootTable>> LOOT_BLACKLIST = null;
  private static Set<String> LOOT_MODIDS = null;
  private static Set<ResourceKey<LootTable>> PROBLEMATIC_LOOT_TABLES = null;

  public static void reset() {
    MODID_DIM_WHITELIST = null;
    MODID_DIM_BLACKLIST = null;
    DIM_WHITELIST = null;
    DIM_BLACKLIST = null;
    LOOT_BLACKLIST = null;
    DECAY_MODS = null;
    DECAY_TABLES = null;
    DECAY_DIMS = null;
    LOOT_MODIDS = null;
    REFRESH_DIMS = null;
    REFRESH_MODS = null;
    REFRESH_TABLES = null;
    PROBLEMATIC_LOOT_TABLES = null;
    LootrAPI.refreshSections();
    LootrAPI.refreshServices();
  }

  public static Set<ResourceKey<Level>> getDimensionWhitelist() {
    if (DIM_WHITELIST == null) {
      DIM_WHITELIST = validateDimensions(LootrCommonConfig.Restrictions.dimensionWhitelist.get(), "dimension_whitelist");
    }
    return DIM_WHITELIST;
  }

  public static Set<String> getDimensionModidWhitelist() {
    if (MODID_DIM_WHITELIST == null) {
      MODID_DIM_WHITELIST = validateStringList(LootrCommonConfig.Restrictions.modidDimensionWhitelist.get(), "modid_dimension_whitelist");
    }
    return MODID_DIM_WHITELIST;
  }

  public static Set<ResourceKey<Level>> getDimensionBlacklist() {
    if (DIM_BLACKLIST == null) {
      DIM_BLACKLIST = validateDimensions(LootrCommonConfig.Restrictions.dimensionBlacklist.get(), "dimension_blacklist");
    }
    return DIM_BLACKLIST;
  }

  public static Set<String> getDimensionModidBlacklist() {
    if (MODID_DIM_BLACKLIST == null) {
      MODID_DIM_BLACKLIST = validateStringList(LootrCommonConfig.Restrictions.modidDimensionBlacklist.get(), "modid_dimension_blacklist");
    }
    return MODID_DIM_BLACKLIST;
  }

  public static Set<ResourceKey<Level>> getDecayDimensions() {
    if (DECAY_DIMS == null) {
      DECAY_DIMS = validateDimensions(LootrCommonConfig.Decay.decayDimensions.get(), "decay_dimensions");
    }
    return DECAY_DIMS;
  }

  public static Set<ResourceKey<Level>> getRefreshDimensions() {
    if (REFRESH_DIMS == null) {
      REFRESH_DIMS = validateDimensions(LootrCommonConfig.Refresh.refreshDimensions.get(), "refresh_dimensions");
    }
    return REFRESH_DIMS;
  }

  public static Set<ResourceKey<LootTable>> getLootBlacklist() {
    if (LOOT_BLACKLIST == null) {
      LOOT_BLACKLIST = validateResourceKeyList(LootrCommonConfig.Restrictions.lootTableBlacklist.get(), "loot_blacklist", o -> ResourceKey.create(Registries.LOOT_TABLE, o));
      // Fixes for #79 and #74
      LOOT_BLACKLIST.addAll(getProblematicLootTables());
    }
    return LOOT_BLACKLIST;
  }

  public static Set<String> getLootModidsBlacklist() {
    if (LOOT_MODIDS == null) {
      LOOT_MODIDS = validateStringList(LootrCommonConfig.Restrictions.lootTableModidBlacklist.get(), "loot_modid_blacklist");
    }
    return LOOT_MODIDS;
  }

  public static boolean isBlacklisted(ResourceKey<LootTable> table) {
    if (getLootBlacklist().contains(table)) {
      return true;
    }

    return getLootModidsBlacklist().contains(table.identifier().getNamespace());
  }

  public static Set<ResourceKey<LootTable>> getDecayingTables() {
    if (DECAY_TABLES == null) {
      DECAY_TABLES = validateResourceKeyList(LootrCommonConfig.Decay.decayLootTables.get(), "decay_loot_tables", o -> ResourceKey.create(Registries.LOOT_TABLE, o));
    }
    return DECAY_TABLES;
  }

  public static Set<String> getDecayMods() {
    if (DECAY_MODS == null) {
      DECAY_MODS = validateStringList(LootrCommonConfig.Decay.decayLootTableModids.get(), "decay_mods");
    }
    return DECAY_MODS;
  }

  public static Set<ResourceKey<LootTable>> getRefreshingTables() {
    if (REFRESH_TABLES == null) {
      REFRESH_TABLES = validateResourceKeyList(LootrCommonConfig.Refresh.refreshLootTables.get(), "refresh_tables", o -> ResourceKey.create(Registries.LOOT_TABLE, o));
    }
    return REFRESH_TABLES;
  }

  public static Set<String> getRefreshMods() {
    if (REFRESH_MODS == null) {
      REFRESH_MODS = validateStringList(LootrCommonConfig.Refresh.refreshLootTableModids.get(), "refresh_modids");
    }
    return REFRESH_MODS;
  }

  public static boolean isDimensionBlocked(ResourceKey<Level> key) {
    if (!getDimensionModidWhitelist().isEmpty() && !getDimensionModidWhitelist().contains(key.identifier()
        .getNamespace()) || getDimensionModidBlacklist().contains(key.identifier().getNamespace())) {
      return true;
    }

    return (!getDimensionWhitelist().isEmpty() && !getDimensionWhitelist().contains(key)) || getDimensionBlacklist().contains(key);
  }

  public static boolean isDimensionDecaying(ResourceKey<Level> key) {
    return getDecayDimensions().contains(key);
  }

  public static boolean isDimensionRefreshing(ResourceKey<Level> key) {
    return getRefreshDimensions().contains(key);
  }

  public static boolean isDecaying(ILootrInfoProvider tile) {
    if (LootrCommonConfig.Decay.decayAll) {
      return true;
    }
    if (tile.getInfoLootTable() != null) {
      if (getDecayingTables().contains(tile.getInfoLootTable())) {
        return true;
      }
      if (getDecayMods().contains(tile.getInfoLootTable().identifier().getNamespace())) {
        return true;
      }
    }
    if (LootrAPI.isTaggedStructurePresent((ServerLevel) tile.getInfoLevel(), ChunkPos.containing(tile.getInfoPos()), LootrTags.Structure.DECAY_STRUCTURES, tile.getInfoPos())) {
      return true;
    }
    return isDimensionDecaying(tile.getInfoDimension());
  }

  public static boolean isRefreshing(ILootrInfoProvider tile) {
    if (LootrCommonConfig.Refresh.refreshAll) {
      return true;
    }
    if (tile.getInfoLootTable() != null) {
      if (getRefreshingTables().contains(tile.getInfoLootTable())) {
        return true;
      }
      if (getRefreshMods().contains(tile.getInfoLootTable().identifier().getNamespace())) {
        return true;
      }
    }
    if (LootrAPI.isTaggedStructurePresent((ServerLevel) tile.getInfoLevel(), ChunkPos.containing(tile.getInfoPos()), LootrTags.Structure.REFRESH_STRUCTURES, tile.getInfoPos())) {
      return true;
    }
    return isDimensionRefreshing(tile.getInfoDimension());
  }


  public static boolean shouldNotify(int remaining) {
    int delay = LootrCommonConfig.Notifications.maximumNotificationDelay;
    return !LootrCommonConfig.Notifications.disableNotifications && (delay == -1 || remaining <= delay);
  }

  public static boolean shouldPerformPiecewiseCheck() {
    return LootrCommonConfig.Conversion.performPiecewiseCheck;
  }

  public static boolean isVanillaTextures() {
    return LootrClientConfig.Textures.useVanillaTextures;
  }

  public static Set<ResourceKey<LootTable>> getProblematicLootTables() {
    if (PROBLEMATIC_LOOT_TABLES == null) {
      PROBLEMATIC_LOOT_TABLES = validateResourceKeyList(LootrCommonConfig.Restrictions.problematicLootTables.get(), "problematic_loot_tables", o -> ResourceKey.create(Registries.LOOT_TABLE, o));
    }
    return PROBLEMATIC_LOOT_TABLES;
  }

  protected static Set<String> validateStringList(String[] incomingList, String listKey) {
    return validateStringList(List.of(incomingList), listKey);
  }

  protected static Set<String> validateStringList(Collection<? extends String> incomingList, String listKey) {
    Set<String> validatedList = new HashSet<>();
    for (String entry : incomingList) {
      if (entry == null || entry.isEmpty()) {
        LootrAPI.LOG.error("Error found when validating a configuration list for '{}'. One of the entries is null or empty and cannot be converted to a String.", listKey);
        continue;
      }
      validatedList.add(entry);
    }
    return validatedList;
  }

  protected static Set<ResourceKey<Level>> validateDimensions(String[] incomingList, String listKey) {
    return validateDimensions(List.of(incomingList), listKey);
  }

  protected static Set<ResourceKey<Level>> validateDimensions(Collection<? extends String> incomingList, String listKey) {
    return validateResourceKeyList(incomingList, listKey, o -> ResourceKey.create(Registries.DIMENSION, o));
  }

  protected static <T> Set<ResourceKey<T>> validateResourceKeyList(String[] incomingList, String listKey, Function<Identifier, ResourceKey<T>> builder) {
    return validateResourceKeyList(List.of(incomingList), listKey, builder);
  }

  protected static <T> Set<ResourceKey<T>> validateResourceKeyList(Collection<? extends String> incomingList, String listKey, Function<Identifier, ResourceKey<T>> builder) {
    Set<ResourceKey<T>> validatedList = new HashSet<>();
    for (String entry : incomingList) {
      if (entry == null || entry.isEmpty()) {
        throw new RuntimeException("Error found when validating a configuration list for '" + listKey + "'. One of the entries is null or empty and cannot be converted to a Identifier.");
      }
      Identifier location;
      try {
        location = Identifier.parse(entry);
      } catch (Exception e) {
        throw new RuntimeException("Error found when validating a configuration list for '" + listKey + "'. The value found in the list, '" + entry + "', is not a valid Identifier.", e);
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
