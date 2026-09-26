package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = LootrAPI.MODID)
public class LootrDataGenerators {
  @SubscribeEvent
  public static void gatherDataServer(GatherDataEvent.Client event) {
    DataGenerator generator = event.getGenerator();
    PackOutput output = event.getGenerator().getPackOutput();
    CompletableFuture<HolderLookup.Provider> provider = event.getReloadableLookupProvider();

    generator.addProvider(true, new LootrBlockTagProvider(output, provider));
    generator.addProvider(true, new LootrItemTagsProvider(output, provider));


    generator.addProvider(true, DatapackBuiltinEntriesProvider.forReloadableLayer(output, "Lootr Data Packs", event.getWorldLookupProvider(), event.getReloadableLookupProvider(), new RegistrySetBuilder().add(Registries.LOOT_TABLE, LootrLootTableProvider.create())
        .add(Registries.ADVANCEMENT, new AdvancementProvider(List.of(LootrAdvancementGenerator::new))), Set.of("lootr")));
    generator.addProvider(true, new LootrEntityTagsProvider(output, provider));
    generator.addProvider(true, new LootrBlockEntityTagsProvider(output, provider));
    generator.addProvider(true, new LootrStructureTagsProvider(output, provider));
    ;
    generator.addProvider(true, new LootrAtlasGenerator(output, provider));
    generator.addProvider(true, new LootrLangProvider(output));
    generator.addProvider(true, new LootrParticleProvider(output));
    generator.addProvider(true, new LootrModelProvider(output));
  }
}
