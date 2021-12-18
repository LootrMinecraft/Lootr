package noobanidus.mods.lootr.init;

import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.resources.ResourceLocation;
import noobanidus.mods.lootr.Lootr;

public class ModStats {
  public static ResourceLocation LOOTED_LOCATION = new ResourceLocation(Lootr.MODID, "looted_stat");
  public static Stat<ResourceLocation> LOOTED_STAT;

  public static void load() {
    LOOTED_STAT = Stats.CUSTOM.get(LOOTED_LOCATION);
  }
}
