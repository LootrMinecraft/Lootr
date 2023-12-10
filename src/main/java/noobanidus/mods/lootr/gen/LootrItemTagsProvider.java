package noobanidus.mods.lootr.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import noobanidus.mods.lootr.LootrTags;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.init.ModItems;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class LootrItemTagsProvider extends ItemTagsProvider {
  public LootrItemTagsProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagsProvider.TagLookup<Block>> p_275322_, @org.jetbrains.annotations.Nullable ExistingFileHelper existingFileHelper) {
    super(p_275343_, p_275729_, p_275322_, LootrAPI.MODID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    tag(LootrTags.Items.BARRELS).add(ModItems.BARREL.get());
    tag(LootrTags.Items.CHESTS).add(ModItems.CHEST.get(), ModItems.INVENTORY.get());
    tag(LootrTags.Items.TRAPPED_CHESTS).add(ModItems.TRAPPED_CHEST.get());
    tag(LootrTags.Items.SHULKERS).add(ModItems.SHULKER.get());
    //noinspection unchecked
    tag(LootrTags.Items.CONTAINERS).addTags(LootrTags.Items.BARRELS, LootrTags.Items.CHESTS, LootrTags.Items.TRAPPED_CHESTS, LootrTags.Items.SHULKERS);
  }

  @Override
  public String getName() {
    return "Lootr Item Tags";
  }
}
