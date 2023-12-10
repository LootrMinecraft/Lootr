package noobanidus.mods.lootr.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import noobanidus.mods.lootr.LootrTags;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.init.ModBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class LootrBlockTagProvider extends BlockTagsProvider {
  public LootrBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, LootrAPI.MODID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
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
