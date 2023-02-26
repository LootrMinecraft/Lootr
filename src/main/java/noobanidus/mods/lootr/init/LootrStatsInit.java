package noobanidus.mods.lootr.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.config.LootrModConfig;

public class LootrStatsInit {
  public static ResourceLocation LOOTED_LOCATION = new ResourceLocation(LootrAPI.MODID, "looted_stat");
  public static Stat<ResourceLocation> LOOTED_STAT;

  public static void registerStats() {
    Registry.register(Registry.CUSTOM_STAT, LOOTED_LOCATION, LOOTED_LOCATION);
    LOOTED_STAT = Stats.CUSTOM.get(LOOTED_LOCATION);

    if (LootrModConfig.get().debug.debugMode) {
      LootrAPI.LOG.info("Lootr: Common Registry - Stats Registered");
    }
  }
}
