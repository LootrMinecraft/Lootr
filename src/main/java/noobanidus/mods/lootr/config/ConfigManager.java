package noobanidus.mods.lootr.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.LootrTags;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.init.ModBlocks;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = LootrAPI.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigManager {
  private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
  private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

  private static final List<ResourceLocation> PROBLEMATIC_CHESTS = Arrays.asList(new ResourceLocation("twilightforest", "structures/stronghold_boss"), new ResourceLocation("atum", "chests/pharaoh"));

  public static ModConfigSpec COMMON_CONFIG;
  public static ModConfigSpec CLIENT_CONFIG;

  // Debug
  public static final ModConfigSpec.BooleanValue REPORT_UNRESOLVED_TABLES;

  // Seed randomization
  public static final ModConfigSpec.BooleanValue RANDOMISE_SEED;

  // Conversion
  public static final ModConfigSpec.BooleanValue DISABLE;
  public static final ModConfigSpec.BooleanValue SKIP_UNLOADED;
  public static final ModConfigSpec.IntValue MAXIMUM_AGE;
  public static final ModConfigSpec.BooleanValue CONVERT_MINESHAFTS;
  public static final ModConfigSpec.BooleanValue CONVERT_ELYTRAS;
  public static final ModConfigSpec.BooleanValue CONVERT_WOODEN_CHESTS;
  public static final ModConfigSpec.BooleanValue CONVERT_TRAPPED_CHESTS;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_CHESTS;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_TRAPPED_CHESTS;

  // Breaking
  public static final ModConfigSpec.BooleanValue DISABLE_BREAK;
  public static final ModConfigSpec.BooleanValue ENABLE_BREAK;

  public static final ModConfigSpec.BooleanValue ENABLE_FAKE_PLAYER_BREAK;

  public static final ModConfigSpec.BooleanValue CHECK_WORLD_BORDER;

  // Whitelist/blacklist (loot table, modid, dimension)
  public static final ModConfigSpec.ConfigValue<List<? extends String>> DIMENSION_WHITELIST;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> DIMENSION_BLACKLIST;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> LOOT_TABLE_BLACKLIST;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> LOOT_MODID_BLACKLIST;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> MODID_DIMENSION_WHITELIST;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> MODID_DIMENSION_BLACKLIST;

  // Decay
  public static final ModConfigSpec.IntValue DECAY_VALUE;
  public static final ModConfigSpec.BooleanValue DECAY_ALL;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> DECAY_MODIDS;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> DECAY_LOOT_TABLES;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> DECAY_DIMENSIONS;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> DECAY_STRUCTURES;

  // Refresh
  public static final ModConfigSpec.IntValue REFRESH_VALUE;
  public static final ModConfigSpec.BooleanValue REFRESH_ALL;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> REFRESH_MODIDS;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> REFRESH_LOOT_TABLES;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> REFRESH_DIMENSIONS;
  public static final ModConfigSpec.ConfigValue<List<? extends String>> REFRESH_STRUCTURES;

  public static final ModConfigSpec.BooleanValue POWER_COMPARATORS;
  public static final ModConfigSpec.BooleanValue BLAST_RESISTANT;
  public static final ModConfigSpec.BooleanValue BLAST_IMMUNE;
  public static final ModConfigSpec.IntValue NOTIFICATION_DELAY;
  public static final ModConfigSpec.BooleanValue DISABLE_NOTIFICATIONS;
  public static final ModConfigSpec.BooleanValue DISABLE_MESSAGE_STYLES;
  public static final ModConfigSpec.BooleanValue TRAPPED_CUSTOM;

  // Client-only
  public static final ModConfigSpec.BooleanValue VANILLA_TEXTURES;

  private static Set<String> DECAY_MODS = null;
  private static Set<ResourceLocation> DECAY_TABLES = null;
  private static Set<String> REFRESH_MODS = null;
  private static Set<ResourceLocation> REFRESH_TABLES = null;

  private static Set<ResourceKey<Level>> DIM_WHITELIST = null;
  private static Set<String> MODID_DIM_WHITELIST = null;
  private static Set<ResourceKey<Level>> DIM_BLACKLIST = null;
  private static Set<String> MODID_DIM_BLACKLIST = null;
  private static Set<ResourceKey<Level>> DECAY_DIMS = null;
  private static Set<ResourceKey<Level>> REFRESH_DIMS = null;
  private static Set<ResourceLocation> LOOT_BLACKLIST = null;
  private static Set<ResourceLocation> STRUCTURE_BLACKLIST = null;
  private static Set<ResourceLocation> REFRESH_STRUCTS = null;
  private static Set<ResourceLocation> DECAY_STRUCTS = null;
  private static Set<ResourceLocation> ADD_CHESTS = null;
  private static Set<ResourceLocation> ADD_TRAPPED_CHESTS = null;
  private static Map<Block, Block> replacements = null;
  private static Set<String> LOOT_MODIDS = null;

  static {
    RANDOMISE_SEED = COMMON_BUILDER.comment("determine whether or not loot generated is the same for all players using the provided seed, or randomised per player").define("randomise_seed", true);
    MAXIMUM_AGE = COMMON_BUILDER.comment("the maximum age for containers; entries above this age will be discarded [default: 180 * 20, 3 minutes]").defineInRange("max_age", 60 * 10 * 10, 0, Integer.MAX_VALUE);
    DISABLE = COMMON_BUILDER.comment("if true, no chests will be converted").define("disable", false);
    SKIP_UNLOADED = COMMON_BUILDER.comment("skip unloaded block entities that are eligible for conversion, set to false to potentially resolve issues with containers that aren't being converted [default: true]").define("skip_unloaded", true);
    CONVERT_MINESHAFTS = COMMON_BUILDER.comment("whether or not mineshaft chest minecarts should be converted to standard loot chests").define("convert_mineshafts", true);
    CONVERT_ELYTRAS = COMMON_BUILDER.comment("whether or not the Elytra item frame should be converted into a standard loot chest with a guaranteed elytra").define("convert_elytras", true);
    CONVERT_WOODEN_CHESTS = COMMON_BUILDER.comment("whether or not the entire forge:chests/wooden tag should be added to the conversion list for structures (if they are backed by RandomizableContainerBlockEntity)").define("convert_wooden_chests", true);
    CONVERT_TRAPPED_CHESTS = COMMON_BUILDER.comment("whether or not the entire forge:chests/trapped tag should be added to the conversion list for structures (if they are backed by RandomizableContainerBlockEntity").define("convert_trapped_chests", true);
    List<? extends String> empty = Collections.emptyList();
    Predicate<Object> validator = o -> o instanceof String && ((String) o).contains(":");
    Predicate<Object> modidValidator = o -> o instanceof String && !((String) o).contains(":");
    REPORT_UNRESOLVED_TABLES = COMMON_BUILDER.comment("lootr will automatically log all unresolved tables (i.e., for containers that have a loot table associated with them but, for whatever reason, the lookup for this table returns empty). setting this option to true additionally informs players when they open containers.").define("report_unresolved_tables", false);
    ADDITIONAL_CHESTS = COMMON_BUILDER.comment("a list of additional chests that should be converted (in the format of [\"modid:name\", \"modid:other_name\"], must be a tile entity instance of RandomizableContainerBlockEntity)").defineList("additional_chests", empty, validator);
    ADDITIONAL_TRAPPED_CHESTS = COMMON_BUILDER.comment("a list of additional trapped chests that should be converted (in the format of [\"modid:name\", \"modid:other_name\"], must be a tile entity instance of RandomizableContainerBlockEntity)").defineList("additional_trapped_chests", empty, validator);
    DIMENSION_WHITELIST = COMMON_BUILDER.comment("list of dimensions (to the exclusion of all others) that loot chest should be replaced in (default: blank, allowing all dimensions, e.g., [\"minecraft:overworld\", \"minecraft:the_end\"])").defineList("dimension_whitelist", empty, validator);
    DIMENSION_BLACKLIST = COMMON_BUILDER.comment("list of dimensions that loot chests should not be replaced in (default: blank, allowing all dimensions, format e.g., [\"minecraft:overworld\", \"minecraft:the_end\"])").defineList("dimension_blacklist", empty, validator);
    MODID_DIMENSION_BLACKLIST = COMMON_BUILDER.comment("list of dimensions by modid that loot chests should not be replaced in (default: blank, allowing all modids, format e.g., [\"minecraft", "othermod\"])").defineList("modid_dimension_blacklist", empty, modidValidator);
    MODID_DIMENSION_WHITELIST = COMMON_BUILDER.comment("list of dimensions by modid that loot chest should be replaced in (default: blank, allowing all modids, format e.g., [\"minecraft", "othermod\"])").defineList("modid_dimension_whitelist", empty, modidValidator);
    LOOT_TABLE_BLACKLIST = COMMON_BUILDER.comment("list of loot tables which shouldn't be converted (in the format of [\"modid:loot_table\", \"othermodid:other_loot_table\"])").defineList("loot_table_blacklist", empty, validator);
    LOOT_MODID_BLACKLIST = COMMON_BUILDER.comment("list of modids whose loot tables shouldn't be converted (in the format of [\"modid\", \"other_modid\"])").defineList("loot_modid_blacklist", empty, modidValidator);
    DISABLE_BREAK = COMMON_BUILDER.comment("prevent the destruction of Lootr chests except while sneaking in creative mode").define("disable_break", false);
    ENABLE_BREAK = COMMON_BUILDER.comment("allow the destruction of Lootr chests regardless. overrides `disable_break`").define("enable_break", false);
    CHECK_WORLD_BORDER = COMMON_BUILDER.comment("disregard chests and chunks that are outside of the world border; enable this option if you are using a world border and are suffering consistent TPS issues; if you change the world border, you will need to restart your client").define("check_world_border", false);
    ENABLE_FAKE_PLAYER_BREAK = COMMON_BUILDER.comment("allows fake players to destroy Lootr chests without having to sneak, overrides the `disable_break` option for fake players").define("enable_fake_player_break", false);
    POWER_COMPARATORS = COMMON_BUILDER.comment("when true, comparators on Lootr containers will give an output of 1; when false, they will give an output of 0").define("power_comparators", true);
    TRAPPED_CUSTOM = COMMON_BUILDER.comment("when true, custom inventories will act like trapped chests when opened").define("trapped_custom", false);
    BLAST_RESISTANT = COMMON_BUILDER.comment("lootr chests cannot be destroyed by creeper or TNT explosions").define("blast_resistant", false);
    BLAST_IMMUNE = COMMON_BUILDER.comment("lootr chests cannot be destroyed by any explosion").define("blast_immune", false);
    DISABLE_NOTIFICATIONS = COMMON_BUILDER.comment("prevent notifications of decaying or refreshed chests").define("disable_notifications", false);
    NOTIFICATION_DELAY = COMMON_BUILDER.comment("maximum time (in ticks) remaining on a chest before a notification for refreshing or decaying is sent to a player (default 30 seconds, -1 for no delay)").defineInRange("notification_delay", 30 * 20, -1, Integer.MAX_VALUE);
    DISABLE_MESSAGE_STYLES = COMMON_BUILDER.comment("disables styling of breaking, decaying and refreshing messages sent to players").define("disable_message_styles", false);

    DECAY_VALUE = COMMON_BUILDER.comment("how long (in ticks) a decaying loot containers should take to decay (default 5 minutes = 5 * 60 * 20)").defineInRange("decay_value", 5 * 60 * 20, 0, Integer.MAX_VALUE);
    DECAY_LOOT_TABLES = COMMON_BUILDER.comment("list of loot tables which will decay (default blank, meaning no chests decay, in the format of (in the format of [\"modid:loot_table\", \"othermodid:other_loot_table\"])").defineList("decay_loot_tables", empty, validator);
    DECAY_MODIDS = COMMON_BUILDER.comment("list of mod IDs whose loot tables will decay (default blank, meaning no chests decay, in the format [\"modid\", \"othermodid\"])").defineList("decay_modids", empty, o -> o instanceof String);
    DECAY_DIMENSIONS = COMMON_BUILDER.comment("list of dimensions where loot chests should automatically decay (default: blank, e.g., [\"minecraft:the_nether\", \"minecraft:the_end\"])").defineList("decay_dimensions", empty, validator);
    DECAY_STRUCTURES = COMMON_BUILDER.comment("list of structures in which loot chests should automatically decay (in the format of [\"modid:structure_name\", \"modid:other_structure_name\"])").defineList("decay_structures", empty, validator);
    DECAY_ALL = COMMON_BUILDER.comment("overriding decay_loot_tables, decay_modids and decay_dimensions: all chests will decay after being opened for the first time").define("decay_all", false);

    REFRESH_VALUE = COMMON_BUILDER.comment("how long (in ticks) a refreshing loot containers should take to refresh their contents (default 20 minutes = 20 * 60 * 20)").defineInRange("refresh_value", 20 * 60 * 20, 0, Integer.MAX_VALUE);
    REFRESH_LOOT_TABLES = COMMON_BUILDER.comment("list of loot tables which will refresh (default blank, meaning no chests refresh, in the format of [\"modid:loot_table\", \"othermodid:loot_table\"])").defineList("refresh_loot_tables", empty, validator);
    REFRESH_MODIDS = COMMON_BUILDER.comment("list of mod IDs whose loot tables will refresh (default blank, meaning no chests refresh, in the format of [\"modid\", \"othermodid\"])").defineList("refresh_modids", empty, o -> o instanceof String);
    REFRESH_DIMENSIONS = COMMON_BUILDER.comment("list of dimensions where loot chests should automatically refresh (default: blank, e.g., [\"minecraft:overworld\", \"othermod:otherdimension\"])").defineList("refresh_dimensions", empty, validator);
    REFRESH_STRUCTURES = COMMON_BUILDER.comment("list of structures in which loot chests should automatically refresh (in the format of [\"modid:structure_name\", \"othermodid:other_structure_name\"])").defineList("refresh_structures", empty, validator);
    REFRESH_ALL = COMMON_BUILDER.comment("overriding refresh_loot_tables, refresh_modids and refresh_dimensions: all chests will refresh after being opened for the first time").define("refresh_all", false);

    COMMON_CONFIG = COMMON_BUILDER.build();
    VANILLA_TEXTURES = CLIENT_BUILDER.comment("set to true to use vanilla textures instead of Lootr special textures. Note: this will prevent previously opened chests from rendering differently").define("vanilla_textures", false);
    CLIENT_CONFIG = CLIENT_BUILDER.build();
  }

  public static void loadConfig(ModConfigSpec spec, Path path) {
    CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
    configData.load();
    spec.setConfig(configData);
  }

  @SubscribeEvent
  public static void reloadConfig(ModConfigEvent event) {
    if (event.getConfig().getType() == ModConfig.Type.COMMON) {
      COMMON_CONFIG.setConfig(event.getConfig().getConfigData());
      replacements = null;
      MODID_DIM_WHITELIST = null;
      MODID_DIM_BLACKLIST = null;
      DIM_WHITELIST = null;
      DIM_BLACKLIST = null;
      LOOT_BLACKLIST = null;
      ADD_CHESTS = null;
      ADD_TRAPPED_CHESTS = null;
      DECAY_MODS = null;
      DECAY_TABLES = null;
      DECAY_DIMS = null;
      LOOT_MODIDS = null;
      REFRESH_DIMS = null;
      REFRESH_MODS = null;
      REFRESH_TABLES = null;
      STRUCTURE_BLACKLIST = null;
      DECAY_STRUCTS = null;
      REFRESH_STRUCTS = null;
    }
  }

  public static Set<ResourceKey<Level>> getDimensionWhitelist() {
    if (DIM_WHITELIST == null) {
      DIM_WHITELIST = DIMENSION_WHITELIST.get().stream().map(o -> ResourceKey.create(Registries.DIMENSION, new ResourceLocation(o))).collect(Collectors.toSet());
    }
    return DIM_WHITELIST;
  }

  public static Set<String> getDimensionModidWhitelist() {
    if (MODID_DIM_WHITELIST == null) {
      MODID_DIM_WHITELIST = MODID_DIMENSION_WHITELIST.get().stream().map(o -> o.toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
    }
    return MODID_DIM_WHITELIST;
  }

  public static Set<ResourceKey<Level>> getDimensionBlacklist() {
    if (DIM_BLACKLIST == null) {
      DIM_BLACKLIST = DIMENSION_BLACKLIST.get().stream().map(o -> ResourceKey.create(Registries.DIMENSION, new ResourceLocation(o))).collect(Collectors.toSet());
    }
    return DIM_BLACKLIST;
  }

  public static Set<String> getDimensionModidBlacklist() {
    if (MODID_DIM_BLACKLIST == null) {
      MODID_DIM_BLACKLIST = MODID_DIMENSION_BLACKLIST.get().stream().map(o -> o.toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
    }
    return MODID_DIM_BLACKLIST;
  }

  public static Set<ResourceKey<Level>> getDecayDimensions() {
    if (DECAY_DIMS == null) {
      DECAY_DIMS = DECAY_DIMENSIONS.get().stream().map(o -> ResourceKey.create(Registries.DIMENSION, new ResourceLocation(o))).collect(Collectors.toSet());
    }
    return DECAY_DIMS;
  }

  public static Set<ResourceKey<Level>> getRefreshDimensions() {
    if (REFRESH_DIMS == null) {
      REFRESH_DIMS = REFRESH_DIMENSIONS.get().stream().map(o -> ResourceKey.create(Registries.DIMENSION, new ResourceLocation(o))).collect(Collectors.toSet());
    }
    return REFRESH_DIMS;
  }

  public static Set<ResourceLocation> getRefreshStructures () {
    if (REFRESH_STRUCTS == null) {
      REFRESH_STRUCTS = REFRESH_STRUCTURES.get().stream().map(ResourceLocation::new).collect(Collectors.toSet());
    }
    return REFRESH_STRUCTS;
  }

  public static Set<ResourceLocation> getDecayStructures () {
    if (DECAY_STRUCTS == null) {
      DECAY_STRUCTS = DECAY_STRUCTURES.get().stream().map(ResourceLocation::new).collect(Collectors.toSet());
    }
    return DECAY_STRUCTS;
  }

  public static Set<ResourceLocation> getLootBlacklist() {
    if (LOOT_BLACKLIST == null) {
      LOOT_BLACKLIST = LOOT_TABLE_BLACKLIST.get().stream().map(ResourceLocation::new).collect(Collectors.toSet());
      // Fixes for #79 and #74
      LOOT_BLACKLIST.addAll(PROBLEMATIC_CHESTS);
    }
    return LOOT_BLACKLIST;
  }

  public static Set<String> getLootModids() {
    if (LOOT_MODIDS == null) {
      LOOT_MODIDS = LOOT_MODID_BLACKLIST.get().stream().map(o -> o.toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
    }
    return LOOT_MODIDS;
  }

  public static boolean isBlacklisted(ResourceLocation table) {
    if (getLootBlacklist().contains(table)) {
      return true;
    }

    return getLootModids().contains(table.getNamespace());
  }

  public static Set<ResourceLocation> getDecayingTables() {
    if (DECAY_TABLES == null) {
      DECAY_TABLES = DECAY_LOOT_TABLES.get().stream().map(ResourceLocation::new).collect(Collectors.toSet());
    }
    return DECAY_TABLES;
  }

  public static Set<String> getDecayMods() {
    if (DECAY_MODS == null) {
      DECAY_MODS = DECAY_MODIDS.get().stream().map(o -> o.toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
    }
    return DECAY_MODS;
  }

  public static Set<ResourceLocation> getRefreshingTables () {
    if (REFRESH_TABLES == null) {
      REFRESH_TABLES = REFRESH_LOOT_TABLES.get().stream().map(ResourceLocation::new).collect(Collectors.toSet());
    }
    return REFRESH_TABLES;
  }

  public static Set<String> getRefreshMods () {
    if (REFRESH_MODS == null) {
      REFRESH_MODS = REFRESH_MODIDS.get().stream().map(o -> o.toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
    }
    return REFRESH_MODS;
  }

  public static Set<ResourceLocation> getAdditionalChests() {
    if (ADD_CHESTS == null) {
      ADD_CHESTS = ADDITIONAL_CHESTS.get().stream().map(ResourceLocation::new).collect(Collectors.toSet());
    }
    return ADD_CHESTS;
  }

  public static Set<ResourceLocation> getAdditionalTrappedChests() {
    if (ADD_TRAPPED_CHESTS == null) {
      ADD_TRAPPED_CHESTS = ADDITIONAL_TRAPPED_CHESTS.get().stream().map(ResourceLocation::new).collect(Collectors.toSet());
    }
    return ADD_TRAPPED_CHESTS;
  }

  public static boolean isDimensionBlocked(ResourceKey<Level> key) {
    if (!getDimensionModidWhitelist().isEmpty() && !getDimensionModidWhitelist().contains(key.location().getNamespace()) || getDimensionModidBlacklist().contains(key.location().getNamespace())) {
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

  public static boolean isDecaying(ServerLevel level, ILootBlockEntity tile) {
    if (DECAY_ALL.get()) {
      return true;
    }
    if (tile.getTable() != null) {
      if (getDecayingTables().contains(tile.getTable())) {
        return true;
      }
      if (getDecayMods().contains(tile.getTable().getNamespace())) {
        return true;
      }
    }
/*    if (!ConfigManager.getDecayStructures().isEmpty()) {
      StructureFeature<?> startAt = StructureUtil.featureFor(level, tile.getPosition());
      if (startAt != null && ConfigManager.getDecayStructures().contains(startAt.getRegistryName())) {
        return true;
      }
    }*/
    return isDimensionDecaying(level.dimension());
  }

  public static boolean isRefreshing (ServerLevel level, ILootBlockEntity tile) {
    if (REFRESH_ALL.get()) {
      return true;
    }
    if (tile.getTable() != null) {
      if (getRefreshingTables().contains(tile.getTable())) {
        return true;
      }
      if (getRefreshMods().contains(tile.getTable().getNamespace())) {
        return true;
      }
    }
/*    if (!ConfigManager.getRefreshStructures().isEmpty()) {
      StructureFeature<?> startAt = StructureUtil.featureFor(level, tile.getPosition());
      if (startAt != null && ConfigManager.getRefreshStructures().contains(startAt.getRegistryName())) {
        return true;
      }
    }*/
    return isDimensionRefreshing(level.dimension());
  }

  public static boolean isDecaying(ServerLevel level, LootrChestMinecartEntity entity) {
    if (DECAY_ALL.get()) {
      return true;
    }
    if (entity.lootTable != null) {
      if (getDecayingTables().contains(entity.lootTable)) {
        return true;
      }
      if (getDecayMods().contains(entity.lootTable.getNamespace())) {
        return true;
      }
    }
/*    if (!ConfigManager.getDecayStructures().isEmpty()) {
      StructureFeature<?> startAt = StructureUtil.featureFor(level, new BlockPos(entity.position()));
      if (startAt != null && ConfigManager.getDecayStructures().contains(startAt.getRegistryName())) {
        return true;
      }
    }*/
    return isDimensionDecaying(level.dimension());
  }

  public static boolean isRefreshing (ServerLevel level, LootrChestMinecartEntity entity) {
    if (REFRESH_ALL.get()) {
      return true;
    }
    if (entity.lootTable != null) {
      if (getRefreshingTables().contains(entity.lootTable)) {
        return true;
      }

      if (getDecayMods().contains(entity.lootTable.getNamespace())) {
        return true;
      }
    }
/*    if (!ConfigManager.getRefreshStructures().isEmpty()) {
      StructureFeature<?> startAt = StructureUtil.featureFor(level, new BlockPos(entity.position()));
      if (startAt != null && ConfigManager.getRefreshStructures().contains(startAt.getRegistryName())) {
        return true;
      }
    }*/
    return isDimensionRefreshing(level.dimension());
  }

  public static boolean shouldNotify (int remaining) {
    int delay = NOTIFICATION_DELAY.get();
    return !DISABLE_NOTIFICATIONS.get() && (delay == -1 || remaining <= delay);
  }

  public static boolean isVanillaTextures () {
    return VANILLA_TEXTURES.get();
  }

  private static void addSafeReplacement(ResourceLocation location, Block replacement) {
    Block block = BuiltInRegistries.BLOCK.getOptional(location).orElse(null);
    if (block != null) {
      replacements.put(block, replacement);
    }
  }

  private static void addUnsafeReplacement(ResourceLocation location, Block replacement, ServerLevel world) {
    Block block = BuiltInRegistries.BLOCK.getOptional(location).orElse(null);
    if (block instanceof EntityBlock) {
      BlockEntity tile = ((EntityBlock) block).newBlockEntity(BlockPos.ZERO, block.defaultBlockState());
      if (tile instanceof RandomizableContainerBlockEntity) {
        replacements.put(block, replacement);
      }
    }
  }

  // TODO: Move this to the config module?
  public static BlockState replacement(BlockState original) {
    if (replacements == null) {
      replacements = new HashMap<>();
      if (CONVERT_WOODEN_CHESTS.get() || CONVERT_TRAPPED_CHESTS.get()) {
        if (CONVERT_TRAPPED_CHESTS.get()) {
          BuiltInRegistries.BLOCK.getTag(Tags.Blocks.CHESTS_TRAPPED).orElseThrow().stream().map(Holder::value).forEach(o -> {
            if (replacements.containsKey(o)) {
              return;
            }
            if (o instanceof EntityBlock eb) {
              BlockEntity tile = eb.newBlockEntity(BlockPos.ZERO, o.defaultBlockState());
              if (tile instanceof RandomizableContainerBlockEntity && !(tile instanceof ILootBlockEntity)) {
                replacements.put(o, ModBlocks.TRAPPED_CHEST.get());
              }
            }
          });
        }
        if (CONVERT_WOODEN_CHESTS.get()) {
          BuiltInRegistries.BLOCK.getTag(Tags.Blocks.CHESTS_WOODEN).orElseThrow().stream().map(Holder::value).forEach(o -> {
            if (replacements.containsKey(o)) {
              return;
            }
            if (o instanceof EntityBlock eb) {
              BlockEntity tile = eb.newBlockEntity(BlockPos.ZERO, o.defaultBlockState());
              if (tile instanceof RandomizableContainerBlockEntity && !(tile instanceof ILootBlockEntity)) {
                replacements.put(o, ModBlocks.CHEST.get());
              }
            }
          });
        }
      }
      if (!getAdditionalChests().isEmpty() || !getAdditionalTrappedChests().isEmpty()) {
        final ServerLevel world = ServerLifecycleHooks.getCurrentServer().overworld();
        getAdditionalChests().forEach(o -> addUnsafeReplacement(o, ModBlocks.CHEST.get(), world));
        getAdditionalTrappedChests().forEach(o -> addUnsafeReplacement(o, ModBlocks.TRAPPED_CHEST.get(), world));
      }
    }

    Block replacement = replacements.get(original.getBlock());
    if (replacement == null && original.is(LootrTags.Blocks.CONVERT_BLOCK)) {
      if (original.getBlock() instanceof EntityBlock entityBlock) {
        BlockEntity be = entityBlock.newBlockEntity(BlockPos.ZERO, original);
        if (be instanceof RandomizableContainerBlockEntity) {
          if (original.is(LootrTags.Blocks.CONVERT_TRAPPED_CHESTS)) {
            replacements.put(original.getBlock(), ModBlocks.TRAPPED_CHEST.get());
          } else if (original.is(LootrTags.Blocks.CONVERT_BARRELS)) {
            replacements.put(original.getBlock(), ModBlocks.BARREL.get());
          } else if (original.is(LootrTags.Blocks.CONVERT_CHESTS)) {
            replacements.put(original.getBlock(), ModBlocks.CHEST.get());
          } else if (original.is(LootrTags.Blocks.CONVERT_SHULKERS)) {
            replacements.put(original.getBlock(), ModBlocks.SHULKER.get());
          }
        }
      }
      replacement = replacements.get(original.getBlock());
    }

    if (replacement != null) {
      return copyProperties(replacement.defaultBlockState(), original);
    }

    return null;
  }

  private static BlockState copyProperties(BlockState state, BlockState original) {
    for (Property<?> prop : original.getProperties()) {
      if (state.hasProperty(prop)) {
        state = safeReplace(state, original, prop);
      }
    }
    return state;
  }

  private static <V extends Comparable<V>> BlockState safeReplace(BlockState state, BlockState original, Property<V> property) {
    // TODO: Bit of a dirty hack
    if (property == ChestBlock.TYPE && state.hasProperty(property)) {
      return state.setValue(ChestBlock.TYPE, ChestType.SINGLE);
    }
    if (original.hasProperty(property) && state.hasProperty(property)) {
      return state.setValue(property, original.getValue(property));
    }
    return state;
  }
}
