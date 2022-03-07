package net.zestyblaze.lootr.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.api.blockentity.ILootBlockEntity;
import net.zestyblaze.lootr.entity.LootrChestMinecartEntity;
import net.zestyblaze.lootr.util.StructureUtil;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Config(name = LootrAPI.MODID)
public class LootrModConfig implements ConfigData {
    private static Set<String> DECAY_MODS = null;
    private static Set<ResourceLocation> DECAY_TABLES = null;
    private static Set<String> REFRESH_MODS = null;
    private static Set<ResourceLocation> REFRESH_TABLES = null;

    private static Set<ResourceKey<Level>> DIM_WHITELIST = null;
    private static Set<ResourceKey<Level>> DIM_BLACKLIST = null;
    private static Set<ResourceKey<Level>> DECAY_DIMS = null;
    private static Set<ResourceKey<Level>> REFRESH_DIMS = null;
    private static Set<ResourceLocation> LOOT_BLACKLIST = null;
    private static Set<ResourceLocation> STRUCTURE_BLACKLIST = null;
    private static Set<ResourceLocation> REFRESH_STRUCTS = null;
    private static Set<ResourceLocation> DECAY_STRUCTS = null;
    private static Set<ResourceLocation> ADD_CHESTS = null;
    private static Set<ResourceLocation> ADD_TRAPPED_CHESTS = null;
    private static Map<Block, Block> replacements = null;
    private static Set<String> LOOT_MODIDS = null;

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

    public static boolean isDecaying(ServerLevel level, ILootBlockEntity tile) {
        if (get().decay.decay_all) {
            return true;
        }
        if (tile.getTable() != null) {
            if (getDecayingTables().contains(tile.getTable())) {
                return true;
            }
            if (getDecayMods().contains(tile.getTable().getNamespace())) {
                return true;
            }
        }
        if (!getDecayStructures().isEmpty()) {
            StructureFeature<?> startAt = StructureUtil.featureFor(level, tile.getPosition());
            /*if (startAt != null && getDecayStructures().contains(startAt.getRegistryName())) {
                return true;
            }

             */
        }
        return isDimensionDecaying(level.dimension());
    }

    public static boolean isDecaying(ServerLevel level, LootrChestMinecartEntity entity) {
        if (get().decay.decay_all) {
            return true;
        }
        if (entity.lootTable != null) {
            if (getDecayingTables().contains(entity.lootTable)) {
                return true;
            }
            if (getDecayMods().contains(entity.lootTable.getNamespace())) {
                return true;
            }
        }
        if (!getDecayStructures().isEmpty()) {
            StructureFeature<?> startAt = StructureUtil.featureFor(level, new BlockPos(entity.position()));
            /*if (startAt != null && getDecayStructures().contains(startAt.getRegistryName())) {
                return true;
            }

             */
        }
        return isDimensionDecaying(level.dimension());
    }

    public static boolean isRefreshing(ServerLevel level, LootrChestMinecartEntity entity) {
        if(get().refresh.refresh_all) {
            return true;
        }
        if(entity.lootTable != null) {
            if (getRefreshingTables().contains(entity.lootTable)) {
                return true;
            }

            if(getDecayMods().contains(entity.lootTable.getNamespace())) {
                return true;
            }
        }
        if(!getRefreshStructures().isEmpty()) {
            StructureFeature<?> startAt = StructureUtil.featureFor(level, new BlockPos(entity.position()));
            /*if(startAt != null && getRefreshStructures().contains(startAt.getRegistryName())) {
                return true;
            }

             */
        }
        return isDimensionRefreshing(level.dimension());
    }

    public static Set<ResourceLocation> getDecayingTables() {
        if (DECAY_TABLES == null) {
            DECAY_TABLES = get().decay.decay_loot_tables.stream().map(ResourceLocation::new).collect(Collectors.toSet());
        }
        return DECAY_TABLES;
    }

    public static Set<String> getDecayMods() {
        if (DECAY_MODS == null) {
            DECAY_MODS = get().decay.decay_modids.stream().map(o -> o.toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
        }
        return DECAY_MODS;
    }

    public static Set<ResourceLocation> getDecayStructures () {
        if (DECAY_STRUCTS == null) {
            DECAY_STRUCTS = get().decay.decay_structures.stream().map(ResourceLocation::new).collect(Collectors.toSet());
        }
        return DECAY_STRUCTS;
    }

    public static boolean isDimensionDecaying(ResourceKey<Level> key) {
        return getDecayDimensions().contains(key);
    }

    public static Set<ResourceKey<Level>> getDecayDimensions() {
        if (DECAY_DIMS == null) {
            DECAY_DIMS = get().decay.decay_dimensions.stream().map(o -> ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(o))).collect(Collectors.toSet());
        }
        return DECAY_DIMS;
    }

    public static boolean isRefreshing (ServerLevel level, ILootBlockEntity tile) {
        if (get().refresh.refresh_all) {
            return true;
        }
        if (tile.getTable() != null) {
            if (getRefreshingTables().contains(tile.getTable())) {
                return true;
            }
            if (getRefreshMods().contains(tile.getTable().getNamespace())) {
                return true;
            }
        }
        if (!getRefreshStructures().isEmpty()) {
            StructureFeature<?> startAt = StructureUtil.featureFor(level, tile.getPosition());
            /*if (startAt != null && getRefreshStructures().contains(startAt.getRegistryName())) {
                return true;
            }
            
             */
        }
        return isDimensionRefreshing(level.dimension());
    }

    public static Set<ResourceLocation> getRefreshingTables () {
        if (REFRESH_TABLES == null) {
            REFRESH_TABLES = get().refresh.refresh_loot_tables.stream().map(ResourceLocation::new).collect(Collectors.toSet());
        }
        return REFRESH_TABLES;
    }

    public static Set<String> getRefreshMods () {
        if (REFRESH_MODS == null) {
            REFRESH_MODS = get().refresh.refresh_modids.stream().map(o -> o.toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
        }
        return REFRESH_MODS;
    }

    public static Set<ResourceLocation> getRefreshStructures () {
        if (REFRESH_STRUCTS == null) {
            REFRESH_STRUCTS = get().refresh.refresh_structures.stream().map(ResourceLocation::new).collect(Collectors.toSet());
        }
        return REFRESH_STRUCTS;
    }

    public static boolean isDimensionRefreshing(ResourceKey<Level> key) {
        return getRefreshDimensions().contains(key);
    }

    public static Set<ResourceKey<Level>> getRefreshDimensions() {
        if (REFRESH_DIMS == null) {
            REFRESH_DIMS = get().refresh.refresh_dimensions.stream().map(o -> ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(o))).collect(Collectors.toSet());
        }
        return REFRESH_DIMS;
    }
}
