package net.zestyblaze.lootr.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.config.LootrModConfig;

public class LootrStatsInit {
    public static ResourceLocation LOOTED_LOCATION = new ResourceLocation(LootrAPI.MODID, "looted_stat");
    public static Stat<ResourceLocation> LOOTED_STAT;

    public static void registerStats() {
        Registry.register(BuiltInRegistries.CUSTOM_STAT, LOOTED_LOCATION, LOOTED_LOCATION);
        LOOTED_STAT = Stats.CUSTOM.get(LOOTED_LOCATION);
    }
}
