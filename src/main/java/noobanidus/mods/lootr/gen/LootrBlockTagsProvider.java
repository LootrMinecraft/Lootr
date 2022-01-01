package noobanidus.mods.lootr.gen;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import noobanidus.mods.lootr.LootrTags;
import noobanidus.mods.lootr.init.ModBlocks;

import javax.annotation.Nullable;

public class LootrBlockTagsProvider extends BlockTagsProvider {
  public LootrBlockTagsProvider(DataGenerator pGenerator, String modId, @Nullable ExistingFileHelper existingFileHelper) {
    super(pGenerator, modId, existingFileHelper);
  }

  @Override
  protected void addTags() {
    tag(LootrTags.Blocks.BARRELS).add(ModBlocks.BARREL);
    tag(LootrTags.Blocks.CHESTS).add(ModBlocks.CHEST, ModBlocks.INVENTORY);
    tag(LootrTags.Blocks.TRAPPED_CHESTS).add(ModBlocks.TRAPPED_CHEST);
    tag(LootrTags.Blocks.SHULKERS).add(ModBlocks.SHULKER);
    //noinspection unchecked
    tag(LootrTags.Blocks.CONTAINERS).addTags(LootrTags.Blocks.BARRELS, LootrTags.Blocks.CHESTS, LootrTags.Blocks.TRAPPED_CHESTS, LootrTags.Blocks.SHULKERS);
  }

  @Override
  public String getName() {
    return "Lootr Block Tags";
  }
}
