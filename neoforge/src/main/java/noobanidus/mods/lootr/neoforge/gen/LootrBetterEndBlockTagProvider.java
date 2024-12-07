package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LootrBetterEndBlockTagProvider extends BlockTagsProvider {
  public LootrBetterEndBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, LootrAPI.MODID, existingFileHelper);
  }

  private final List<String> BARREL_BLOCKS = List.of("mossy_glowshroom_barrel","end_lotus_barrel","pythadendron_barrel", "lacugrove_barrel", "dragon_tree_barrel", "tenanea_barrel", "helix_tree_barrel", "umbrella_tree_barrel", "jellyshroom_barrel", "lucernia_barrel");
  private final List<String> CHEST_BLOCKS = List.of("mossy_glowshroom_chest","end_lotus_chest","pythadendron_chest", "lacugrove_chest", "dragon_tree_chest", "tenanea_chest", "helix_tree_chest", "umbrella_tree_chest", "jellyshroom_chest", "lucernia_chest");

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    var tag = tag(LootrTags.Blocks.CONVERT_CHESTS);
    for (String block : CHEST_BLOCKS) {
      tag.add(TagEntry.optionalElement(ResourceLocation.fromNamespaceAndPath("betterend", block)));
    }
    tag = tag(LootrTags.Blocks.CONVERT_BARRELS);
    for (String block : BARREL_BLOCKS) {
      tag.add(TagEntry.optionalElement(ResourceLocation.fromNamespaceAndPath("betterend", block)));
    }
  }

  @Override
  public String getName() {
    return "BetterEnd Compatibility Block Tags";
  }
}
