package noobanidus.mods.lootr.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import noobanidus.mods.lootr.api.LootrAPI;

public class ModStats {
  public static ResourceLocation LOOTED_LOCATION = new ResourceLocation(LootrAPI.MODID, "looted_stat");
  public static Stat<ResourceLocation> LOOTED_STAT;

  public static void load() {
    LOOTED_STAT = Stats.CUSTOM.get(LOOTED_LOCATION);
  }
}
