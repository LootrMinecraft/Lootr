package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
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
    ExistingFileHelper helper = event.getExistingFileHelper();

    generator.addProvider(true, new LootrAtlasGenerator(output, provider, helper));
    generator.addProvider(true, new LootrLangProvider(output));
  }

  @SubscribeEvent
  public static void gatherServerData(GatherDataEvent.Server event) {
    DataGenerator generator = event.getGenerator();
    PackOutput output = event.getGenerator().getPackOutput();
    CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
    ExistingFileHelper helper = event.getExistingFileHelper();

    LootrBlockTagProvider blocks;
    generator.addProvider(true, blocks = new LootrBlockTagProvider(output, provider, helper));
    generator.addProvider(true, new LootrItemTagsProvider(output, provider, blocks.contentsGetter(), helper));
    generator.addProvider(true, LootrLootTableProvider.create(output, provider));
    generator.addProvider(true, new LootrEntityTagsProvider(output, provider, helper));
    generator.addProvider(true, new LootrBlockEntityTagsProvider(output, provider, helper));
    generator.addProvider(true, new LootrStructureTagsProvider(output, provider, helper));
    generator.addProvider(true, new AdvancementProvider(output, provider, helper, List.of(new LootrAdvancementGenerator())));
  }
}
