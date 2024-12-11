package noobanidus.mods.lootr.neoforge.gen.compat;

import net.minecraft.DetectedVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = LootrAPI.MODID, bus = EventBusSubscriber.Bus.MOD)
public class LootrCompatDataGenerators {
  private static final List<DataGenerator> generators = new ArrayList<>();

  private static DataGenerator makeGenerator (Path path, Component description) {
    DataGenerator generator = new DataGenerator(path, DetectedVersion.tryDetectVersion(), true);
    generator.addProvider(true, new PackMetadataGenerator(generator.getPackOutput()).add(PackMetadataSection.TYPE, new PackMetadataSection(description, 15, Optional.empty()))); // 15 for compatibility
    generators.add(generator);
    return generator;
  }

  @SubscribeEvent
  public static void gatherData (GatherDataEvent event) {
    PackOutput output = event.getGenerator().getPackOutput();
    CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
    ExistingFileHelper helper = event.getExistingFileHelper();

    Path root = output.getOutputFolder().getParent().getParent().getParent().getParent();

    Path datapacks = root.resolve("datapacks"); // a hack

    // Data pack generation
    var generator = makeGenerator(datapacks.resolve("betterend"), Component.literal("Lootr Compatibility for BetterEnd"));
    generator.addProvider(event.includeServer(), new LootrCompatBlockTagProvider("better_end", List.of("mossy_glowshroom_barrel","end_lotus_barrel","pythadendron_barrel", "lacugrove_barrel", "dragon_tree_barrel", "tenanea_barrel", "helix_tree_barrel", "umbrella_tree_barrel", "jellyshroom_barrel", "lucernia_barrel"), List.of("mossy_glowshroom_chest","end_lotus_chest","pythadendron_chest", "lacugrove_chest", "dragon_tree_chest", "tenanea_chest", "helix_tree_chest", "umbrella_tree_chest", "jellyshroom_chest", "lucernia_chest"), null, null, generator, provider, helper));

    generator = makeGenerator(datapacks.resolve("betternether"), Component.literal("Lootr Compatibility for BetterNether"));
    generator.addProvider(event.includeServer(), new LootrCompatBlockTagProvider("betternether", List.of("nether_reed_barrel", "stalagnate_barrel", "willow_barrel", "wart_barrel", "warped_barrel", "crimson_barrel", "rubeus_barrel", "mushroom_fir_barrel", "nether_mushroom_barrel", "anchor_tree_barrel", "nether_sakura_barrel"), List.of("nether_reed_chest", "stalagnate_chest", "willow_chest", "wart_chest", "warped_chest", "crimson_chest", "rubeus_chest", "mushroom_fir_chest", "nether_mushroom_chest", "anchor_tree_chest", "nether_sakura_chest"), null, null, generator, provider, helper));

    try {
      for (DataGenerator toRun : generators) {
        toRun.run();
        Files.copy(root.resolve("logo.png"), toRun.getPackOutput().getOutputFolder().resolve("pack.png"));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
