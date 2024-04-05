package noobanidus.mods.lootr.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.Property;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.LootrTags;

import java.util.*;

@Config(name = LootrAPI.MODID)
public class ConfigManager implements ConfigData {
  @ConfigEntry.Gui.Excluded
  private static Set<String> DECAY_MODS = null;
  @ConfigEntry.Gui.Excluded
  private static Set<ResourceLocation> DECAY_TABLES = null;
  @ConfigEntry.Gui.Excluded
  private static Set<String> REFRESH_MODS = null;
  @ConfigEntry.Gui.Excluded
  private static Set<ResourceLocation> REFRESH_TABLES = null;

  @ConfigEntry.Gui.Excluded
  private static Set<ResourceKey<Level>> DIM_WHITELIST = null;
  @ConfigEntry.Gui.Excluded
  private static Set<String> DIM_MODID_WHITELIST = null;
  @ConfigEntry.Gui.Excluded
  private static Set<ResourceKey<Level>> DIM_BLACKLIST = null;
  @ConfigEntry.Gui.Excluded
  private static Set<String> DIM_MODID_BLACKLIST = null;
  @ConfigEntry.Gui.Excluded
  private static Set<ResourceKey<Level>> DECAY_DIMS = null;
  @ConfigEntry.Gui.Excluded
  private static Set<ResourceKey<Level>> REFRESH_DIMS = null;
  @ConfigEntry.Gui.Excluded
  private static Set<ResourceLocation> LOOT_BLACKLIST = null;
  @ConfigEntry.Gui.Excluded
  private static Map<Block, Block> replacements = null;
  @ConfigEntry.Gui.Excluded
  private static Set<String> LOOT_MODIDS = null;

  public static void reset() {
    DECAY_MODS = null;
    DECAY_TABLES = null;
    REFRESH_MODS = null;
    DIM_WHITELIST = null;
    DIM_MODID_WHITELIST = null;
    DIM_BLACKLIST = null;
    DIM_MODID_BLACKLIST = null;
    DECAY_DIMS = null;
    REFRESH_DIMS = null;
    LOOT_BLACKLIST = null;
    LOOT_MODIDS = null;
  }

  @ConfigEntry.Gui.CollapsibleObject
  public Debug debug = new Debug();

  public static class Debug {
    public boolean report_invalid_tables = true;
  }

  @ConfigEntry.Gui.CollapsibleObject
  public Seed seed = new Seed();

  public static class Seed {
    public boolean randomize_seed = true;
  }

  @ConfigEntry.Gui.CollapsibleObject
  public Conversion conversion = new Conversion();

  public static class Conversion {
    public int max_entry_age = 60 * 10 * 20;
    public boolean disable = false;
  }

  @ConfigEntry.Gui.CollapsibleObject
  public Breaking breaking = new Breaking();

  public static class Breaking {
    public boolean enable_break = false;
    public boolean enable_fake_player_break = false;
    public boolean disable_break = false;
    public boolean power_comparators = true;
    public boolean blast_resistant = false;
    public boolean blast_immune = false;
    public boolean trapped_custom = false;
  }

  @ConfigEntry.Gui.CollapsibleObject
  public Lists lists = new Lists();

  public static class Lists {
    public List<String> dimension_whitelist = List.of();
    public List<String> dimension_blacklist = List.of();
    public List<String> dimension_modid_blacklist = List.of();
    public List<String> dimension_modid_whitelist = List.of();
    public List<String> loot_table_blacklist = List.of();
    public List<String> loot_modid_blacklist = List.of();
  }

  @ConfigEntry.Gui.CollapsibleObject
  public Decay decay = new Decay();

  public static class Decay {
    public int decay_value = 6000;
    public boolean decay_all = false;
    public List<String> decay_modids = List.of();
    public List<String> decay_loot_tables = List.of();
    public List<String> decay_dimensions = List.of();
  }

  @ConfigEntry.Gui.CollapsibleObject
  public Refresh refresh = new Refresh();

  public static class Refresh {
    public int refresh_value = 24000;
    public boolean refresh_all = false;
    public List<String> refresh_modids = List.of();
    public List<String> refresh_loot_tables = List.of();
    public List<String> refresh_dimensions = List.of();
  }

  @ConfigEntry.Gui.CollapsibleObject
  public Notifications notifications = new Notifications();

  public static class Notifications {
    public int notification_delay = 30 * 20;
    public boolean disable_notifications = false;
  }

  @ConfigEntry.Gui.CollapsibleObject
  public Vanilla vanilla = new Vanilla();

  public static class Vanilla {
    public boolean vanilla_textures = false;
  }

  private static Set<String> validateStringList(List<String> incomingList, String listKey) {
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

  private static Set<ResourceKey<Level>> validateDimensions(List<String> incomingList, String listKey) {
    Set<ResourceKey<Level>> validatedList = new HashSet<>();
    for (String entry : incomingList) {
      if (entry == null || entry.isEmpty()) {
        throw new RuntimeException("Error found when validating a configuration list for '" + listKey + "'. One of the entries is null or empty and cannot be converted to a dimension identifier.");
      }
      try {
        validatedList.add(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(entry)));
      } catch (Exception e) {
        throw new RuntimeException("Error found when validating a configuration list for '" + listKey + "'. The value found in the list, '" + entry + "', is not a valid dimension identifier.", e);
      }
    }
    return validatedList;
  }

  private static Set<ResourceLocation> validateResourceLocationList(List<String> incomingList, String listKey) {
    Set<ResourceLocation> validatedList = new HashSet<>();
    for (String entry : incomingList) {
      if (entry == null || entry.isEmpty()) {
        throw new RuntimeException("Error found when validating a configuration list for '" + listKey + "'. One of the entries is null or empty and cannot be converted to a ResourceLocation.");
      }
      try {
        validatedList.add(new ResourceLocation(entry));
      } catch (Exception e) {
        throw new RuntimeException("Error found when validating a configuration list for '" + listKey + "'. The value found in the list, '" + entry + "', is not a valid ResourceLocation.", e);
      }
    }
    return validatedList;
  }

  public static ConfigManager get() {
    return AutoConfig.getConfigHolder(ConfigManager.class).getConfig();
  }

  public static boolean isDecaying(ServerLevel level, ILootBlockEntity tile) {
    if (get().decay.decay_all) {
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
    return isDimensionDecaying(level.dimension());
  }

  public static boolean isDecaying(ServerLevel level, LootrChestMinecartEntity entity) {
    if (get().decay.decay_all) {
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
    return isDimensionDecaying(level.dimension());
  }

  public static boolean isRefreshing(ServerLevel level, LootrChestMinecartEntity entity) {
    if (get().refresh.refresh_all) {
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
    return isDimensionRefreshing(level.dimension());
  }

  public static Set<ResourceLocation> getDecayingTables() {
    if (DECAY_TABLES == null) {
      DECAY_TABLES = validateResourceLocationList(get().decay.decay_loot_tables, "decay_loot_tables");
    }
    return DECAY_TABLES;
  }

  public static Set<String> getDecayMods() {
    if (DECAY_MODS == null) {
      DECAY_MODS = validateStringList(get().decay.decay_modids, "decay_modids");
    }
    return DECAY_MODS;
  }

  public static boolean isDimensionDecaying(ResourceKey<Level> key) {
    return getDecayDimensions().contains(key);
  }

  public static Set<ResourceKey<Level>> getDecayDimensions() {
    if (DECAY_DIMS == null) {
      DECAY_DIMS = validateDimensions(get().decay.decay_dimensions, "decay_dimensions");
    }
    return DECAY_DIMS;
  }

  public static boolean isRefreshing(ServerLevel level, ILootBlockEntity tile) {
    if (get().refresh.refresh_all) {
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
    return isDimensionRefreshing(level.dimension());
  }

  public static Set<ResourceLocation> getRefreshingTables() {
    if (REFRESH_TABLES == null) {
      REFRESH_TABLES = validateResourceLocationList(get().refresh.refresh_loot_tables, "refresh_loot_tables");
    }
    return REFRESH_TABLES;
  }

  public static Set<String> getRefreshMods() {
    if (REFRESH_MODS == null) {
      REFRESH_MODS = validateStringList(get().refresh.refresh_modids, "refresh_modids");
    }
    return REFRESH_MODS;
  }

  public static boolean isDimensionRefreshing(ResourceKey<Level> key) {
    return getRefreshDimensions().contains(key);
  }

  public static Set<ResourceKey<Level>> getRefreshDimensions() {
    if (REFRESH_DIMS == null) {
      REFRESH_DIMS = validateDimensions(get().refresh.refresh_dimensions, "refresh_dimensions");
    }
    return REFRESH_DIMS;
  }

  // TODO: Move this to the config module?
  public static BlockState replacement(BlockState original) {
    if (replacements == null) {
      replacements = new HashMap<>();
    }

    // TODO: Do this for Forge too
    Block replacement = replacements.get(original.getBlock());
    if (replacement == null && (original.is(LootrTags.Blocks.CONVERT_BARRELS) || original.is(LootrTags.Blocks.CONVERT_CHESTS) || original.is(LootrTags.Blocks.CONVERT_CHESTS) || original.is(LootrTags.Blocks.CONVERT_SHULKERS) || original.is(LootrTags.Blocks.CONVERT_TRAPPED_CHESTS))) {
      if (original.getBlock() instanceof EntityBlock entityBlock) {
        BlockEntity be = entityBlock.newBlockEntity(BlockPos.ZERO, original);
        if (be instanceof RandomizableContainerBlockEntity) {
          if (original.is(LootrTags.Blocks.CONVERT_TRAPPED_CHESTS)) {
            replacements.put(original.getBlock(), ModBlocks.TRAPPED_CHEST);
          } else if (original.is(LootrTags.Blocks.CONVERT_BARRELS)) {
            replacements.put(original.getBlock(), ModBlocks.BARREL);
          } else if (original.is(LootrTags.Blocks.CONVERT_CHESTS)) {
            replacements.put(original.getBlock(), ModBlocks.CHEST);
          } else if (original.is(LootrTags.Blocks.CONVERT_SHULKERS)) {
            replacements.put(original.getBlock(), ModBlocks.SHULKER);
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

  public static boolean isVanillaTextures() {
    return get().vanilla.vanilla_textures;
  }

  public static Set<String> getDimensionModidWhitelist() {
    if (DIM_MODID_WHITELIST == null) {
      DIM_MODID_WHITELIST = validateStringList(get().lists.dimension_modid_whitelist, "dimension_modid_whitelist");
    }

    return DIM_MODID_WHITELIST;
  }

  public static Set<ResourceKey<Level>> getDimensionWhitelist() {
    if (DIM_WHITELIST == null) {
      DIM_WHITELIST = validateDimensions(get().lists.dimension_whitelist, "dimension_whitelist");
    }

    return DIM_WHITELIST;
  }

  public static Set<String> getDimensionModidBlacklist() {
    if (DIM_MODID_BLACKLIST == null) {
      DIM_MODID_BLACKLIST = validateStringList(get().lists.dimension_modid_blacklist, "dimension_modid_blacklist");
    }

    return DIM_MODID_BLACKLIST;
  }

  public static Set<ResourceKey<Level>> getDimensionBlacklist() {
    if (DIM_BLACKLIST == null) {
      DIM_BLACKLIST = validateDimensions(get().lists.dimension_blacklist, "dimension_blacklist");
    }

    return DIM_BLACKLIST;
  }

  public static Set<String> getLootModids() {
    if (LOOT_MODIDS == null) {
      LOOT_MODIDS = validateStringList(get().lists.loot_modid_blacklist, "loot_modid_blacklist");
    }

    return LOOT_MODIDS;
  }

  public static Set<ResourceLocation> getLootBlacklist() {
    if (LOOT_BLACKLIST == null) {
      LOOT_BLACKLIST = validateResourceLocationList(get().lists.loot_table_blacklist, "loot_table_blacklist");
    }

    return LOOT_BLACKLIST;
  }

  public static boolean isDimensionBlacklisted(ResourceKey<Level> key) {
    if (!getDimensionModidWhitelist().isEmpty() && !getDimensionModidWhitelist().contains(key.location().getNamespace()) || getDimensionModidBlacklist().contains(key.location().getNamespace())) {
      return true;
    }

    return (!getDimensionWhitelist().isEmpty() && !getDimensionWhitelist().contains(key)) || getDimensionBlacklist().contains(key);
  }

  public static boolean isBlacklisted(ResourceLocation table) {
    if (getLootBlacklist().contains(table)) {
      return true;
    }

    return getLootModids().contains(table.getNamespace());
  }

  public static boolean shouldNotify(int remaining) {
    int delay = get().notifications.notification_delay;
    return !get().notifications.disable_notifications && (delay == -1 || remaining <= delay);
  }
}
