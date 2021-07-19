package noobanidus.mods.lootr.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import noobanidus.mods.lootr.Lootr;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid= Lootr.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class ConfigManager {
  private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

  public static ForgeConfigSpec COMMON_CONFIG;
  public static final ForgeConfigSpec.BooleanValue RANDOMISE_SEED;
  public static final ForgeConfigSpec.BooleanValue CONVERT_MINESHAFTS;
  public static final ForgeConfigSpec.BooleanValue CONVERT_QUARK;
  public static final ForgeConfigSpec.BooleanValue CONVERT_WOODEN_CHESTS;
  public static final ForgeConfigSpec.BooleanValue CONVERT_TRAPPED_CHESTS;
  public static final ForgeConfigSpec.BooleanValue REPORT_TABLES;
  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DIMENSION_WHITELIST;
  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DIMENSION_BLACKLIST;
  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> LOOT_TABLE_BLACKLIST;

  private static Set<RegistryKey<World>> DIM_WHITELIST = null;
  private static Set<RegistryKey<World>> DIM_BLACKLIST = null;
  private static Set<ResourceLocation> LOOT_BLACKLIST = null;

  static {
    RANDOMISE_SEED = COMMON_BUILDER.comment("determine whether or not loot generated is the same for all players using the provided seed, or randomised per player").define("randomise_seed", true);
    CONVERT_MINESHAFTS = COMMON_BUILDER.comment("whether or not mineshaft chest minecarts should be converted to standard loot chests").define("convert_mineshafts", true);
    CONVERT_QUARK = COMMON_BUILDER.comment("whether or not quark chests used in world generation for loot purposes should be replaced with Lootr chests").define("convert_quark", true);
    CONVERT_WOODEN_CHESTS = COMMON_BUILDER.comment("whether or not the entire forge:chests/wooden tag should be added to the conversion list for structures (if they are backed by LockableLootTileEntity)").define("convert_wooden_chests", true);
    CONVERT_TRAPPED_CHESTS = COMMON_BUILDER.comment("whether or not the entire forge:chests/trapped tag should be added to the conversion list for structures (if they are backed by LockableLootTileEntity").define("convert_trapped_chests", true);
    REPORT_TABLES = COMMON_BUILDER.comment("catches loot chest creation that this mod cannot convert, reporting the loot table, location and mod").define("report_tables", false);
    List<? extends String> empty = Collections.emptyList();
    Predicate<Object> validator = o -> o instanceof String && ((String)o).contains(":");
    DIMENSION_WHITELIST = COMMON_BUILDER.comment("list of dimensions (to the exclusion of all others) that loot chest should be replaced in [default: blank, allowing all dimensions, e.g., minecraft:overworld]").defineList("dimension_whitelist", empty, validator);
    DIMENSION_BLACKLIST = COMMON_BUILDER.comment("list of dimensions that loot chests should not be replaced in [default: blank, allowing all dimensions, format e.g., minecraft:overworld]").defineList("dimension_blacklist", empty, validator);
    LOOT_TABLE_BLACKLIST = COMMON_BUILDER.comment("list of loot tables which shouldn't be converted [in the format of modid:loot_table]").defineList("loot_table_blacklist", empty, validator);
    COMMON_CONFIG = COMMON_BUILDER.build();
  }

  public static void loadConfig(ForgeConfigSpec spec, Path path) {
    CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
    configData.load();
    spec.setConfig(configData);
  }

  @SubscribeEvent
  public static void reloadConfig (ModConfig.ModConfigEvent event) {
    COMMON_CONFIG.setConfig(event.getConfig().getConfigData());
    DIM_WHITELIST = null;
    DIM_BLACKLIST = null;
    LOOT_BLACKLIST = null;
  }

  public static Set<RegistryKey<World>> getDimensionWhitelist () {
    if (DIM_WHITELIST == null) {
      DIM_WHITELIST = DIMENSION_WHITELIST.get().stream().map(o -> RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(o))).collect(Collectors.toSet());
    }
    return DIM_WHITELIST;
  }

  public static Set<RegistryKey<World>> getDimensionBlacklist() {
    if (DIM_BLACKLIST == null) {
      DIM_BLACKLIST = DIMENSION_BLACKLIST.get().stream().map(o -> RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(o))).collect(Collectors.toSet());
    }
    return DIM_BLACKLIST;
  }

  public static Set<ResourceLocation> getLootBlacklist () {
    if (LOOT_BLACKLIST == null) {
      LOOT_BLACKLIST = LOOT_TABLE_BLACKLIST.get().stream().map(ResourceLocation::new).collect(Collectors.toSet());
    }
    return LOOT_BLACKLIST;
  }

  public static boolean isDimensionBlocked (RegistryKey<World> key) {
    return (!getDimensionWhitelist().isEmpty() && !getDimensionWhitelist().contains(key)) || getDimensionBlacklist().contains(key);
  }
}
