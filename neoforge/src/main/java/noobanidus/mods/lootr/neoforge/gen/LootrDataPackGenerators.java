package noobanidus.mods.lootr.neoforge.gen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// Only used for testing
//@EventBusSubscriber(modid=LootrAPI.MODID)
public class LootrDataPackGenerators {
  // Salts used for the randomization of structure placements
  private static final int STRUCTURE_SALT = 8266497;

  private static final ResourceKey<Structure> LOOTR_TEST_STRUCTURE = ResourceKey.create(Registries.STRUCTURE, LootrAPI.rl("test"));
  private static final ResourceKey<StructureTemplatePool> LOOTR_TEST_START_POOL = ResourceKey.create(Registries.TEMPLATE_POOL, LootrAPI.rl("test_start_pool"));
  private static final ResourceKey<StructureSet> LOOTR_TEST_STRUCTURE_SET = ResourceKey.create(Registries.STRUCTURE_SET, LootrAPI.rl("test_structure_set"));

  //@SubscribeEvent
  public static void onGatherData(GatherDataEvent.Client event) {
    event.getGenerator().addProvider(
        true,
        (DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(
            event.getGenerator().getPackOutput(),
            event.getLookupProvider(),
            new RegistrySetBuilder()
                .add(Registries.TEMPLATE_POOL, bootstrap -> {
                  HolderGetter<StructureTemplatePool> getter = bootstrap.lookup(Registries.TEMPLATE_POOL);
                  Holder<StructureTemplatePool> holder = getter.getOrThrow(Pools.EMPTY);
                  bootstrap.register(LOOTR_TEST_START_POOL, new StructureTemplatePool(holder, List.of(Pair.of(StructurePoolElement.single(LootrAPI.rl("test")
                      .toString()), 1)), StructureTemplatePool.Projection.RIGID));
                })
                .add(Registries.STRUCTURE, bootstrap -> {
                  HolderGetter<Biome> biomeGetter = bootstrap.lookup(Registries.BIOME);
                  HolderGetter<StructureTemplatePool> poolGetter = bootstrap.lookup(Registries.TEMPLATE_POOL);
                  bootstrap.register(LOOTR_TEST_STRUCTURE, new JigsawStructure(new Structure.StructureSettings(biomeGetter.getOrThrow(BiomeTags.HAS_BURIED_TREASURE), Collections.emptyMap(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_BOX), poolGetter.getOrThrow(LOOTR_TEST_START_POOL), Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Optional.of(Heightmap.Types.WORLD_SURFACE_WG), new JigsawStructure.MaxDistance(80), Collections.emptyList(), DimensionPadding.ZERO, LiquidSettings.IGNORE_WATERLOGGING));
                })
                .add(Registries.STRUCTURE_SET, bootstrap -> {
                  HolderGetter<Structure> structureGetter = bootstrap.lookup(Registries.STRUCTURE);
                  bootstrap.register(LOOTR_TEST_STRUCTURE_SET, new StructureSet(structureGetter.getOrThrow(LOOTR_TEST_STRUCTURE), new RandomSpreadStructurePlacement(15, 2, RandomSpreadType.LINEAR, STRUCTURE_SALT)));
                }),
            Set.of(LootrAPI.MODID)
        )
    );
  }
}