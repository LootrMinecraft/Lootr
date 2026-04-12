package noobanidus.mods.lootr.common.api.config;

import com.teamresourceful.resourcefulconfig.api.loader.Configurator;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.data.ILootrContainerInstance;
import noobanidus.mods.lootr.common.api.interfaces.annotation.DefaultCandidate;

import java.util.*;
import java.util.function.Function;

public class LootrConfig {
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

  private static List<String> LAST_DECAY_DIMS = null;
  private static List<String> LAST_REFRESH_DIMS = null;
  private static List<String> LAST_DECAY_MODS = null;
  private static List<String> LAST_DECAY_TABLES = null;
  private static List<String> LAST_REFRESH_MODS = null;
  private static List<String> LAST_REFRESH_TABLES = null;
  private static List<String> LAST_DIM_WHITELIST = null;
  private static List<String> LAST_MODID_DIM_WHITELIST = null;
  private static List<String> LAST_DIM_BLACKLIST = null;
  private static List<String> LAST_MODID_DIM_BLACKLIST = null;
  private static List<String> LAST_LOOT_BLACKLIST = null;
  private static List<String> LAST_LOOT_MODIDS = null;
  private static List<String> LAST_PROBLEMATIC_LOOT_TABLES = null;

  // TODO: This is fine but it should be split into client/etc observables
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

  private static Configurator configurator;

  public static Configurator getConfigurator () {
    if (configurator == null) {
      configurator = new Configurator(LootrAPI.MODID);
    }

    return configurator;
  }

  public static Set<ResourceKey<Level>> getDimensionWhitelist() {
    if (DIM_WHITELIST == null || !LootrCommonConfig.Restrictions.dimensionWhitelist.equals(LAST_DIM_WHITELIST)) {
      LAST_DIM_WHITELIST = new ArrayList<>(LootrCommonConfig.Restrictions.dimensionWhitelist);
      DIM_WHITELIST = validateDimensions(LootrCommonConfig.Restrictions.dimensionWhitelist, "dimension_whitelist");
    }
    return DIM_WHITELIST;
  }

  public static Set<String> getDimensionModIdWhitelist() {
    if (MODID_DIM_WHITELIST == null || !LootrCommonConfig.Restrictions.modidDimensionWhitelist.equals(LAST_MODID_DIM_WHITELIST)) {
      LAST_MODID_DIM_WHITELIST = new ArrayList<>(LootrCommonConfig.Restrictions.modidDimensionWhitelist);
      MODID_DIM_WHITELIST = validateStringList(LootrCommonConfig.Restrictions.modidDimensionWhitelist, "modid_dimension_whitelist");
    }
    return MODID_DIM_WHITELIST;
  }

  public static Set<ResourceKey<Level>> getDimensionBlacklist() {
    if (DIM_BLACKLIST == null || !LootrCommonConfig.Restrictions.dimensionBlacklist.equals(LAST_DIM_BLACKLIST)) {
      LAST_DIM_BLACKLIST = new ArrayList<>(LootrCommonConfig.Restrictions.dimensionBlacklist);
      DIM_BLACKLIST = validateDimensions(LootrCommonConfig.Restrictions.dimensionBlacklist, "dimension_blacklist");
    }
    return DIM_BLACKLIST;
  }

  public static Set<String> getDimensionModIdBlacklist() {
    if (MODID_DIM_BLACKLIST == null || !LootrCommonConfig.Restrictions.modidDimensionBlacklist.equals(LAST_MODID_DIM_BLACKLIST)) {
      LAST_MODID_DIM_BLACKLIST = new ArrayList<>(LootrCommonConfig.Restrictions.modidDimensionBlacklist);
      MODID_DIM_BLACKLIST = validateStringList(LootrCommonConfig.Restrictions.modidDimensionBlacklist, "modid_dimension_blacklist");
    }
    return MODID_DIM_BLACKLIST;
  }

  public static Set<ResourceKey<Level>> getDecayDimensions() {
    if (DECAY_DIMS == null || !LootrCommonConfig.Decay.decayDimensions.equals(LAST_DECAY_DIMS)) {
      LAST_DECAY_DIMS = new ArrayList<>(LootrCommonConfig.Decay.decayDimensions);
      DECAY_DIMS = validateDimensions(LootrCommonConfig.Decay.decayDimensions, "decay_dimensions");
    }
    return DECAY_DIMS;
  }

  public static Set<ResourceKey<Level>> getRefreshDimensions() {
    if (REFRESH_DIMS == null || !LootrCommonConfig.Refresh.refreshDimensions.equals(LAST_REFRESH_DIMS)) {
      LAST_REFRESH_DIMS = new ArrayList<>(LootrCommonConfig.Refresh.refreshDimensions);
      REFRESH_DIMS = validateDimensions(LootrCommonConfig.Refresh.refreshDimensions, "refresh_dimensions");
    }
    return REFRESH_DIMS;
  }

  public static Set<ResourceKey<LootTable>> getLootTableBlacklist() {
    if (LOOT_BLACKLIST == null || !LootrCommonConfig.Restrictions.lootTableBlacklist.equals(LAST_LOOT_BLACKLIST)) {
      LAST_LOOT_BLACKLIST = new ArrayList<>(LootrCommonConfig.Restrictions.lootTableBlacklist);
      LOOT_BLACKLIST = validateResourceKeyList(LootrCommonConfig.Restrictions.lootTableBlacklist, "loot_blacklist", o -> ResourceKey.create(Registries.LOOT_TABLE, o));
      // Fixes for #79 and #74
      LOOT_BLACKLIST.addAll(getProblematicLootTables());
    }
    return LOOT_BLACKLIST;
  }

  public static Set<String> getLootModIdsBlacklist() {
    if (LOOT_MODIDS == null || !LootrCommonConfig.Restrictions.lootTableModidBlacklist.equals(LAST_LOOT_MODIDS)) {
      LAST_LOOT_MODIDS = new ArrayList<>(LootrCommonConfig.Restrictions.lootTableModidBlacklist);
      LOOT_MODIDS = validateStringList(LootrCommonConfig.Restrictions.lootTableModidBlacklist, "loot_modid_blacklist");
    }
    return LOOT_MODIDS;
  }

  public static Set<ResourceKey<LootTable>> getDecayingTables() {
    if (DECAY_TABLES == null || !LootrCommonConfig.Decay.decayLootTables.equals(LAST_DECAY_TABLES)) {
      LAST_DECAY_TABLES = new ArrayList<>(LootrCommonConfig.Decay.decayLootTables);
      DECAY_TABLES = validateResourceKeyList(LootrCommonConfig.Decay.decayLootTables, "decay_loot_tables", o -> ResourceKey.create(Registries.LOOT_TABLE, o));
    }
    return DECAY_TABLES;
  }

  public static Set<String> getDecayMods() {
    if (DECAY_MODS == null || !LootrCommonConfig.Decay.decayLootTableModids.equals(LAST_DECAY_MODS)) {
      LAST_DECAY_MODS = new ArrayList<>(LootrCommonConfig.Decay.decayLootTableModids);
      DECAY_MODS = validateStringList(LootrCommonConfig.Decay.decayLootTableModids, "decay_mods");
    }
    return DECAY_MODS;
  }

  public static Set<ResourceKey<LootTable>> getRefreshingTables() {
    if (REFRESH_TABLES == null || !LootrCommonConfig.Refresh.refreshLootTables.equals(LAST_REFRESH_TABLES)) {
      LAST_REFRESH_TABLES = new ArrayList<>(LootrCommonConfig.Refresh.refreshLootTables);
      REFRESH_TABLES = validateResourceKeyList(LootrCommonConfig.Refresh.refreshLootTables, "refresh_tables", o -> ResourceKey.create(Registries.LOOT_TABLE, o));
    }
    return REFRESH_TABLES;
  }

  public static Set<String> getRefreshLootTableModIds() {
    if (REFRESH_MODS == null || !LootrCommonConfig.Refresh.refreshLootTableModids.equals(LAST_REFRESH_MODS)) {
      LAST_REFRESH_MODS = new ArrayList<>(LootrCommonConfig.Refresh.refreshLootTableModids);
      REFRESH_MODS = validateStringList(LootrCommonConfig.Refresh.refreshLootTableModids, "refresh_modids");
    }
    return REFRESH_MODS;
  }

  public static Set<ResourceKey<LootTable>> getProblematicLootTables() {
    if (PROBLEMATIC_LOOT_TABLES == null || !LootrCommonConfig.Restrictions.problematicLootTables.equals(LAST_PROBLEMATIC_LOOT_TABLES)) {
      LAST_PROBLEMATIC_LOOT_TABLES = new ArrayList<>(LootrCommonConfig.Restrictions.problematicLootTables);
      PROBLEMATIC_LOOT_TABLES = validateResourceKeyList(LootrCommonConfig.Restrictions.problematicLootTables, "problematic_loot_tables", o -> ResourceKey.create(Registries.LOOT_TABLE, o));
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
        LootrAPI.LOG.error("Error found when validating a configuration list for '{}'. One of the entries is null or empty and cannot be converted to a Identifier.", listKey, new RuntimeException());
        continue;
      }
      Identifier location;
      try {
        location = Identifier.parse(entry);
      } catch (Exception e) {
        LootrAPI.LOG.error("Error found when validating a configuration list for '{}'. The value found in the list, '{}', is not a valid Identifier.", listKey, entry, e);
        continue;
      }

      try {
        validatedList.add(builder.apply(location));
      } catch (Exception e) {
        LootrAPI.LOG.error("Error found when validating a configuration list for '{}'. The value found in the list, '{}', is not valid to create a ResourceKey.", listKey, entry, e);
      }
    }
    return validatedList;
  }
}
