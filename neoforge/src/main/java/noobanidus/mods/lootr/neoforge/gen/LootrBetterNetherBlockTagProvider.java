package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LootrBetterNetherBlockTagProvider extends BlockTagsProvider {
  public LootrBetterNetherBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, LootrAPI.MODID, existingFileHelper);
  }

  private final List<String> BARREL_BLOCKS = List.of("nether_reed_barrel", "stalagnate_barrel", "willow_barrel", "wart_barrel", "warped_barrel", "crimson_barrel", "rubeus_barrel", "mushroom_fir_barrel", "nether_mushroom_barrel", "anchor_tree_barrel", "nether_sakura_barrel");
  private final List<String> CHEST_BLOCKS = List.of("nether_reed_chest", "stalagnate_chest", "willow_chest", "wart_chest", "warped_chest", "crimson_chest", "rubeus_chest", "mushroom_fir_chest", "nether_mushroom_chest", "anchor_tree_chest", "nether_sakura_chest");

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    var tag = tag(LootrTags.Blocks.CONVERT_CHESTS);
    for (String block : CHEST_BLOCKS) {
      tag.add(TagEntry.optionalElement(ResourceLocation.fromNamespaceAndPath("betternether", block)));
    }
    tag = tag(LootrTags.Blocks.CONVERT_BARRELS);
    for (String block : BARREL_BLOCKS) {
      tag.add(TagEntry.optionalElement(ResourceLocation.fromNamespaceAndPath("betternether", block)));
    }
  }

  @Override
  public String getName() {
    return "BetterNether Compatibility Block Tags";
  }
}
