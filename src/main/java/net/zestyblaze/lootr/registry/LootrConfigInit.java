package net.zestyblaze.lootr.registry;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.config.LootrModConfig;

public class LootrConfigInit {
    public static void registerConfig() {
        AutoConfig.register(LootrModConfig.class, GsonConfigSerializer::new);

        if(LootrModConfig.get().debug.debugMode) {
            LootrAPI.LOG.info("Lootr: Registry - Config Registered");
        }
    }
}
