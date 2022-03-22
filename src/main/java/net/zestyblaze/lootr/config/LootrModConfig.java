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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.api.blockentity.ILootBlockEntity;
import net.zestyblaze.lootr.entity.LootrChestMinecartEntity;
import net.zestyblaze.lootr.registry.LootrBlockInit;
import net.zestyblaze.lootr.util.StructureUtil;

import java.util.*;
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

    @ConfigEntry.Gui.CollapsibleObject
    public Vanilla vanilla = new Vanilla();

    public static class Vanilla {
        @ConfigEntry.Gui.RequiresRestart
        public boolean vanilla_textures = false;
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

    private static void addSafeReplacement(ResourceLocation location, Block replacement) {
        Block block = Registry.BLOCK.get(location);
        if (block != Blocks.AIR) {
            replacements.put(block, replacement);
        }
    }

    private static void addUnsafeReplacement(ResourceLocation location, Block replacement, ServerLevel world) {
        Block block = Registry.BLOCK.get(location);
        if (block instanceof EntityBlock entityBlock) {
            BlockEntity tile = entityBlock.newBlockEntity(BlockPos.ZERO, block.defaultBlockState());
            if (tile instanceof RandomizableContainerBlockEntity) {
                replacements.put(block, replacement);
            }
        }
    }

    // TODO: Move this to the config module?
    public static BlockState replacement(BlockState original) {
        if (replacements == null) {
            replacements = new HashMap<>();
            replacements.put(Blocks.CHEST, LootrBlockInit.CHEST);
            // TODO:
/*            replacements.put(Blocks.BARREL, LootrBlockInit.BARREL);
            replacements.put(Blocks.TRAPPED_CHEST, LootrBlockInit.TRAPPED_CHEST);
            replacements.put(Blocks.SHULKER_BOX, LootrBlockInit.SHULKER);*/

           // TODO: Is Quark on Fabric?
/*            if (CONVERT_QUARK.get() && ModList.get().isLoaded("quark")) {
                QUARK_CHESTS.forEach(o -> addSafeReplacement(o, ModBlocks.CHEST));
                QUARK_TRAPPED_CHESTS.forEach(o -> addSafeReplacement(o, ModBlocks.TRAPPED_CHEST));
            }


            if (CONVERT_WOODEN_CHESTS.get() || CONVERT_TRAPPED_CHESTS.get()) {
                if (CONVERT_WOODEN_CHESTS.get()) {
                    Registry.BLOCK.getTagOrEmpty(Tags.Blocks.CHESTS_WOODEN).forEach(z -> {
                        Block o = z.value();
                        if (replacements.containsKey(o)) {
                            return;
                        }
                        if (o instanceof EntityBlock) {
                            BlockEntity tile = ((EntityBlock) o).newBlockEntity(BlockPos.ZERO, o.defaultBlockState());
                            if (tile instanceof RandomizableContainerBlockEntity) {
                                replacements.put(o, ModBlocks.CHEST);
                            }
                        }
                    });
                }
                if (CONVERT_TRAPPED_CHESTS.get()) {
                    Registry.BLOCK.getTagOrEmpty(Tags.Blocks.CHESTS_TRAPPED).forEach(z -> {
                        Block o = z.value();
                        if (replacements.containsKey(o)) {
                            return;
                        }
                        if (o instanceof EntityBlock) {
                            BlockEntity tile = ((EntityBlock) o).newBlockEntity(BlockPos.ZERO, o.defaultBlockState());
                            if (tile instanceof RandomizableContainerBlockEntity) {
                                replacements.put(o, ModBlocks.TRAPPED_CHEST);
                            }
                        }
                    });
                }
            }
            if (!getAdditionalChests().isEmpty() || !getAdditionalTrappedChests().isEmpty()) {
                final ServerLevel world = ServerLifecycleHooks.getCurrentServer().overworld();
                getAdditionalChests().forEach(o -> addUnsafeReplacement(o, ModBlocks.CHEST, world));
                getAdditionalTrappedChests().forEach(o -> addUnsafeReplacement(o, ModBlocks.TRAPPED_CHEST, world));
            }*/
        }

        Block replacement = replacements.get(original.getBlock());
        if (replacement == null) {
            return null;
        }

        return copyProperties(replacement.defaultBlockState(), original);
    }

    private static BlockState copyProperties(BlockState state, BlockState original) {
        for (Property<?> prop : original.getProperties()) {
            if (state.hasProperty(prop)) {
                state = safeReplace(state, original, prop);
            }
        }
        return state;
    }

    private static <V extends Comparable<V>> BlockState safeReplace(BlockState state, BlockState original, Property<V> property) {
        // TODO: Bit of a dirty hack
        if (property == ChestBlock.TYPE && state.hasProperty(property)) {
            return state.setValue(ChestBlock.TYPE, ChestType.SINGLE);
        }
        if (original.hasProperty(property) && state.hasProperty(property)) {
            return state.setValue(property, original.getValue(property));
        }
        return state;
    }

    public static boolean isVanillaTextures () {
        return get().vanilla.vanilla_textures;
    }
}
