package noobanidus.mods.lootr.registry;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import noobanidus.mods.lootr.config.LootrModConfig;

public class LootrConfigInit {
    public static void registerConfig() {
        AutoConfig.register(LootrModConfig.class, GsonConfigSerializer::new);
    }
}
