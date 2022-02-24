package net.zestyblaze.lootr.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.zestyblaze.lootr.Lootr;

@Config(name = Lootr.MODID)
public class LootrModConfig implements ConfigData {
    public boolean debugMode = false;

    public static LootrModConfig get() {
        return AutoConfig.getConfigHolder(LootrModConfig.class).getConfig();
    }
}
