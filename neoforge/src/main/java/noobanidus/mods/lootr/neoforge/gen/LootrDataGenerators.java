package noobanidus.mods.lootr.neoforge.gen;

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

    generator.addProvider(true, new PackMetadataGenerator(generator.getPackOutput()).add(PackMetadataSection.CLIENT_TYPE, new PackMetadataSection(Component.literal("Lootr Assets"), SharedConstants.getCurrentVersion()
        .packVersion(PackType.CLIENT_RESOURCES).minorRange())));
  }
}
