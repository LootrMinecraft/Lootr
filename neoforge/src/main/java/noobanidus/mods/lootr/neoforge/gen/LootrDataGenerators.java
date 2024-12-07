package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.DetectedVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = LootrAPI.MODID, bus = EventBusSubscriber.Bus.MOD)
public class LootrDataGenerators {
  @SubscribeEvent
  public static void gatherData(GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    PackOutput output = event.getGenerator().getPackOutput();
    CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
    ExistingFileHelper helper = event.getExistingFileHelper();

    LootrBlockTagProvider blocks;
    generator.addProvider(event.includeServer(), blocks = new LootrBlockTagProvider(output, provider, helper));
    generator.addProvider(event.includeServer(), new LootrItemTagsProvider(output, provider, blocks.contentsGetter(), helper));
    generator.addProvider(event.includeClient(), new LootrAtlasGenerator(output, provider, helper));
    generator.addProvider(true, LootrLootTableProvider.create(output, provider));
    generator.addProvider(event.includeServer(), new LootrEntityTagsProvider(output, provider, helper));
    generator.addProvider(event.includeServer(), new LootrBlockEntityTagsProvider(output, provider, helper));
    generator.addProvider(event.includeServer(), new LootrStructureTagsProvider(output, provider, helper));
    generator.addProvider(event.includeClient(), new LootrLangProvider(output));
    generator.addProvider(event.includeServer(), new AdvancementProvider(output, provider, helper, List.of(new LootrAdvancementGenerator())));

    Path datapacks = generator.getPackOutput().getOutputFolder().getParent().getParent().getParent().getParent().resolve("datapacks"); // a hack

    // Data pack generation
    DataGenerator betterEndGenerator = new DataGenerator(datapacks.resolve("betterend"), DetectedVersion.tryDetectVersion(), true);
    betterEndGenerator.addProvider(event.includeServer(), new LootrBetterEndBlockTagProvider(betterEndGenerator.getPackOutput(), provider, helper));
    betterEndGenerator.addProvider(event.includeServer(),
    new PackMetadataGenerator(betterEndGenerator.getPackOutput()).add(PackMetadataSection.TYPE, new PackMetadataSection(Component.literal("Lootr Compatibility for BetterEnd"),15, Optional.empty())));

    DataGenerator betterNetherGenerator = new DataGenerator(datapacks.resolve("betternether"), DetectedVersion.tryDetectVersion(), true);
    betterNetherGenerator.addProvider(event.includeServer(), new LootrBetterNetherBlockTagProvider(betterNetherGenerator.getPackOutput(), provider, helper));
    betterNetherGenerator.addProvider(event.includeServer(),
        new PackMetadataGenerator(betterNetherGenerator.getPackOutput()).add(PackMetadataSection.TYPE, new PackMetadataSection(Component.literal("Lootr Compatibility for BetterNether"),15, Optional.empty())));
    try {
      betterEndGenerator.run();
      betterNetherGenerator.run();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
