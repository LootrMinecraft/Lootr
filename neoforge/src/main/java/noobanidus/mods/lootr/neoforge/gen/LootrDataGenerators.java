package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;

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
  }
}
