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

@EventBusSubscriber(modid = LootrAPI.MODID, bus = EventBusSubscriber.Bus.MOD)
public class LootrDataGenerators {
  @SubscribeEvent
  public static void gatherData(GatherDataEvent.Client event) {
    DataGenerator generator = event.getGenerator();
    PackOutput output = event.getGenerator().getPackOutput();
    CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();

    generator.addProvider(true, new LootrAtlasGenerator(output, provider));
    generator.addProvider(true, new LootrLangProvider(output));

    LootrBlockTagProvider blocks;
    generator.addProvider(true, blocks = new LootrBlockTagProvider(output, provider));
    generator.addProvider(true, new LootrItemTagsProvider(output, provider, blocks.contentsGetter()));
    generator.addProvider(true, LootrLootTableProvider.create(output, provider));
    generator.addProvider(true, new LootrEntityTagsProvider(output, provider));
    generator.addProvider(true, new LootrBlockEntityTagsProvider(output, provider));
    generator.addProvider(true, new LootrStructureTagsProvider(output, provider));
    generator.addProvider(true, new AdvancementProvider(output, provider, List.of(new LootrAdvancementGenerator())));
  }
}
