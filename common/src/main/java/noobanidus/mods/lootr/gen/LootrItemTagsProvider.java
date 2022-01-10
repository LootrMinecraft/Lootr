package noobanidus.mods.lootr.gen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import noobanidus.mods.lootr.LootrTags;
import noobanidus.mods.lootr.init.ModItems;

import javax.annotation.Nullable;

public class LootrItemTagsProvider extends ItemTagsProvider {
  public LootrItemTagsProvider(DataGenerator pGenerator, BlockTagsProvider pBlockTagsProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
    super(pGenerator, pBlockTagsProvider, modId, existingFileHelper);
  }

  @Override
  protected void addTags() {
    tag(LootrTags.Items.BARRELS).add(ModItems.BARREL);
    tag(LootrTags.Items.CHESTS).add(ModItems.CHEST, ModItems.INVENTORY);
    tag(LootrTags.Items.TRAPPED_CHESTS).add(ModItems.TRAPPED_CHEST);
    tag(LootrTags.Items.SHULKERS).add(ModItems.SHULKER);
    //noinspection unchecked
    tag(LootrTags.Items.CONTAINERS).addTags(LootrTags.Items.BARRELS, LootrTags.Items.CHESTS, LootrTags.Items.TRAPPED_CHESTS, LootrTags.Items.SHULKERS);
  }

  @Override
  public String getName() {
    return "Lootr Item Tags";
  }
}
