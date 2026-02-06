package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = LootrAPI.MODID)
public class LootrDataGenerators {
  @SubscribeEvent
  public static void gatherDataServer(GatherDataEvent.Client event) {
    DataGenerator generator = event.getGenerator();
    PackOutput output = event.getGenerator().getPackOutput();
    CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();

    generator.addProvider(true, new LootrBlockTagProvider(output, provider));
    generator.addProvider(true, new LootrItemTagsProvider(output, provider));
    generator.addProvider(true, LootrLootTableProvider.create(output, provider));
    generator.addProvider(true, new LootrEntityTagsProvider(output, provider));
    generator.addProvider(true, new LootrBlockEntityTagsProvider(output, provider));
    generator.addProvider(true, new LootrStructureTagsProvider(output, provider));
    generator.addProvider(true, new AdvancementProvider(output, provider, List.of(new LootrAdvancementGenerator())));
    generator.addProvider(true, new LootrAtlasGenerator(output, provider));
    generator.addProvider(true, new LootrLangProvider(output));
    generator.addProvider(true, new LootrParticleProvider(output));
    generator.addProvider(true, new LootrModelProvider(output));

    Path root = output.getOutputFolder().getParent().getParent().getParent().getParent();
    Path datapacks = root.resolve("neoforge").resolve("src").resolve("generated").resolve("resources"); // a hack

    DataGenerator generator2 = new DataGenerator(datapacks, DetectedVersion.tryDetectVersion(), true);
    generator2.addProvider(true,new LootrUnbakedModelProvider(output));
    try {
      generator2.run();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
