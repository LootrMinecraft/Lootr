package noobanidus.mods.lootr.neoforge.gen.compat;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
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

public class LootrCompatBlockTagProvider extends BlockTagsProvider {
  private final List<String> BARREL_BLOCK_IDS;
  private final List<String> CHEST_BLOCK_IDS;
  private final List<String> TRAPPED_CHEST_BLOCK_IDS;
  private final List<String> SHULKER_BOX_BLOCK_IDS;
  private final String COMPAT_MODID;

  public LootrCompatBlockTagProvider(String compatModid, @Nullable List<String> barrelBlockIds, @Nullable List<String> chestBlockIds, @Nullable List<String> trappedBlockIds, @Nullable List<String> shulkerBoxIds, DataGenerator generator, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
    super(generator.getPackOutput(), lookupProvider, LootrAPI.MODID, existingFileHelper);
    this.COMPAT_MODID = compatModid;
    this.BARREL_BLOCK_IDS = barrelBlockIds == null ? List.of() : barrelBlockIds;
    this.CHEST_BLOCK_IDS = chestBlockIds == null ? List.of() : chestBlockIds;
    this.SHULKER_BOX_BLOCK_IDS = shulkerBoxIds == null ? List.of() : shulkerBoxIds;
    this.TRAPPED_CHEST_BLOCK_IDS = trappedBlockIds == null ? List.of() : trappedBlockIds;
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    if (!CHEST_BLOCK_IDS.isEmpty()) {
      var tag = tag(LootrTags.Blocks.CONVERT_CHESTS);
      for (String block : CHEST_BLOCK_IDS) {
        tag.add(TagEntry.optionalElement(ResourceLocation.fromNamespaceAndPath(COMPAT_MODID, block)));
      }
    }
    if (!BARREL_BLOCK_IDS.isEmpty()) {
      var tag = tag(LootrTags.Blocks.CONVERT_BARRELS);
      for (String block : BARREL_BLOCK_IDS) {
        tag.add(TagEntry.optionalElement(ResourceLocation.fromNamespaceAndPath(COMPAT_MODID, block)));
      }
    }
    if (!TRAPPED_CHEST_BLOCK_IDS.isEmpty()) {
      var tag = tag(LootrTags.Blocks.CONVERT_TRAPPED_CHESTS);
      for (String block : TRAPPED_CHEST_BLOCK_IDS) {
        tag.add(TagEntry.optionalElement(ResourceLocation.fromNamespaceAndPath(COMPAT_MODID, block)));
      }
    }
    if (!SHULKER_BOX_BLOCK_IDS.isEmpty()) {
      var tag = tag(LootrTags.Blocks.CONVERT_SHULKERS);
      for (String block : SHULKER_BOX_BLOCK_IDS) {
        tag.add(TagEntry.optionalElement(ResourceLocation.fromNamespaceAndPath(COMPAT_MODID, block)));
      }
    }
  }

  @Override
  public String getName() {
    return COMPAT_MODID + " Compatibility Block Tags";
  }
}
