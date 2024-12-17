package noobanidus.mods.lootr.fabric.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.config.ConfigManagerBase;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Config(name = LootrAPI.MODID)
public class ConfigManager extends ConfigManagerBase implements ConfigData {
  @ConfigEntry.Gui.Excluded
  private static final List<ResourceLocation> PROBLEMATIC_CHESTS = Arrays.asList(ResourceLocation.fromNamespaceAndPath("atum", "chests/pharaoh"), ResourceLocation.fromNamespaceAndPath("twilightforest", "structures/stronghold_boss"));

  @ConfigEntry.Gui.Excluded
  private static Set<String> DECAY_MODS = null;
  @ConfigEntry.Gui.Excluded
  private static Set<ResourceKey<LootTable>> DECAY_TABLES = null;
  @ConfigEntry.Gui.Excluded
  private static Set<String> REFRESH_MODS = null;
  @ConfigEntry.Gui.Excluded
  private static Set<ResourceKey<LootTable>> REFRESH_TABLES = null;

  @ConfigEntry.Gui.Excluded
  private static Set<ResourceKey<Level>> DIM_WHITELIST = null;
  @ConfigEntry.Gui.Excluded
  private static Set<String> MODID_DIM_WHITELIST = null;
  @ConfigEntry.Gui.Excluded
  private static Set<ResourceKey<Level>> DIM_BLACKLIST = null;
  @ConfigEntry.Gui.Excluded
  private static Set<String> MODID_DIM_BLACKLIST = null;
  @ConfigEntry.Gui.Excluded
  private static Set<ResourceKey<Level>> DECAY_DIMS = null;
  @ConfigEntry.Gui.Excluded
  private static Set<ResourceKey<Level>> REFRESH_DIMS = null;
  @ConfigEntry.Gui.Excluded
  private static Set<ResourceKey<LootTable>> LOOT_BLACKLIST = null;
  @ConfigEntry.Gui.Excluded
  private static Map<Block, Block> replacements = null;
  @ConfigEntry.Gui.Excluded
  private static Set<String> LOOT_MODIDS = null;


  @ConfigEntry.Gui.CollapsibleObject
  public Debug debug = new Debug();
  @ConfigEntry.Gui.CollapsibleObject
  public Seed seed = new Seed();
  @ConfigEntry.Gui.CollapsibleObject
  public Conversion conversion = new Conversion();
  @ConfigEntry.Gui.CollapsibleObject
  public Breaking breaking = new Breaking();
  @ConfigEntry.Gui.CollapsibleObject
  public Lists lists = new Lists();
  @ConfigEntry.Gui.CollapsibleObject
  public Decay decay = new Decay();
  @ConfigEntry.Gui.CollapsibleObject
  public Refresh refresh = new Refresh();
  @ConfigEntry.Gui.CollapsibleObject
  public Notifications notifications = new Notifications();
  @ConfigEntry.Gui.CollapsibleObject
  public Client client = new Client();

  public static void reset() {
    replacements = null;
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
    LootrAPI.refreshSections();
  }

  public static ConfigManager get() {
    return AutoConfig.getConfigHolder(ConfigManager.class).getConfig();
  }

  public static Set<ResourceKey<Level>> getDimensionWhitelist() {
    if (DIM_WHITELIST == null) {
      DIM_WHITELIST = validateDimensions(get().lists.dimension_whitelist, "dimension_whitelist");
    }
    return DIM_WHITELIST;
  }

  public static Set<String> getDimensionModidWhitelist() {
    if (MODID_DIM_WHITELIST == null) {
      MODID_DIM_WHITELIST = validateStringList(get().lists.modid_dimension_whitelist, "modid_dimension_whitelist");
    }
    return MODID_DIM_WHITELIST;
  }

  public static Set<ResourceKey<Level>> getDimensionBlacklist() {
    if (DIM_BLACKLIST == null) {
      DIM_BLACKLIST = validateDimensions(get().lists.dimension_blacklist, "dimension_blacklist");
    }
    return DIM_BLACKLIST;
  }

  public static Set<String> getDimensionModidBlacklist() {
    if (MODID_DIM_BLACKLIST == null) {
      MODID_DIM_BLACKLIST = validateStringList(get().lists.modid_dimension_blacklist, "modid_dimension_blacklist");
    }
    return MODID_DIM_BLACKLIST;
  }

  public static Set<ResourceKey<Level>> getDecayDimensions() {
    if (DECAY_DIMS == null) {
      DECAY_DIMS = validateDimensions(get().decay.decay_dimensions, "decay_dimensions");
    }
    return DECAY_DIMS;
  }

  public static Set<ResourceKey<Level>> getRefreshDimensions() {
    if (REFRESH_DIMS == null) {
      REFRESH_DIMS = validateDimensions(get().refresh.refresh_dimensions, "refresh_dimensions");
    }
    return REFRESH_DIMS;
  }

  public static Set<ResourceKey<LootTable>> getLootBlacklist() {
    if (LOOT_BLACKLIST == null) {
      LOOT_BLACKLIST = validateResourceKeyList(get().lists.loot_table_blacklist, "loot_blacklist", o -> ResourceKey.create(Registries.LOOT_TABLE, o));
      // Fixes for #79 and #74
      PROBLEMATIC_CHESTS.forEach(o -> LOOT_BLACKLIST.add(ResourceKey.create(Registries.LOOT_TABLE, o)));
    }
    return LOOT_BLACKLIST;
  }

  public static Set<String> getLootModidsBlacklist() {
    if (LOOT_MODIDS == null) {
      LOOT_MODIDS = validateStringList(get().lists.loot_modid_blacklist, "loot_modid_blacklist");
    }
    return LOOT_MODIDS;
  }

  public static boolean isBlacklisted(ResourceKey<LootTable> table) {
    if (getLootBlacklist().contains(table)) {
      return true;
    }

    return getLootModidsBlacklist().contains(table.location().getNamespace());
  }

  public static Set<ResourceKey<LootTable>> getDecayingTables() {
    if (DECAY_TABLES == null) {
      DECAY_TABLES = validateResourceKeyList(get().decay.decay_loot_tables, "decay_loot_tables", o -> ResourceKey.create(Registries.LOOT_TABLE, o));
    }
    return DECAY_TABLES;
  }

  public static Set<String> getDecayMods() {
    if (DECAY_MODS == null) {
      DECAY_MODS = validateStringList(get().decay.decay_modids, "decay_mods");
    }
    return DECAY_MODS;
  }

  public static Set<ResourceKey<LootTable>> getRefreshingTables() {
    if (REFRESH_TABLES == null) {
      REFRESH_TABLES = validateResourceKeyList(get().refresh.refresh_loot_tables, "refresh_tables", o -> ResourceKey.create(Registries.LOOT_TABLE, o));
    }
    return REFRESH_TABLES;
  }

  public static Set<String> getRefreshMods() {
    if (REFRESH_MODS == null) {
      REFRESH_MODS = validateStringList(get().refresh.refresh_modids, "refresh_modids");
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
    return getDecayDimensions().contains(key);
  }

  public static boolean isDimensionRefreshing(ResourceKey<Level> key) {
    return getRefreshDimensions().contains(key);
  }

  public static boolean isDecaying(ILootrInfoProvider tile) {
    if (get().decay.decay_all) {
      return true;
    }
    if (tile.getInfoLootTable() != null) {
      if (getDecayingTables().contains(tile.getInfoLootTable())) {
        return true;
      }
      if (getDecayMods().contains(tile.getInfoLootTable().location().getNamespace())) {
        return true;
      }
    }
    if (LootrAPI.isTaggedStructurePresent((ServerLevel)tile.getInfoLevel(), new ChunkPos(tile.getInfoPos()), LootrTags.Structure.DECAY_STRUCTURES, tile.getInfoPos())) {
      return true;
    }
    return isDimensionDecaying(tile.getInfoDimension());
  }

  public static boolean isRefreshing(ILootrInfoProvider tile) {
    if (get().refresh.refresh_all) {
      return true;
    }
    if (tile.getInfoLootTable() != null) {
      if (getRefreshingTables().contains(tile.getInfoLootTable())) {
        return true;
      }
      if (getRefreshMods().contains(tile.getInfoLootTable().location().getNamespace())) {
        return true;
      }
    }
    if (LootrAPI.isTaggedStructurePresent((ServerLevel)tile.getInfoLevel(), new ChunkPos(tile.getInfoPos()), LootrTags.Structure.REFRESH_STRUCTURES, tile.getInfoPos())) {
      return true;
    }
    return isDimensionRefreshing(tile.getInfoDimension());
  }


  public static boolean shouldNotify(int remaining) {
    int delay = get().notifications.notification_delay;
    return !get().notifications.disable_notifications && (delay == -1 || remaining <= delay);
  }

  // TODO: Why isn't this being used?
  public static boolean shouldPerformPiecewiseCheck () {
    return get().conversion.perform_piecewise_check;
  }

  public static boolean isVanillaTextures() {
    return get().client.vanilla_textures;
  }

  public static boolean isNewTextures () {
    return get().client.new_textures;
  }

  // TODO: Generify this
  public static BlockState replacement(BlockState original) {
    if (replacements == null) {
      replacements = new HashMap<>();
    }

    Block replacement = replacements.get(original.getBlock());
    if (replacement == null && original.is(LootrTags.Blocks.CONVERT_BLOCK)) {
      if (original.getBlock() instanceof EntityBlock entityBlock) {
        BlockEntity be = entityBlock.newBlockEntity(BlockPos.ZERO, original);
        if (be instanceof RandomizableContainerBlockEntity) {
          if (original.is(LootrTags.Blocks.CONVERT_TRAPPED_CHESTS)) {
            replacements.put(original.getBlock(), LootrRegistry.getTrappedChestBlock());
          } else if (original.is(LootrTags.Blocks.CONVERT_BARRELS)) {
            replacements.put(original.getBlock(), LootrRegistry.getBarrelBlock());
          } else if (original.is(LootrTags.Blocks.CONVERT_CHESTS)) {
            replacements.put(original.getBlock(), LootrRegistry.getChestBlock());
          } else if (original.is(LootrTags.Blocks.CONVERT_SHULKERS)) {
            replacements.put(original.getBlock(), LootrRegistry.getShulkerBlock());
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
    if (property == ChestBlock.TYPE && state.hasProperty(property)) {
      return state.setValue(ChestBlock.TYPE, ChestType.SINGLE);
    }
    if (original.hasProperty(property) && state.hasProperty(property)) {
      return state.setValue(property, original.getValue(property));
    }
    return state;
  }

  public static class Debug {
    public boolean report_unresolved_tables = true;
  }

  public static class Seed {
    public boolean randomize_seed = true;
  }

  public static class Conversion {
    public int max_entry_age = 60 * 20 * 15;
    public boolean disable = false;
    public boolean convert_elytras = true;
    public boolean convert_mineshafts = true;
    public boolean world_border = false;
    public boolean perform_piecewise_check = true;
  }

  public static class Breaking {
    public boolean disable_break = false;
    public boolean enable_break = false;
    public boolean enable_fake_player_break = false;
    public boolean power_comparators = true;
    public boolean blast_resistant = false;
    public boolean blast_immune = false;
    public boolean trapped_custom = false;
    public boolean should_drop_player_loot = false;
  }

  public static class Lists {
    public List<String> dimension_whitelist = List.of();
    public List<String> dimension_blacklist = List.of();
    public List<String> loot_table_blacklist = List.of();
    public List<String> loot_modid_blacklist = List.of();
    public List<String> modid_dimension_whitelist = List.of();
    public List<String> modid_dimension_blacklist = List.of();
  }

  public static class Decay {
    public int decay_value = 6000;
    public boolean decay_all = false;
    @ConfigEntry.Gui.RequiresRestart
    public boolean perform_tick_decay = true;
    @ConfigEntry.Gui.RequiresRestart
    public boolean start_tick_decay = false;
    public List<String> decay_modids = List.of();
    public List<String> decay_loot_tables = List.of();
    public List<String> decay_dimensions = List.of();
  }

  public static class Refresh {
    public int refresh_value = 24000;
    public boolean refresh_all = false;
    @ConfigEntry.Gui.RequiresRestart
    public boolean perform_tick_refresh = true;
    @ConfigEntry.Gui.RequiresRestart
    public boolean start_tick_refresh = true;
    public List<String> refresh_modids = List.of();
    public List<String> refresh_loot_tables = List.of();
    public List<String> refresh_dimensions = List.of();
  }

  public static class Notifications {
    public int notification_delay = 30 * 20;
    public boolean disable_notifications = false;
    public boolean disable_message_styles = false;
  }

  public static class Client {
    public boolean vanilla_textures = false;
    public boolean new_textures = true;
  }
}
