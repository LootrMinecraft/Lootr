package net.zestyblaze.lootr.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.zestyblaze.lootr.api.LootrAPI;

import java.util.List;

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

    @ConfigEntry.Gui.CollapsibleObject
    public Conversion conversion = new Conversion();

    public static class Conversion {
        @ConfigEntry.Gui.RequiresRestart
        public boolean skip_unloaded = true;
        @ConfigEntry.Gui.RequiresRestart
        public int maximum_age = 3600;
        @ConfigEntry.Gui.RequiresRestart
        public boolean convert_mineshafts = true;
    }

    @ConfigEntry.Gui.CollapsibleObject
    public Breaking breaking = new Breaking();

    public static class Breaking {
        @ConfigEntry.Gui.RequiresRestart
        public boolean disable_break = false;
    }

    @ConfigEntry.Gui.CollapsibleObject
    public Lists lists = new Lists();

    public static class Lists {
        @ConfigEntry.Gui.RequiresRestart
        public List<String> dimension_whitelist = List.of();
        @ConfigEntry.Gui.RequiresRestart
        public List<String> dimension_blacklist = List.of();
        @ConfigEntry.Gui.RequiresRestart
        public List<String> loot_table_blacklist = List.of();
        @ConfigEntry.Gui.RequiresRestart
        public List<String> loot_modid_blacklist = List.of();
        @ConfigEntry.Gui.RequiresRestart
        public List<String> loot_structure_blacklist = List.of();
    }

    @ConfigEntry.Gui.CollapsibleObject
    public Decay decay = new Decay();

    public static class Decay {
        @ConfigEntry.Gui.RequiresRestart
        public int decay_value = 6000;
        @ConfigEntry.Gui.RequiresRestart
        public boolean decay_all = false;
        @ConfigEntry.Gui.RequiresRestart
        public List<String> decay_modids = List.of();
        @ConfigEntry.Gui.RequiresRestart
        public List<String> decay_loot_tables = List.of();
        @ConfigEntry.Gui.RequiresRestart
        public List<String> decay_dimensions = List.of();
        @ConfigEntry.Gui.RequiresRestart
        public List<String> decay_structures = List.of();
    }

    @ConfigEntry.Gui.CollapsibleObject
    public Refresh refresh = new Refresh();

    public static class Refresh {
        @ConfigEntry.Gui.RequiresRestart
        public int refresh_value = 24000;
        @ConfigEntry.Gui.RequiresRestart
        public boolean refresh_all = false;
        @ConfigEntry.Gui.RequiresRestart
        public List<String> refresh_modids = List.of();
        @ConfigEntry.Gui.RequiresRestart
        public List<String> refresh_loot_tables = List.of();
        @ConfigEntry.Gui.RequiresRestart
        public List<String> refresh_dimensions = List.of();
        @ConfigEntry.Gui.RequiresRestart
        public List<String> refresh_structures = List.of();
    }

    public static LootrModConfig get() {
        return AutoConfig.getConfigHolder(LootrModConfig.class).getConfig();
    }
}
