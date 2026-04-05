package noobanidus.mods.lootr.neoforge.gen.optionals;

import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.criterion.ImpossibleTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@EventBusSubscriber(modid = LootrAPI.MODID)
public class LootrNoAdvancementGenerator {
  private static final List<DataGenerator> generators = new ArrayList<>();

  private static DataGenerator makeGenerator (Path path, Component description) {
    DataGenerator generator = new DataGenerator.Cached(path, DetectedVersion.tryDetectVersion(), true);
    generator.addProvider(true, new PackMetadataGenerator(generator.getPackOutput()).add(PackMetadataSection.SERVER_TYPE, new PackMetadataSection(description, SharedConstants.getCurrentVersion().packVersion(PackType.SERVER_DATA).minorRange())));
    generators.add(generator);
    return generator;
  }

  @SubscribeEvent
  public static void gatherData(GatherDataEvent.Client event) {
    PackOutput output = event.getGenerator().getPackOutput();
    CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();

    Path root = output.getOutputFolder().getParent().getParent().getParent().getParent();

    Path datapacks = root.resolve("datapacks"); // a hack

    // Data pack generation
    var generator = makeGenerator(datapacks.resolve("lootr_no_advancements"), Component.literal("Disable Lootr Advancements"));
    generator.addProvider(true, new AdvancementProvider(generator.getPackOutput(), provider, List.of(new LootrAdvancementGenerator())));

    generator = makeGenerator(datapacks.resolve("lootr_no_suspicious_blocks"), Component.literal("Disable Lootr Suspicious Sand and Gravel"));
    generator.addProvider(true, new LootrNoSuspiciousGenerator.LootrBlockTagProvider(generator.getPackOutput(), provider));

    try {
      for (DataGenerator toRun : generators) {
        toRun.run();
        Files.copy(root.resolve("logo.png"), toRun.getPackOutput().getOutputFolder().resolve("pack.png"));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static class LootrAdvancementGenerator implements AdvancementSubProvider {
    @Override
    public void generate(HolderLookup.@NonNull Provider arg, @NonNull Consumer<AdvancementHolder> consumer) {
      var impossible = CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance());

      AdvancementHolder lootrRoot = Advancement.Builder.advancement().addCriterion("impossible", impossible)
          .save(consumer, LootrAPI.rl("root"));
      AdvancementHolder one_barrel = Advancement.Builder.advancement().addCriterion("impossible", impossible)
          .save(consumer, LootrAPI.rl("1barrel"));
      AdvancementHolder one_cart = Advancement.Builder.advancement().addCriterion("impossible", impossible)
          .save(consumer, LootrAPI.rl("1cart"));
      AdvancementHolder one_chest = Advancement.Builder.advancement().addCriterion("impossible", impossible)
          .save(consumer, LootrAPI.rl("1chest"));
      AdvancementHolder one_shulker = Advancement.Builder.advancement().addCriterion("impossible", impossible)
          .save(consumer, LootrAPI.rl("1shulker"));
      AdvancementHolder brush = Advancement.Builder.advancement().addCriterion("impossible", impossible)
          .save(consumer, LootrAPI.rl("all_gravel"));
      AdvancementHolder pot = Advancement.Builder.advancement().addCriterion("impossible", impossible)
          .save(consumer, LootrAPI.rl("pot_opened"));
      AdvancementHolder archaeologist = Advancement.Builder.advancement().addCriterion("impossible", impossible)
          .save(consumer, LootrAPI.rl("archaeologist"));
      AdvancementHolder item_frame = Advancement.Builder.advancement().addCriterion("impossible", impossible)
          .save(consumer, LootrAPI.rl("1frame"));
      AdvancementHolder ten_loot = Advancement.Builder.advancement().addCriterion("impossible", impossible)
          .save(consumer, LootrAPI.rl("10loot"));
      AdvancementHolder twentyfive_loot = Advancement.Builder.advancement().addCriterion("impossible", impossible)
          .save(consumer, LootrAPI.rl("25loot"));
      AdvancementHolder fifty_loot = Advancement.Builder.advancement().addCriterion("impossible", impossible)
          .save(consumer, LootrAPI.rl("50loot"));
      Advancement.Builder.advancement().parent(fifty_loot).addCriterion("impossible", impossible)
          .save(consumer, LootrAPI.rl("100loot"));
      Advancement.Builder.advancement().parent(one_chest).addCriterion("impossible", impossible)
          .save(consumer, LootrAPI.rl("social"));
    }
  }
}
