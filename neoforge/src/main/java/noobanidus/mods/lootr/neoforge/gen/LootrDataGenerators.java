package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = LootrAPI.MODID)
public class LootrDataGenerators {
  @SubscribeEvent
  public static void gatherData(GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    PackOutput output = event.getGenerator().getPackOutput();
    CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();

    generator.addProvider(event.includeDev(), new LootrBlockTagProvider(output, provider));
    generator.addProvider(event.includeDev(), new LootrItemTagsProvider(output, provider));
    generator.addProvider(event.includeDev(), new LootrAtlasGenerator(output, provider));
    generator.addProvider(true, LootrLootTableProvider.create(output, provider));
    generator.addProvider(event.includeDev(), new LootrEntityTagsProvider(output, provider));
    generator.addProvider(event.includeDev(), new LootrBlockEntityTagsProvider(output, provider));
    generator.addProvider(event.includeDev(), new LootrStructureTagsProvider(output, provider));
    generator.addProvider(event.includeDev(), new LootrLangProvider(output));
    generator.addProvider(event.includeDev(), new AdvancementProvider(output, provider, List.of(new LootrAdvancementGenerator())));
    generator.addProvider(event.includeDev(), new LootrParticleProvider(output));
  }
}
