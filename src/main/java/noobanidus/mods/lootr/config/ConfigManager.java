package noobanidus.mods.lootr.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class ConfigManager {
  private static ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

  public static ForgeConfigSpec COMMON_CONFIG;
  public static ForgeConfigSpec.BooleanValue RANDOMISE_SEED;
  public static ForgeConfigSpec.BooleanValue CONVERT_MINESHAFTS;
  public static ForgeConfigSpec.BooleanValue REPORT_TABLES;

  static {
    RANDOMISE_SEED = COMMON_BUILDER.comment("determine whether or not loot generated is the same for all players using the provided seed, or randomised per player").define("randomise_seed", true);
    CONVERT_MINESHAFTS = COMMON_BUILDER.comment("whether or not mineshaft chest minecarts should be converted to standard loot chests").define("convert_mineshafts", true);
    REPORT_TABLES = COMMON_BUILDER.comment("catches loot chest creation that this mod cannot convert, reporting the loot table, location and mod").define("report_tables", false);
    COMMON_CONFIG = COMMON_BUILDER.build();
  }

  public static void loadConfig(ForgeConfigSpec spec, Path path) {
    CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
    configData.load();
    spec.setConfig(configData);
  }
}
