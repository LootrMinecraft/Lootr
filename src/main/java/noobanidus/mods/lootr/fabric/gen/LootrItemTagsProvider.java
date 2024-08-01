package noobanidus.mods.lootr.fabric.gen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import noobanidus.mods.lootr.api.LootrTags;
import noobanidus.mods.lootr.fabric.init.ModItems;

import java.util.concurrent.CompletableFuture;

public class LootrItemTagsProvider extends FabricTagProvider.ItemTagProvider {
  public LootrItemTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
    super(output, completableFuture, null);
  }

  @Override
  protected void addTags(HolderLookup.Provider wrapperLookup) {
    getOrCreateTagBuilder(LootrTags.Items.BARRELS).add(ModItems.BARREL);
    getOrCreateTagBuilder(LootrTags.Items.CHESTS).add(ModItems.CHEST, ModItems.INVENTORY);
    getOrCreateTagBuilder(LootrTags.Items.TRAPPED_CHESTS).add(ModItems.TRAPPED_CHEST);
    getOrCreateTagBuilder(LootrTags.Items.SHULKERS).add(ModItems.SHULKER);
    //noinspection unchecked
    getOrCreateTagBuilder(LootrTags.Items.CONTAINERS).addTag(LootrTags.Items.BARRELS).addTag(LootrTags.Items.CHESTS).addTag(LootrTags.Items.TRAPPED_CHESTS).addTag(LootrTags.Items.SHULKERS);
  }

  @Override
  public String getName() {
    return "Lootr Item Tags";
  }
}
