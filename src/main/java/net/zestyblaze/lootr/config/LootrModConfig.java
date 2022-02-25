package net.zestyblaze.lootr.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.zestyblaze.lootr.Lootr;
import net.zestyblaze.lootr.api.LootrAPI;

@Config(name = LootrAPI.MODID)
public class LootrModConfig implements ConfigData {
    @ConfigEntry.Gui.CollapsibleObject
    public Debug debug = new Debug();

    public static class Debug {
        @ConfigEntry.Gui.RequiresRestart
        public boolean debugMode = false;
        @ConfigEntry.Gui.RequiresRestart
        public boolean report_unresolved_tables = false;
    }

    @ConfigEntry.Gui.CollapsibleObject
    public Seed seed = new Seed();

    public static class Seed {
        @ConfigEntry.Gui.RequiresRestart
        public boolean randomize_seed = true;
    }

    public static class Conversion {
        @ConfigEntry.Gui.RequiresRestart
        public boolean skip_unloaded = true;
        @ConfigEntry.Gui.RequiresRestart
        public int maximum_age = 3600;
    }

    public static LootrModConfig get() {
        return AutoConfig.getConfigHolder(LootrModConfig.class).getConfig();
    }
}
