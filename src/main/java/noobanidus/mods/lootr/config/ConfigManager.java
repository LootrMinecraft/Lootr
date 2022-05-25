package noobanidus.mods.lootr.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.state.Property;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.tile.ILootTile;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.init.ModBlocks;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Lootr.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigManager {
  private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
  private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

  private static final List<ResourceLocation> QUARK_CHESTS = Arrays.asList(new ResourceLocation("quark", "oak_chest"), new ResourceLocation("quark", "spruce_chest"), new ResourceLocation("quark", "birch_chest"), new ResourceLocation("quark", "jungle_chest"), new ResourceLocation("quark", "acacia_chest"), new ResourceLocation("quark", "dark_oak_chest"), new ResourceLocation("quark", "warped_chest"), new ResourceLocation("quark", "crimson_chest"), new ResourceLocation("quark", "nether_brick_chest"), new ResourceLocation("quark", "purpur_chest")); // Quark normal chests
  private static final List<ResourceLocation> QUARK_TRAPPED_CHESTS = Arrays.asList(new ResourceLocation("quark", "oak_trapped_chest"), new ResourceLocation("quark", "spruce_trapped_chest"), new ResourceLocation("quark", "birch_trapped_chest"), new ResourceLocation("quark", "jungle_trapped_chest"), new ResourceLocation("quark", "acacia_trapped_chest"), new ResourceLocation("quark", "dark_oak_trapped_chest"), new ResourceLocation("quark", "warped_trapped_chest"), new ResourceLocation("quark", "crimson_trapped_chest"));
  private static final List<ResourceLocation> PROBLEMATIC_CHESTS = Arrays.asList(new ResourceLocation("atum", "chests/pharaoh"), new ResourceLocation("twilightforest", "structures/stronghold_boss"));

  public static ForgeConfigSpec COMMON_CONFIG;
  public static ForgeConfigSpec CLIENT_CONFIG;
  public static final ForgeConfigSpec.BooleanValue REPORT_UNRESOLVED_TABLES;
  public static final ForgeConfigSpec.BooleanValue RANDOMISE_SEED;
  public static final ForgeConfigSpec.BooleanValue CONVERT_MINESHAFTS;
  public static final ForgeConfigSpec.BooleanValue CONVERT_QUARK;
  public static final ForgeConfigSpec.BooleanValue CONVERT_WOODEN_CHESTS;
  public static final ForgeConfigSpec.BooleanValue CONVERT_TRAPPED_CHESTS;
  public static final ForgeConfigSpec.BooleanValue DISABLE_BREAK;
  public static final ForgeConfigSpec.IntValue DECAY_VALUE;
  public static final ForgeConfigSpec.IntValue REFRESH_VALUE;
  public static final ForgeConfigSpec.BooleanValue DECAY_ALL;
  public static final ForgeConfigSpec.BooleanValue REFRESH_ALL;
  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_CHESTS;
  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_TRAPPED_CHESTS;
  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DIMENSION_WHITELIST;
  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DIMENSION_BLACKLIST;
  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> LOOT_TABLE_BLACKLIST;
  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> LOOT_MODID_BLACKLIST;
  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DECAY_MODIDS;
  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DECAY_LOOT_TABLES;
  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DECAY_DIMENSIONS;
  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> REFRESH_MODIDS;
  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> REFRESH_LOOT_TABLES;
  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> REFRESH_DIMENSIONS;
  public static final ForgeConfigSpec.BooleanValue VANILLA_TEXTURES;

  private static Set<String> DECAY_MODS = null;
  private static Set<ResourceLocation> DECAY_TABLES = null;
  private static Set<String> REFRESH_MODS = null;
  private static Set<ResourceLocation> REFRESH_TABLES = null;

  private static Set<RegistryKey<World>> DIM_WHITELIST = null;
  private static Set<RegistryKey<World>> DIM_BLACKLIST = null;
  private static Set<RegistryKey<World>> DECAY_DIMS = null;
  private static Set<RegistryKey<World>> REFRESH_DIMS = null;
  private static Set<ResourceLocation> LOOT_BLACKLIST = null;
  private static Set<ResourceLocation> ADD_CHESTS = null;
  private static Set<ResourceLocation> ADD_TRAPPED_CHESTS = null;
  private static Map<Block, Block> replacements = null;
  private static Set<String> LOOT_MOD_BLACKLIST = null;

  static {
    RANDOMISE_SEED = COMMON_BUILDER.comment("determine whether or not loot generated is the same for all players using the provided seed, or randomised per player").define("randomise_seed", true);
    CONVERT_MINESHAFTS = COMMON_BUILDER.comment("whether or not mineshaft chest minecarts should be converted to standard loot chests").define("convert_mineshafts", true);
    CONVERT_QUARK = COMMON_BUILDER.comment("whether or not quark chests used in world generation for loot purposes should be replaced with Lootr chests").define("convert_quark", true);
    CONVERT_WOODEN_CHESTS = COMMON_BUILDER.comment("whether or not the entire forge:chests/wooden tag should be added to the conversion list for structures (if they are backed by LockableLootTileEntity)").define("convert_wooden_chests", true);
    CONVERT_TRAPPED_CHESTS = COMMON_BUILDER.comment("whether or not the entire forge:chests/trapped tag should be added to the conversion list for structures (if they are backed by LockableLootTileEntity").define("convert_trapped_chests", true);
    List<? extends String> empty = Collections.emptyList();
    Predicate<Object> validator = o -> o instanceof String && ((String) o).contains(":");
    REPORT_UNRESOLVED_TABLES = COMMON_BUILDER.comment("lootr will automatically log all unresolved tables (i.e., for containers that have a loot table associated with them but, for whatever reason, the lookup for this table returns empty). setting this option to true additionally informs players when they open containers.").define("report_unresolved_tables", false);
    ADDITIONAL_CHESTS = COMMON_BUILDER.comment("a list of additional chests that should be converted [in the format of modid:name, must be a tile entity instance of LockableLootTileEntity]").defineList("additional_chests", empty, validator);
    ADDITIONAL_TRAPPED_CHESTS = COMMON_BUILDER.comment("a list of additional trapped chests that should be converted [in the format of modid:name, must be a tile entity instanceof LockableLootTileEntity]").defineList("additional_trapped_chests", empty, validator);
    DIMENSION_WHITELIST = COMMON_BUILDER.comment("list of dimensions (to the exclusion of all others) that loot chest should be replaced in [default: blank, allowing all dimensions, e.g., minecraft:overworld]").defineList("dimension_whitelist", empty, validator);
    DIMENSION_BLACKLIST = COMMON_BUILDER.comment("list of dimensions that loot chests should not be replaced in [default: blank, allowing all dimensions, format e.g., minecraft:overworld]").defineList("dimension_blacklist", empty, validator);
    LOOT_TABLE_BLACKLIST = COMMON_BUILDER.comment("list of loot tables which shouldn't be converted [in the format of modid:loot_table]").defineList("loot_table_blacklist", empty, validator);
    LOOT_MODID_BLACKLIST = COMMON_BUILDER.comment("list of modids which shouldn't be converted [in the format of modid, modid]").defineList("loot_mod_blacklist", empty, o -> o instanceof String);
    DISABLE_BREAK = COMMON_BUILDER.comment("prevent the destruction of Lootr chests except while sneaking in creative mode").define("disable_break", false);
    DECAY_VALUE = COMMON_BUILDER.comment("how long (in ticks) a decaying loot containers should take to decay [default 5 minutes = 5 * 60 * 20]").defineInRange("decay_value", 5 * 60 * 20, 0, Integer.MAX_VALUE);
    DECAY_LOOT_TABLES = COMMON_BUILDER.comment("list of loot tables which will decay [default blank, meaning no chests decay, in the format of 'modid:loot_table']").defineList("decay_loot_tables", empty, validator);
    DECAY_MODIDS = COMMON_BUILDER.comment("list of mod IDs whose loot tables will decay [default blank, meaning no chests decay, in the format of 'modid', 'modid']").defineList("decay_modids", empty, o -> o instanceof String);
    DECAY_DIMENSIONS = COMMON_BUILDER.comment("list of dimensions where loot chests should automatically decay [default: blank, e.g., minecraft:overworld]").defineList("decay_dimensions", empty, validator);
    DECAY_ALL = COMMON_BUILDER.comment("overriding decay_loot_tables, decay_modids and decay_dimensions: all chests will decay after being opened for the first time").define("decay_all", false);


    REFRESH_VALUE = COMMON_BUILDER.comment("how long (in ticks) before the contents of a loot container will be refresh [default 20 minutes = 20 * 60 * 20]").defineInRange("refresh_value", 20 * 60 * 20, 0, Integer.MAX_VALUE);
    REFRESH_LOOT_TABLES = COMMON_BUILDER.comment("list of loot tables which will refresh [default blank, meaning no chests refresh, in the format of 'modid:loot_table']").defineList("refresh_loot_tables", empty, validator);
    REFRESH_MODIDS = COMMON_BUILDER.comment("list of mod IDs whose loot tables will refresh [default blank, meaning no chests refresh, in the format of 'modid', 'modid']").defineList("refresh_modids", empty, o -> o instanceof String);
    REFRESH_DIMENSIONS = COMMON_BUILDER.comment("list of dimensions where loot chests should automatically refresh [default: blank, e.g., minecraft:overworld]").defineList("refresh_dimensions", empty, validator);
    REFRESH_ALL = COMMON_BUILDER.comment("overriding refresh_loot_tables, refresh_modids and refresh_dimensions: all chests will refresh after being opened for the first time").define("refresh_all", false);
    COMMON_CONFIG = COMMON_BUILDER.build();

    VANILLA_TEXTURES = CLIENT_BUILDER.comment("set to true to use vanilla textures instead of Lootr special textures. Note: this will prevent previously opened chests from rendering differently").define("vanilla_textures", false);
    CLIENT_CONFIG = CLIENT_BUILDER.build();
  }

  public static void loadConfig(ForgeConfigSpec spec, Path path) {
    CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
    configData.load();
    spec.setConfig(configData);
  }

  @SubscribeEvent
  public static void reloadConfig(ModConfig.ModConfigEvent event) {
    if (event.getConfig().getType() == ModConfig.Type.COMMON) {
      COMMON_CONFIG.setConfig(event.getConfig().getConfigData());
      replacements = null;
      DIM_WHITELIST = null;
      DIM_BLACKLIST = null;
      LOOT_BLACKLIST = null;
      ADD_CHESTS = null;
      ADD_TRAPPED_CHESTS = null;
      DECAY_MODS = null;
      DECAY_TABLES = null;
      DECAY_DIMS = null;
      LOOT_MOD_BLACKLIST = null;
      REFRESH_MODS = null;
      REFRESH_TABLES = null;
      REFRESH_DIMS = null;
    }
  }

  public static Set<RegistryKey<World>> getDimensionWhitelist() {
    if (DIM_WHITELIST == null) {
      DIM_WHITELIST = DIMENSION_WHITELIST.get().stream().map(o -> RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(o))).collect(Collectors.toSet());
    }
    return DIM_WHITELIST;
  }

  public static Set<RegistryKey<World>> getDimensionBlacklist() {
    if (DIM_BLACKLIST == null) {
      DIM_BLACKLIST = DIMENSION_BLACKLIST.get().stream().map(o -> RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(o))).collect(Collectors.toSet());
    }
    return DIM_BLACKLIST;
  }

  public static Set<RegistryKey<World>> getDecayDimensions() {
    if (DECAY_DIMS == null) {
      DECAY_DIMS = DECAY_DIMENSIONS.get().stream().map(o -> RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(o))).collect(Collectors.toSet());
    }
    return DECAY_DIMS;
  }

  public static Set<RegistryKey<World>> getRefreshDimensions() {
    if (REFRESH_DIMS == null) {
      REFRESH_DIMS = REFRESH_DIMENSIONS.get().stream().map(o -> RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(o))).collect(Collectors.toSet());
    }
    return REFRESH_DIMS;
  }

  public static boolean isBlacklisted(ResourceLocation table) {
    if (getLootBlacklist().contains(table)) {
      return true;
    }

    return getModBlacklist().contains(table.getNamespace());
  }

  public static Set<ResourceLocation> getLootBlacklist() {
    if (LOOT_BLACKLIST == null) {
      LOOT_BLACKLIST = LOOT_TABLE_BLACKLIST.get().stream().map(ResourceLocation::new).collect(Collectors.toSet());
      // FIX for https://github.com/noobanidus/Lootr/issues/74
      // Converting this atum chest results in completely breaking the
      // atum pyramid fight, etc.
      // Also fix for #79: Twilight Forest stronghold boss
      LOOT_BLACKLIST.addAll(PROBLEMATIC_CHESTS);
    }
    return LOOT_BLACKLIST;
  }

  public static Set<String> getModBlacklist() {
    if (LOOT_MOD_BLACKLIST == null) {
      LOOT_MOD_BLACKLIST = LOOT_MODID_BLACKLIST.get().stream().map(String::toLowerCase).collect(Collectors.toSet());
    }
    return LOOT_MOD_BLACKLIST;
  }

  public static Set<ResourceLocation> getDecayingTables() {
    if (DECAY_TABLES == null) {
      DECAY_TABLES = DECAY_LOOT_TABLES.get().stream().map(ResourceLocation::new).collect(Collectors.toSet());
    }
    return DECAY_TABLES;
  }

  public static Set<ResourceLocation> getRefreshingTables() {
    if (REFRESH_TABLES == null) {
      REFRESH_TABLES = REFRESH_LOOT_TABLES.get().stream().map(ResourceLocation::new).collect(Collectors.toSet());
    }
    return REFRESH_TABLES;
  }

  public static Set<String> getDecayMods() {
    if (DECAY_MODS == null) {
      DECAY_MODS = DECAY_MODIDS.get().stream().map(o -> o.toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
    }
    return DECAY_MODS;
  }

  public static Set<String> getRefreshMods() {
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

  public static boolean isDimensionBlocked(RegistryKey<World> key) {
    return (!getDimensionWhitelist().isEmpty() && !getDimensionWhitelist().contains(key)) || getDimensionBlacklist().contains(key);
  }

  public static boolean isDimensionDecaying(RegistryKey<World> key) {
    return (getDecayDimensions().contains(key));
  }

  public static boolean isDimensionRefreshing(RegistryKey<World> key) {
    return getRefreshDimensions().contains(key);
  }

  public static boolean isDecaying(World world, ILootTile tile) {
    if (DECAY_ALL.get()) {
      return true;
    }
    if (getDecayingTables().contains(tile.getTable())) {
      return true;
    }
    if (getDecayMods().contains(tile.getTable().getNamespace().toLowerCase(Locale.ROOT))) {
      return true;
    }
    return isDimensionDecaying(world.dimension());
  }

  public static boolean isRefreshing(World world, ILootTile tile) {
    if (REFRESH_ALL.get()) {
      return true;
    }
    if (tile.getTable() != null) {
      if (getRefreshingTables().contains(tile.getTable())) {
        return true;
      }
      if (getRefreshMods().contains(tile.getTable().getNamespace().toLowerCase(Locale.ROOT))) {
        return true;
      }
    }
    return isDimensionRefreshing(world.dimension());
  }

  public static boolean isDecaying(World world, LootrChestMinecartEntity entity) {
    if (DECAY_ALL.get()) {
      return true;
    }
    if (getDecayingTables().contains(entity.lootTable)) {
      return true;
    }
    if (getDecayMods().contains(entity.lootTable.getNamespace().toLowerCase(Locale.ROOT))) {
      return true;
    }
    return isDimensionDecaying(world.dimension());
  }

  public static boolean isRefreshing(World world, LootrChestMinecartEntity entity) {
    if (REFRESH_ALL.get()) {
      return true;
    }
    if (getRefreshingTables().contains(entity.lootTable)) {
      return true;
    }
    if (getRefreshMods().contains(entity.lootTable.getNamespace().toLowerCase(Locale.ROOT))) {
      return true;
    }
    return isDimensionRefreshing(world.dimension());
  }

  private static void addSafeReplacement(ResourceLocation location, Block replacement) {
    Block block = ForgeRegistries.BLOCKS.getValue(location);
    if (block != null) {
      replacements.put(block, replacement);
    }
  }

  private static void addUnsafeReplacement(ResourceLocation location, Block replacement, ServerWorld world) {
    Block block = ForgeRegistries.BLOCKS.getValue(location);
    if (block != null) {
      TileEntity tile = block.createTileEntity(block.defaultBlockState(), world);
      if (tile instanceof LockableLootTileEntity) {
        replacements.put(block, replacement);
      }
    }
  }

  // TODO: Move this to the config module?
  public static BlockState replacement(BlockState original) {
    if (replacements == null) {
      replacements = new HashMap<>();
      replacements.put(Blocks.CHEST, ModBlocks.CHEST);
      replacements.put(Blocks.BARREL, ModBlocks.BARREL);
      replacements.put(Blocks.TRAPPED_CHEST, ModBlocks.TRAPPED_CHEST);
      replacements.put(Blocks.SHULKER_BOX, ModBlocks.SHULKER);
      if (CONVERT_QUARK.get() && ModList.get().isLoaded("quark")) {
        QUARK_CHESTS.forEach(o -> addSafeReplacement(o, ModBlocks.CHEST));
        QUARK_TRAPPED_CHESTS.forEach(o -> addSafeReplacement(o, ModBlocks.TRAPPED_CHEST));
      }
      if (CONVERT_WOODEN_CHESTS.get() || CONVERT_TRAPPED_CHESTS.get()) {
        final ServerWorld world = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD);
        if (CONVERT_WOODEN_CHESTS.get()) {
          Tags.Blocks.CHESTS_WOODEN.getValues().forEach(o -> {
            if (replacements.containsKey(o)) {
              return;
            }
            TileEntity tile = o.createTileEntity(o.defaultBlockState(), world);
            if (tile instanceof LockableLootTileEntity) {
              replacements.put(o, ModBlocks.CHEST);
            }
          });
        }
        if (CONVERT_TRAPPED_CHESTS.get()) {
          Tags.Blocks.CHESTS_TRAPPED.getValues().forEach(o -> {
            if (replacements.containsKey(o)) {
              return;
            }
            TileEntity tile = o.createTileEntity(o.defaultBlockState(), world);
            if (tile instanceof LockableLootTileEntity) {
              replacements.put(o, ModBlocks.TRAPPED_CHEST);
            }
          });
        }
        if (CONVERT_WOODEN_CHESTS.get()) {
          Set<Block> enderChests = new HashSet<>(Tags.Blocks.CHESTS_ENDER.getValues());
          Tags.Blocks.CHESTS.getValues().forEach(o -> {
            if (replacements.containsKey(o) || enderChests.contains(o)) {
              return;
            }
            TileEntity tile = o.createTileEntity(o.defaultBlockState(), world);
            if (tile instanceof LockableLootTileEntity) {
              replacements.put(o, ModBlocks.CHEST);
            }
          });
        }
      }
      if (!getAdditionalChests().isEmpty() || !getAdditionalTrappedChests().isEmpty()) {
        final ServerWorld world = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD);
        getAdditionalChests().forEach(o -> addUnsafeReplacement(o, ModBlocks.CHEST, world));
        getAdditionalTrappedChests().forEach(o -> addUnsafeReplacement(o, ModBlocks.TRAPPED_CHEST, world));
      }
    }

    Block replacement = replacements.get(original.getBlock());
    if (replacement == null) {
      return null;
    }

    return copyProperties(replacement.defaultBlockState(), original);
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

  public static boolean isVanillaTextures () {
    return VANILLA_TEXTURES.get();
  }
}
