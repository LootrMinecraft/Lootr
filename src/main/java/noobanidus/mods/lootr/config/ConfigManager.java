package noobanidus.mods.lootr.config;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.tile.ILootTile;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.init.ModBlocks;

import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
@Config(modid = Lootr.MODID)
public class ConfigManager {

  private static final List<ResourceLocation> QUARK_CHESTS = Arrays.asList(new ResourceLocation("quark", "oak_chest"), new ResourceLocation("quark", "spruce_chest"), new ResourceLocation("quark", "birch_chest"), new ResourceLocation("quark", "jungle_chest"), new ResourceLocation("quark", "acacia_chest"), new ResourceLocation("quark", "dark_oak_chest"), new ResourceLocation("quark", "warped_chest"), new ResourceLocation("quark", "crimson_chest"), new ResourceLocation("quark", "nether_brick_chest"), new ResourceLocation("quark", "purpur_chest")); // Quark normal chests
  private static final List<ResourceLocation> QUARK_TRAPPED_CHESTS = Arrays.asList(new ResourceLocation("quark", "oak_trapped_chest"), new ResourceLocation("quark", "spruce_trapped_chest"), new ResourceLocation("quark", "birch_trapped_chest"), new ResourceLocation("quark", "jungle_trapped_chest"), new ResourceLocation("quark", "acacia_trapped_chest"), new ResourceLocation("quark", "dark_oak_trapped_chest"), new ResourceLocation("quark", "warped_trapped_chest"), new ResourceLocation("quark", "crimson_trapped_chest"));
  private static final List<ResourceLocation> ATUM_BLACKLIST = Collections.singletonList(new ResourceLocation("atum", "chests/pharaoh"));

  @Config.Comment("determine whether or not loot generated is the same for all players using the provided seed, or randomised per player")
  public static boolean RANDOMISE_SEED = true;
  @Config.Comment("whether or not mineshaft chest minecarts should be converted to standard loot chests")
  public static boolean CONVERT_MINESHAFTS = true;
  @Config.Comment("whether or not quark chests used in world generation for loot purposes should be replaced with Lootr chests")
  public static boolean CONVERT_QUARK = true;
  @Config.Comment("prevent the destruction of Lootr chests except while sneaking in creative mode")
  public static boolean DISABLE_BREAK = false;
  @Config.Comment("how long (in ticks) a decaying loot containers should take to decay [default 5 minutes = 5 * 60 * 20]")
  @Config.RangeInt(min = 0)
  public static int DECAY_VALUE = 5 * 60 * 20;
  @Config.Comment("how long (in ticks) before the contents of a loot container will be refreshed [default 20 minutes = 20 * 60 * 20]")
  @Config.RangeInt(min = 0)
  public static int REFRESH_VALUE = 20 * 60 * 20;
  @Config.Comment("overriding decay_loot_tables, decay_modids and decay_dimensions: all chests will decay after being opened for the first time")
  public static boolean DECAY_ALL = false;
  @Config.Comment("overriding refresh_loot_tables, refresh_modids and refresh_dimensions: all chests will refresh after being opened for the first time")
  public static boolean REFRESH_ALL = false;
  @Config.Comment("a list of additional chests that should be converted [in the format of modid:name, must be a tile entity instance of TileEntityLockableLoot]")
  public static String[] ADDITIONAL_CHESTS = new String[0];
  @Config.Comment("a list of additional trapped chests that should be converted [in the format of modid:name, must be a tile entity instanceof TileEntityLockableLoot]")
  public static String[] ADDITIONAL_TRAPPED_CHESTS = new String[0];
  @Config.Comment("list of dimensions (to the exclusion of all others) that loot chest should be replaced in [default: blank, allowing all dimensions, format e.g., 0")
  public static int[] DIMENSION_WHITELIST = new int[0];
  @Config.Comment("list of dimensions that loot chests should not be replaced in [default: blank, allowing all dimensions, format e.g., 0]")
  public static int[] DIMENSION_BLACKLIST = new int[0];
  @Config.Comment("list of loot tables which shouldn't be converted [in the format of modid:loot_table]")
  public static String[] LOOT_TABLE_BLACKLIST = new String[0];
  @Config.Comment("list of modids which shouldn't be converted [in the format of modid]")
  public static String[] LOOT_MODID_BLACKLIST = new String[0];
  @Config.Comment("list of mod IDs whose loot tables will decay [default blank, meaning no chests decay, in the format of 'modid', 'modid']")
  public static String[] DECAY_MODIDS = new String[0];
  @Config.Comment("list of loot tables which will decay [default blank, meaning no chests decay, in the format of 'modid:loot_table']")
  public static String[] DECAY_LOOT_TABLES = new String[0];
  @Config.Comment("list of dimensions where loot chests should automatically decay [default: blank, e.g., minecraft:overworld]")
  public static int[] DECAY_DIMENSIONS = new int[0];
  @Config.Comment("list of mod IDs whose loot tables will refresh [default blank, meaning no chests refresh, in the format of 'modid', 'modid']")
  public static String[] REFRESH_MODIDS = new String[0];
  @Config.Comment("list of loot tables which will refresh [default blank, meaning no chests refresh, in the format of 'modid:loot_table']")
  public static String[] REFRESH_LOOT_TABLES = new String[0];
  @Config.Comment("list of dimensions where loot chests should automatically refresh [default: blank, e.g., 0]")
  public static int[] REFRESH_DIMENSIONS = new int[0];
  @Config.Comment("set to true to use vanilla textures instead of Lootr special textures. Note: this will prevent previously opened chests from rendering differently")
  public static boolean VANILLA_TEXTURES = false;

  private static Set<String> DECAY_MODS = null;
  private static Set<ResourceLocation> DECAY_TABLES = null;
  private static Set<String> REFRESH_MODS = null;
  private static Set<ResourceLocation> REFRESH_TABLES = null;

  private static Set<DimensionType> DIM_WHITELIST = null;
  private static Set<DimensionType> DIM_BLACKLIST = null;
  private static Set<DimensionType> DECAY_DIMS = null;
  private static Set<DimensionType> REFRESH_DIMS = null;
  private static Set<ResourceLocation> LOOT_BLACKLIST = null;
  private static Set<ResourceLocation> ADD_CHESTS = null;
  private static Set<ResourceLocation> ADD_TRAPPED_CHESTS = null;
  private static Map<Block, Block> replacements = null;

  private static Set<String> LOOT_MOD_BLACKLIST = null;


  @SubscribeEvent
  public static void reloadConfig(ConfigChangedEvent.OnConfigChangedEvent event) {
    if(event.getModID().equals(Lootr.MODID)) {
      net.minecraftforge.common.config.ConfigManager.sync(Lootr.MODID, Config.Type.INSTANCE);
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

  private static <T> Set<T> mapIdListToTypes(int[] list, IntFunction<T> func) {
    return Arrays.stream(list).mapToObj(func).collect(Collectors.toSet());
  }

  public static Set<DimensionType> getDimensionWhitelist() {
    if (DIM_WHITELIST == null) {
      DIM_WHITELIST = mapIdListToTypes(DIMENSION_WHITELIST, DimensionManager::getProviderType);
    }
    return DIM_WHITELIST;
  }

  public static Set<DimensionType> getDimensionBlacklist() {
    if (DIM_BLACKLIST == null) {
      DIM_BLACKLIST = mapIdListToTypes(DIMENSION_BLACKLIST, DimensionManager::getProviderType);
    }
    return DIM_BLACKLIST;
  }

  public static Set<DimensionType> getDecayDimensions() {
    if (DECAY_DIMS == null) {
      DECAY_DIMS = mapIdListToTypes(DECAY_DIMENSIONS, DimensionManager::getProviderType);
    }
    return DECAY_DIMS;
  }

  public static Set<DimensionType> getRefreshDimensions() {
    if (REFRESH_DIMS == null) {
      REFRESH_DIMS = mapIdListToTypes(REFRESH_DIMENSIONS, DimensionManager::getProviderType);
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
      LOOT_BLACKLIST = Arrays.stream(LOOT_TABLE_BLACKLIST).map(ResourceLocation::new).collect(Collectors.toSet());
      // FIX for https://github.com/noobanidus/Lootr/issues/74
      // Converting this atum chest results in completely breaking the
      // atum pyramid fight, etc.
      LOOT_BLACKLIST.addAll(ATUM_BLACKLIST);
    }
    return LOOT_BLACKLIST;
  }

  public static Set<String> getModBlacklist() {
    if (LOOT_MOD_BLACKLIST == null) {
      LOOT_MOD_BLACKLIST = Arrays.stream(LOOT_MODID_BLACKLIST).map(String::toLowerCase).collect(Collectors.toSet());
    }
    return LOOT_MOD_BLACKLIST;
  }

  public static Set<ResourceLocation> getDecayingTables() {
    if (DECAY_TABLES == null) {
      DECAY_TABLES = Arrays.stream(DECAY_LOOT_TABLES).map(ResourceLocation::new).collect(Collectors.toSet());
    }
    return DECAY_TABLES;
  }

  public static Set<ResourceLocation> getRefreshingTables() {
    if (REFRESH_TABLES == null) {
      REFRESH_TABLES = Arrays.stream(REFRESH_LOOT_TABLES).map(ResourceLocation::new).collect(Collectors.toSet());
    }
    return REFRESH_TABLES;
  }

  public static Set<String> getDecayMods() {
    if (DECAY_MODS == null) {
      DECAY_MODS = Arrays.stream(DECAY_MODIDS).map(o -> o.toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
    }
    return DECAY_MODS;
  }

  public static Set<String> getRefreshMods() {
    if (REFRESH_MODS == null) {
      REFRESH_MODS = Arrays.stream(REFRESH_MODIDS).map(o -> o.toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
    }
    return REFRESH_MODS;
  }

  public static Set<ResourceLocation> getAdditionalChests() {
    if (ADD_CHESTS == null) {
      ADD_CHESTS = Arrays.stream(ADDITIONAL_CHESTS).map(ResourceLocation::new).collect(Collectors.toSet());
    }
    return ADD_CHESTS;
  }

  public static Set<ResourceLocation> getAdditionalTrappedChests() {
    if (ADD_TRAPPED_CHESTS == null) {
      ADD_TRAPPED_CHESTS = Arrays.stream(ADDITIONAL_TRAPPED_CHESTS).map(ResourceLocation::new).collect(Collectors.toSet());
    }
    return ADD_TRAPPED_CHESTS;
  }

  public static boolean isDimensionBlocked(DimensionType key) {
    return (!getDimensionWhitelist().isEmpty() && !getDimensionWhitelist().contains(key)) || getDimensionBlacklist().contains(key);
  }

  public static boolean isDimensionDecaying(DimensionType key) {
    return (getDecayDimensions().contains(key));
  }

  public static boolean isDimensionRefreshing(DimensionType key) {
    return getRefreshDimensions().contains(key);
  }

  public static boolean isDecaying(World world, ILootTile tile) {
    if (DECAY_ALL) {
      return true;
    }

    if(tile.getTable() != null) {
      if (getDecayingTables().contains(tile.getTable())) {
        return true;
      }
      if (getDecayMods().contains(tile.getTable().getNamespace().toLowerCase(Locale.ROOT))) {
        return true;
      }
    }
    return isDimensionDecaying(world.provider.getDimensionType());
  }

  public static boolean isRefreshing(World world, ILootTile tile) {
    if (REFRESH_ALL) {
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
    return isDimensionRefreshing(world.provider.getDimensionType());
  }

  public static boolean isDecaying(World world, LootrChestMinecartEntity entity) {
    if (DECAY_ALL) {
      return true;
    }

    if (getDecayingTables().contains(entity.lootTable)) {
      return true;
    }
    if (getDecayMods().contains(entity.lootTable.getNamespace().toLowerCase(Locale.ROOT))) {
      return true;
    }
    return isDimensionDecaying(world.provider.getDimensionType());
  }

  public static boolean isRefreshing(World world, LootrChestMinecartEntity entity) {
    if (REFRESH_ALL) {
      return true;
    }

    if (getRefreshingTables().contains(entity.lootTable)) {
      return true;
    }
    if (getRefreshMods().contains(entity.lootTable.getNamespace().toLowerCase(Locale.ROOT))) {
      return true;
    }
    return isDimensionRefreshing(world.provider.getDimensionType());
  }

  private static void addSafeReplacement(ResourceLocation location, Block replacement) {
    Block block = ForgeRegistries.BLOCKS.getValue(location);
    if (block != null) {
      replacements.put(block, replacement);
    }
  }

  private static void addUnsafeReplacement(ResourceLocation location, Block replacement, WorldServer world) {
    Block block = ForgeRegistries.BLOCKS.getValue(location);
    if (block != null) {
      TileEntity tile = block.createTileEntity(world, block.getDefaultState());
      if (tile instanceof TileEntityLockableLoot) {
        replacements.put(block, replacement);
      }
    }
  }

  // TODO: Move this to the config module?
  public static IBlockState replacement(IBlockState original) {
    if (replacements == null) {
      replacements = new HashMap<>();
      replacements.put(Blocks.CHEST, ModBlocks.CHEST);
      replacements.put(Blocks.TRAPPED_CHEST, ModBlocks.TRAPPED_CHEST);
      replacements.put(Blocks.PURPLE_SHULKER_BOX, ModBlocks.SHULKER);

      if (CONVERT_QUARK && Loader.isModLoaded("quark")) {
        QUARK_CHESTS.forEach(o -> addSafeReplacement(o, ModBlocks.CHEST));
        QUARK_TRAPPED_CHESTS.forEach(o -> addSafeReplacement(o, ModBlocks.TRAPPED_CHEST));
      }

      if (!getAdditionalChests().isEmpty() || !getAdditionalTrappedChests().isEmpty()) {
        final WorldServer world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);
        getAdditionalChests().forEach(o -> addUnsafeReplacement(o, ModBlocks.CHEST, world));
        getAdditionalTrappedChests().forEach(o -> addUnsafeReplacement(o, ModBlocks.TRAPPED_CHEST, world));
      }
    }

    Block replacement = replacements.get(original.getBlock());
    if (replacement == null) {
      return null;
    }

    return copyProperties(replacement.getDefaultState(), original);
  }

  private static IBlockState copyProperties(IBlockState state, IBlockState original) {
    for (IProperty<?> prop : original.getPropertyKeys()) {
      if (state.getPropertyKeys().contains(prop)) {
        state = safeReplace(state, original, prop);
      }
    }
    return state;
  }

  private static <V extends Comparable<V>> IBlockState safeReplace(IBlockState state, IBlockState original, IProperty<V> property) {
    // TODO: Bit of a dirty hack
    if (original.getPropertyKeys().contains(property) && state.getPropertyKeys().contains(property)) {
      return state.withProperty(property, original.getValue(property));
    }
    return state;
  }

  public static boolean isVanillaTextures () {
    return VANILLA_TEXTURES;
  }

  public static int getDecayValue() {
    return DECAY_VALUE;
  }
  public static int getRefreshValue() {
    return REFRESH_VALUE;
  }
}
