package noobanidus.mods.lootr.neoforge.gen.optionals;

import net.minecraft.DetectedVersion;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@EventBusSubscriber(modid = LootrAPI.MODID)
public class LootrNoSuspiciousGenerator {
  private static final List<DataGenerator> generators = new ArrayList<>();

  @SubscribeEvent
  public static void gatherData(GatherDataEvent event) {
    if (!event.getMods().contains(LootrAPI.MODID)) {
      return;
    }
    PackOutput output = event.getGenerator().getPackOutput();
    CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
    ExistingFileHelper helper = event.getExistingFileHelper();

    Path root = output.getOutputFolder().getParent().getParent().getParent().getParent();

    Path datapacks = root.resolve("datapacks"); // a hack

    // Data pack generation
    var generator = LootrNoAdvancementGenerator.makeGenerator(datapacks.resolve("lootr_no_suspicious_blocks"), Component.literal("Disable Lootr Suspicious Sand and Gravel"));
    generator.addProvider(event.includeServer(), new LootrBlockTagProvider(generator.getPackOutput(), provider, helper));

    try {
      for (DataGenerator toRun : generators) {
        toRun.run();
        Files.copy(root.resolve("logo.png"), toRun.getPackOutput().getOutputFolder().resolve("pack.png"));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static class LootrBlockTagProvider extends BlockTagsProvider {
    public LootrBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
      super(output, lookupProvider, LootrAPI.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
      tag(LootrTags.Blocks.CONVERT_SANDS).replace();
      tag(LootrTags.Blocks.CONVERT_GRAVELS).replace();
    }

    @Override
    public String getName() {
      return "Lootr Block Tags";
    }
  }

}
