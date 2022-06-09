package noobanidus.mods.lootr.gen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import noobanidus.mods.lootr.LootrTags;
import noobanidus.mods.lootr.init.ModBlocks;
import org.jetbrains.annotations.Nullable;

public class LootrBlockTagProvider extends BlockTagsProvider {
  public LootrBlockTagProvider(DataGenerator p_126511_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
    super(p_126511_, modId, existingFileHelper);
  }

  @Override
  protected void addTags() {
    tag(BlockTags.SHULKER_BOXES).add(ModBlocks.SHULKER.get());
    tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.TROPHY.get());
    tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.CHEST.get(), ModBlocks.TRAPPED_CHEST.get(), ModBlocks.BARREL.get());
    tag(BlockTags.GUARDED_BY_PIGLINS).add(ModBlocks.CHEST.get(), ModBlocks.TRAPPED_CHEST.get(), ModBlocks.BARREL.get());
    tag(Tags.Blocks.CHESTS_WOODEN).add(ModBlocks.CHEST.get());
    tag(Tags.Blocks.CHESTS_TRAPPED).add(ModBlocks.TRAPPED_CHEST.get());
    tag(Tags.Blocks.BARRELS).add(ModBlocks.BARREL.get());

    tag(LootrTags.Blocks.BARRELS).add(ModBlocks.BARREL.get());
    tag(LootrTags.Blocks.CHESTS).add(ModBlocks.CHEST.get(), ModBlocks.INVENTORY.get());
    tag(LootrTags.Blocks.TRAPPED_CHESTS).add(ModBlocks.TRAPPED_CHEST.get());
    tag(LootrTags.Blocks.SHULKERS).add(ModBlocks.SHULKER.get());
    //noinspection unchecked
    tag(LootrTags.Blocks.CONTAINERS).addTags(LootrTags.Blocks.BARRELS, LootrTags.Blocks.CHESTS, LootrTags.Blocks.TRAPPED_CHESTS, LootrTags.Blocks.SHULKERS);
  }

  @Override
  public String getName() {
    return "Lootr Block Tags";
  }
}
