package noobanidus.mods.lootr.gen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import noobanidus.mods.lootr.init.ModBlocks;
import org.jetbrains.annotations.Nullable;

public class LootrBlockTagGenerator extends BlockTagsProvider {
  public LootrBlockTagGenerator(DataGenerator p_126511_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
    super(p_126511_, modId, existingFileHelper);
  }

  @Override
  protected void addTags() {
    this.tag(BlockTags.SHULKER_BOXES).add(ModBlocks.SHULKER);
    this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.TROPHY);
    this.tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.CHEST, ModBlocks.TRAPPED_CHEST, ModBlocks.BARREL);
    this.tag(BlockTags.GUARDED_BY_PIGLINS).add(ModBlocks.CHEST, ModBlocks.TRAPPED_CHEST, ModBlocks.BARREL);
    this.tag(Tags.Blocks.CHESTS_WOODEN).add(ModBlocks.CHEST);
    this.tag(Tags.Blocks.CHESTS_TRAPPED).add(ModBlocks.TRAPPED_CHEST);
    this.tag(Tags.Blocks.BARRELS).add(ModBlocks.BARREL);
  }

  @Override
  public String getName() {
    return "Lootr Block Tags";
  }
}
