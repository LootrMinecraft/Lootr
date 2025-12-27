package noobanidus.mods.lootr.neoforge.config;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.common.config.ConfigManagerBase;
import noobanidus.mods.lootr.common.impl.LootrServiceRegistry;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@EventBusSubscriber(modid = LootrAPI.MODID)
public class ConfigManager extends ConfigManagerBase {
  public static final ModConfigSpec.BooleanValue REPORT_UNRESOLVED_TABLES;
  public static final ModConfigSpec.BooleanValue RANDOMISE_SEED;
  public static final ModConfigSpec.BooleanValue DISABLE;
  public static final ModConfigSpec.IntValue MAXIMUM_AGE;
  @Deprecated
  public static final ModConfigSpec.BooleanValue CONVERT_MINESHAFTS;
  public static final ModConfigSpec.BooleanValue CONVERT_ELYTRAS_TO_CHESTS;
  public static final ModConfigSpec.BooleanValue CONVERT_ELYTRAS_TO_ITEM_FRAMES;
  public static final ModConfigSpec.BooleanValue CONVERT_ITEM_FRAMES;
  public static final ModConfigSpec.BooleanValue PERFORM_PIECEWISE_CHECK;
  public static final ModConfigSpec.BooleanValue BYPASS_SPAWN_PROTECTION;
  // Breaking
  public static final ModConfigSpec.BooleanValue DISABLE_BREAK;
  public static final ModConfigSpec.BooleanValue ENABLE_BREAK;
  public static final ModConfigSpec.BooleanValue ENABLE_FAKE_PLAYER_BREAK;
  public static final ModConfigSpec.BooleanValue CHECK_WORLD_BORDER;
  public static final ModConfigSpec.BooleanValue BRUSHABLES_SELF_SUPPORT;
  public static final ModConfigSpec.BooleanValue ITEM_FRAMES_SELF_SUPPORT;
  // Whitelist/blacklist (loot table, modid, dimension)
  public static final ModConfigSpec.ConfigValue<List<? extends String>> DIMENSION_WHITELIST;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> DIMENSION_BLACKLIST;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> LOOT_TABLE_BLACKLIST;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> PROBLEMATIC_LOOT_TABLES;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> LOOT_MODID_BLACKLIST;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> MODID_DIMENSION_WHITELIST;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> MODID_DIMENSION_BLACKLIST;
  // Decay
  public static final ModConfigSpec.IntValue DECAY_VALUE;
  public static final ModConfigSpec.BooleanValue DECAY_ALL;
  public static final ModConfigSpec.BooleanValue PERFORM_DECAY_WHILE_TICKING;
  public static final ModConfigSpec.BooleanValue START_DECAY_WHILE_TICKING;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> DECAY_MODIDS;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> DECAY_LOOT_TABLES;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> DECAY_DIMENSIONS;
  public static final ModConfigSpec.BooleanValue REPLACE_WHEN_DECAYED;
  // Refresh
  public static final ModConfigSpec.IntValue REFRESH_VALUE;
  public static final ModConfigSpec.BooleanValue REFRESH_ALL;
  public static final ModConfigSpec.BooleanValue PERFORM_REFRESH_WHILE_TICKING;
  public static final ModConfigSpec.BooleanValue START_REFRESH_WHILE_TICKING;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> REFRESH_MODIDS;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> REFRESH_LOOT_TABLES;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> REFRESH_DIMENSIONS;
  public static final ModConfigSpec.BooleanValue POWER_COMPARATORS;
  public static final ModConfigSpec.BooleanValue BLAST_RESISTANT;
  public static final ModConfigSpec.BooleanValue BLAST_IMMUNE;
  public static final ModConfigSpec.BooleanValue SHOULD_DROP_PLAYER_LOOT;
  public static final ModConfigSpec.IntValue NOTIFICATION_DELAY;
  public static final ModConfigSpec.BooleanValue DISABLE_NOTIFICATIONS;
  public static final ModConfigSpec.BooleanValue DISABLE_MESSAGE_STYLES;
  public static final ModConfigSpec.BooleanValue TRAPPED_CUSTOM;
  public static final ModConfigSpec.BooleanValue SHOULD_WARN_NO_LOOT_TABLE_AT_GENERATION;
  // Client-only
  public static final ModConfigSpec.BooleanValue VANILLA_TEXTURES;
  public static final ModConfigSpec.BooleanValue NEW_TEXTURES;
  private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
  private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
  public static ModConfigSpec COMMON_CONFIG;
  public static ModConfigSpec CLIENT_CONFIG;

  // Caches
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

  static {
    COMMON_BUILDER.push("conversion").comment("configuration options for the conversion of chests");
    DISABLE = COMMON_BUILDER.comment("if true, no chests will be converted").define("disable", false);
    RANDOMISE_SEED = COMMON_BUILDER.comment("determine whether or not loot generated is the same for all players using the provided seed, or randomised per player").define("randomise_seed", true);
    MAXIMUM_AGE = COMMON_BUILDER.comment("the maximum age for containers; entries above this age will be discarded [default: 60 * 20 * 15, fifteen minutes] [note: the value 6000 will be corrected to 18000. if you wish to use 6000, please use 6001 or 5999.]").defineInRange("max_age", 60 * 20 * 15, 0, Integer.MAX_VALUE);
    BYPASS_SPAWN_PROTECTION = COMMON_BUILDER.comment("if true, accessing a Lootr container will bypass spawn protection").define("bypass_spawn_protection", true);
    CONVERT_MINESHAFTS = COMMON_BUILDER.comment("whether or not mineshaft chest minecarts should be converted to standard loot chests").define("convert_mineshafts", true);
    CONVERT_ELYTRAS_TO_CHESTS = COMMON_BUILDER.comment("whether or not the Elytra item frame should be converted into a standard loot chest with a guaranteed elytra").define("convert_elytras_to_chests", false);
    CONVERT_ELYTRAS_TO_ITEM_FRAMES = COMMON_BUILDER.comment("whether or not the Elytra item frame should be converted into a Lootr item frame with an elytra inside").define("convert_elytras_to_item_frames", true);
    CONVERT_ITEM_FRAMES = COMMON_BUILDER.comment("whether or not item frames with items in them generated by structures should be converted into Lootr item frames").define("convert_item_frames", true);
    SHOULD_WARN_NO_LOOT_TABLE_AT_GENERATION = COMMON_BUILDER.comment("whether or not to output a warning to the log when a suitable block entity has no loot table and is skipped during conversion").define("skip_logging_no_loot_table_at_generation", true);
    REPORT_UNRESOLVED_TABLES = COMMON_BUILDER.comment("lootr will automatically log all unresolved tables (i.e., for containers that have a loot table associated with them but, for whatever reason, the lookup for this table returns empty). setting this option to true additionally informs players when they open containers.").define("report_unresolved_tables", false);
    CHECK_WORLD_BORDER = COMMON_BUILDER.comment("disregard chests and chunks that are outside of the world border; enable this option if you are using a world border and are suffering consistent TPS issues; if you change the world border, you will need to restart your client").define("check_world_border", false);
    PERFORM_PIECEWISE_CHECK = COMMON_BUILDER.comment("checks structure pieces as well as structure starts when determining if a structure contains a position, more accurate but may cause lag (default true)").define("perform_piecewise_check", true);
    COMMON_BUILDER.pop();
    COMMON_BUILDER.push("whitelist").comment("configuration for specific whitelisting and blacklisting of dimensions, loot tables and modids");
    List<? extends String> empty = Collections.emptyList();
    Predicate<Object> validator = o -> o instanceof String && ((String) o).contains(":");
    Predicate<Object> modidValidator = o -> o instanceof String && !((String) o).contains(":");
    DIMENSION_WHITELIST = COMMON_BUILDER.comment("list of dimensions (to the exclusion of all others) that loot chest should be replaced in (default: blank, allowing all dimensions, e.g., [\"minecraft:overworld\", \"minecraft:the_end\"])").defineList("dimension_whitelist", empty, () -> "", validator);
    DIMENSION_BLACKLIST = COMMON_BUILDER.comment("list of dimensions that loot chests should not be replaced in (default: blank, allowing all dimensions, format e.g., [\"minecraft:overworld\", \"minecraft:the_end\"])").defineList("dimension_blacklist", empty, () -> "", validator);
    MODID_DIMENSION_BLACKLIST = COMMON_BUILDER.comment("list of dimensions by modid that loot chests should not be replaced in (default: blank, allowing all modids, format e.g., [\"minecraft", "othermod\"])").defineList("modid_dimension_blacklist", empty, () -> "", modidValidator);
    MODID_DIMENSION_WHITELIST = COMMON_BUILDER.comment("list of dimensions by modid that loot chest should be replaced in (default: blank, allowing all modids, format e.g., [\"minecraft", "othermod\"])").defineList("modid_dimension_whitelist", empty, () -> "", modidValidator);
    LOOT_TABLE_BLACKLIST = COMMON_BUILDER.comment("list of loot tables which shouldn't be converted (in the format of [\"modid:loot_table\", \"othermodid:other_loot_table\"])").defineList("loot_table_blacklist", empty, () -> "", validator);
    LOOT_MODID_BLACKLIST = COMMON_BUILDER.comment("list of modids whose loot tables shouldn't be converted (in the format of [\"modid\", \"other_modid\"])").defineList("loot_modid_blacklist", empty, () -> "", modidValidator);
    PROBLEMATIC_LOOT_TABLES = COMMON_BUILDER.comment("list of loot tables whose conversion causes problems (in the same format as `loot_table_blacklist`)").defineList("problematic_loot_tables", LootrAPI.PROBLEMATIC_CHESTS.stream().map(ResourceLocation::toString).toList(), () -> "", validator);
    COMMON_BUILDER.pop();
    COMMON_BUILDER.push("breaking").comment("configuration options for breaking containers");
    DISABLE_BREAK = COMMON_BUILDER.comment("prevent the destruction of Lootr chests except while sneaking in creative mode").define("disable_break", false);
    ENABLE_BREAK = COMMON_BUILDER.comment("allow the destruction of Lootr chests regardless. overrides `disable_break`").define("enable_break", false);
    ENABLE_FAKE_PLAYER_BREAK = COMMON_BUILDER.comment("allows fake players to destroy Lootr chests without having to sneak, overrides the `disable_break` option for fake players").define("enable_fake_player_break", false);
    BLAST_RESISTANT = COMMON_BUILDER.comment("lootr chests cannot be destroyed by creeper or TNT explosions").define("blast_resistant", false);
    BLAST_IMMUNE = COMMON_BUILDER.comment("lootr chests cannot be destroyed by any explosion").define("blast_immune", false);
    BRUSHABLES_SELF_SUPPORT = COMMON_BUILDER.comment("lootr brushable blocks will not fall if the blocks under them are broken").define("brushables_self_support", false);
    ITEM_FRAMES_SELF_SUPPORT = COMMON_BUILDER.comment("lootr item frames will not break if the blocks they are attached to are broken").define("item_frames_self_support", false);
    SHOULD_DROP_PLAYER_LOOT = COMMON_BUILDER.comment("lootr chests will drop the contents of the player-specific inventory (generated when not generated) when broken").define("should_drop_player_loot", false);
    COMMON_BUILDER.pop();
    COMMON_BUILDER.push("power").comment("configuration options for comparators and redstone power");
    POWER_COMPARATORS = COMMON_BUILDER.comment("when true, comparators on Lootr containers will give an output of 1; when false, they will give an output of 0").define("power_comparators", true);
    TRAPPED_CUSTOM = COMMON_BUILDER.comment("when true, custom inventories will act like trapped chests when opened").define("trapped_custom", false);
    COMMON_BUILDER.pop();
    COMMON_BUILDER.push("notifications").comment("configuration options for notifications");
    DISABLE_NOTIFICATIONS = COMMON_BUILDER.comment("prevent notifications of decaying or refreshed chests").define("disable_notifications", false);
    NOTIFICATION_DELAY = COMMON_BUILDER.comment("maximum time (in ticks) remaining on a chest before a notification for refreshing or decaying is sent to a player (default 30 seconds, -1 for no delay)").defineInRange("notification_delay", 30 * 20, -1, Integer.MAX_VALUE);
    DISABLE_MESSAGE_STYLES = COMMON_BUILDER.comment("disables styling of breaking, decaying and refreshing messages sent to players").define("disable_message_styles", false);
    COMMON_BUILDER.pop();
    COMMON_BUILDER.push("decay").comment("configuration options for decaying containers");
    DECAY_VALUE = COMMON_BUILDER.comment("how long (in ticks) a decaying loot containers should take to decay (default 5 minutes = 5 * 60 * 20)").defineInRange("decay_value", 5 * 60 * 20, 0, Integer.MAX_VALUE);
    DECAY_LOOT_TABLES = COMMON_BUILDER.comment("list of loot tables which will decay (default blank, meaning no chests decay, in the format of (in the format of [\"modid:loot_table\", \"othermodid:other_loot_table\"])").defineList("decay_loot_tables", empty, validator);
    DECAY_MODIDS = COMMON_BUILDER.comment("list of mod IDs whose loot tables will decay (default blank, meaning no chests decay, in the format [\"modid\", \"othermodid\"])").defineList("decay_modids", empty, o -> o instanceof String);
    DECAY_DIMENSIONS = COMMON_BUILDER.comment("list of dimensions where loot chests should automatically decay (default: blank, e.g., [\"minecraft:the_nether\", \"minecraft:the_end\"])").defineList("decay_dimensions", empty, validator);
    REPLACE_WHEN_DECAYED = COMMON_BUILDER.comment("when true, decayed containers will be replaced with their equivalent vanilla block instead of being removed").define("replace_when_decayed", false);
    PERFORM_DECAY_WHILE_TICKING = COMMON_BUILDER.comment("containers that have already been marked as decaying will be decayed during level tick as well as when next trapped").define("perform_decay_while_ticking", true);
    START_DECAY_WHILE_TICKING = COMMON_BUILDER.comment("containers that have not yet been marked as decaying will be marked for decay during level tick as well as when next trapped").define("start_decay_while_ticking", false);
    DECAY_ALL = COMMON_BUILDER.comment("overriding decay_loot_tables, decay_modids and decay_dimensions: all chests will decay after being trapped for the first time").define("decay_all", false);
    COMMON_BUILDER.pop();
    COMMON_BUILDER.push("refresh").comment("configuration options for refreshing containers");
    REFRESH_VALUE = COMMON_BUILDER.comment("how long (in ticks) a refreshing loot containers should take to refresh their contents (default 20 minutes = 20 * 60 * 20)").defineInRange("refresh_value", 20 * 60 * 20, 0, Integer.MAX_VALUE);
    REFRESH_LOOT_TABLES = COMMON_BUILDER.comment("list of loot tables which will refresh (default blank, meaning no chests refresh, in the format of [\"modid:loot_table\", \"othermodid:loot_table\"])").defineList("refresh_loot_tables", empty, validator);
    REFRESH_MODIDS = COMMON_BUILDER.comment("list of mod IDs whose loot tables will refresh (default blank, meaning no chests refresh, in the format of [\"modid\", \"othermodid\"])").defineList("refresh_modids", empty, o -> o instanceof String);
    REFRESH_DIMENSIONS = COMMON_BUILDER.comment("list of dimensions where loot chests should automatically refresh (default: blank, e.g., [\"minecraft:overworld\", \"othermod:otherdimension\"])").defineList("refresh_dimensions", empty, validator);
    PERFORM_REFRESH_WHILE_TICKING = COMMON_BUILDER.comment("containers that have already been marked as refreshing will be refreshed during level tick as well as when next trapped").define("perform_refresh_while_ticking", true);
    START_REFRESH_WHILE_TICKING = COMMON_BUILDER.comment("containers that have not yet been marked as refreshing will be marked for refresh during level tick as well as when next trapped").define("start_refresh_while_ticking", true);
    REFRESH_ALL = COMMON_BUILDER.comment("overriding refresh_loot_tables, refresh_modids and refresh_dimensions: all chests will refresh after being trapped for the first time").define("refresh_all", false);
    COMMON_BUILDER.pop();
    COMMON_CONFIG = COMMON_BUILDER.build();
    CLIENT_BUILDER.push("textures").comment("configuration options for textures");
    VANILLA_TEXTURES = CLIENT_BUILDER.comment("set to true to use vanilla textures instead of Lootr special textures. Note: this will prevent previously opened chests from rendering differently").define("vanilla_textures", false);
    NEW_TEXTURES = CLIENT_BUILDER.comment("set to true to use the new Lootr textures").define("new_textures", true);
    CLIENT_BUILDER.pop();
    CLIENT_CONFIG = CLIENT_BUILDER.build();
  }

  @SubscribeEvent
  public static void reloadConfig(ModConfigEvent.Reloading event) {
    configEvent(event);
  }

  @SubscribeEvent
  public static void loadConfig(ModConfigEvent.Loading event) {
    configEvent(event);
  }

  public static void configEvent(ModConfigEvent event) {
    if (event.getConfig().getType() == ModConfig.Type.COMMON) {
      LootrServiceRegistry.clearReplacements();
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
    } else if (event.getConfig().getType() == ModConfig.Type.CLIENT) {
      LootrAPI.refreshSections();
    }
  }

  public static Set<ResourceKey<Level>> getDimensionWhitelist() {
    if (DIM_WHITELIST == null) {
      DIM_WHITELIST = validateDimensions(DIMENSION_WHITELIST.get(), "dimension_whitelist");
    }
    return DIM_WHITELIST;
  }

  public static Set<String> getDimensionModidWhitelist() {
    if (MODID_DIM_WHITELIST == null) {
      MODID_DIM_WHITELIST = validateStringList(MODID_DIMENSION_WHITELIST.get(), "modid_dimension_whitelist");
    }
    return MODID_DIM_WHITELIST;
  }

  public static Set<ResourceKey<Level>> getDimensionBlacklist() {
    if (DIM_BLACKLIST == null) {
      DIM_BLACKLIST = validateDimensions(DIMENSION_BLACKLIST.get(), "dimension_blacklist");
    }
    return DIM_BLACKLIST;
  }

  public static Set<String> getDimensionModidBlacklist() {
    if (MODID_DIM_BLACKLIST == null) {
      MODID_DIM_BLACKLIST = validateStringList(MODID_DIMENSION_BLACKLIST.get(), "modid_dimension_blacklist");
    }
    return MODID_DIM_BLACKLIST;
  }

  public static Set<ResourceKey<Level>> getDecayDimensions() {
    if (DECAY_DIMS == null) {
      DECAY_DIMS = validateDimensions(DECAY_DIMENSIONS.get(), "decay_dimensions");
    }
    return DECAY_DIMS;
  }

  public static Set<ResourceKey<Level>> getRefreshDimensions() {
    if (REFRESH_DIMS == null) {
      REFRESH_DIMS = validateDimensions(REFRESH_DIMENSIONS.get(), "refresh_dimensions");
    }
    return REFRESH_DIMS;
  }

  private static Set<ResourceKey<LootTable>> getProblematicChests () {
    return validateResourceKeyList(PROBLEMATIC_LOOT_TABLES.get(), "problematic_loot_tables", o -> ResourceKey.create(Registries.LOOT_TABLE, o));
  }

  public static Set<ResourceKey<LootTable>> getLootBlacklist() {
    if (LOOT_BLACKLIST == null) {
      LOOT_BLACKLIST = validateResourceKeyList(LOOT_TABLE_BLACKLIST.get(), "loot_table_blacklist", o -> ResourceKey.create(Registries.LOOT_TABLE, o));
      // Fixes for #79 and #74
      LOOT_BLACKLIST.addAll(getProblematicChests());
    }
    return LOOT_BLACKLIST;
  }

  public static Set<String> getLootModids() {
    if (LOOT_MODIDS == null) {
      LOOT_MODIDS = validateStringList(LOOT_MODID_BLACKLIST.get(), "loot_modid_blacklist");
    }
    return LOOT_MODIDS;
  }

  public static boolean isLootTableBlacklisted(ResourceKey<LootTable> table) {
    if (!getLootBlacklist().isEmpty() && getLootBlacklist().contains(table)) {
      return true;
    }

    return !getLootModids().isEmpty() && getLootModids().contains(table.location().getNamespace());
  }

  public static Set<ResourceKey<LootTable>> getDecayingTables() {
    if (DECAY_TABLES == null) {
      DECAY_TABLES = validateResourceKeyList(DECAY_LOOT_TABLES.get(), "decay_loot_tables", o -> ResourceKey.create(Registries.LOOT_TABLE, o));
    }
    return DECAY_TABLES;
  }

  public static Set<String> getDecayMods() {
    if (DECAY_MODS == null) {
      DECAY_MODS = validateStringList(DECAY_MODIDS.get(), "decay_modids");
    }
    return DECAY_MODS;
  }

  public static Set<ResourceKey<LootTable>> getRefreshingTables() {
    if (REFRESH_TABLES == null) {
      REFRESH_TABLES = validateResourceKeyList(REFRESH_LOOT_TABLES.get(), "refreshing_loot_tables", o -> ResourceKey.create(Registries.LOOT_TABLE, o));
    }
    return REFRESH_TABLES;
  }

  public static Set<String> getRefreshMods() {
    if (REFRESH_MODS == null) {
      REFRESH_MODS = validateStringList(REFRESH_MODIDS.get(), "refresh_modids");
    }
    return REFRESH_MODS;
  }

  public static boolean isDimensionBlocked(ResourceKey<Level> key) {
    if (!getDimensionModidWhitelist().isEmpty() && !getDimensionModidWhitelist().contains(key.location().getNamespace()) || getDimensionModidBlacklist().contains(key.location().getNamespace())) {
      return true;
    }

    return (!getDimensionWhitelist().isEmpty() && !getDimensionWhitelist().contains(key)) || getDimensionBlacklist().contains(key);
  }

  public static boolean isDimensionDecaying(ResourceKey<Level> key) {
    return !getDecayDimensions().isEmpty() && getDecayDimensions().contains(key);
  }

  public static boolean isDimensionRefreshing(ResourceKey<Level> key) {
    return !getRefreshDimensions().isEmpty() && getRefreshDimensions().contains(key);
  }

  public static boolean isDecaying(ILootrInfoProvider provider) {
    if (DECAY_ALL.get()) {
      return true;
    }
    if (provider.getInfoLootTable() != null) {
      if (!getDecayingTables().isEmpty() && getDecayingTables().contains(provider.getInfoLootTable())) {
        return true;
      }
      if (!getDecayMods().isEmpty() && getDecayMods().contains(provider.getInfoLootTable().location().getNamespace())) {
        return true;
      }
    }
    if (LootrAPI.isTaggedStructurePresent((ServerLevel)provider.getInfoLevel(), new ChunkPos(provider.getInfoPos()), LootrTags.Structure.DECAY_STRUCTURES, provider.getInfoPos())) {
      return true;
    }
    return isDimensionDecaying(provider.getInfoDimension());
  }

  public static boolean isRefreshing(ILootrInfoProvider provider) {
    if (REFRESH_ALL.get()) {
      return true;
    }
    if (provider.getInfoLootTable() != null) {
      if (!getRefreshingTables().isEmpty() && getRefreshingTables().contains(provider.getInfoLootTable())) {
        return true;
      }
      if (!getRefreshMods().isEmpty() && getRefreshMods().contains(provider.getInfoLootTable().location().getNamespace())) {
        return true;
      }
    }
    if (LootrAPI.isTaggedStructurePresent((ServerLevel)provider.getInfoLevel(), new ChunkPos(provider.getInfoPos()), LootrTags.Structure.REFRESH_STRUCTURES, provider.getInfoPos())) {
      return true;
    }
    return isDimensionRefreshing(provider.getInfoDimension());
  }


  public static boolean shouldNotify(int remaining) {
    int delay = NOTIFICATION_DELAY.get();
    return !DISABLE_NOTIFICATIONS.get() && (delay == -1 || remaining <= delay);
  }

  public static boolean isVanillaTextures() {
    return VANILLA_TEXTURES.get();
  }

  public static boolean isNewTextures () {
    return NEW_TEXTURES.get();
  }

  public static boolean shouldPerformPiecewiseCheck () {
    return PERFORM_PIECEWISE_CHECK.get();
  }
}
