package noobanidus.mods.lootr.fabric.gen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import noobanidus.mods.lootr.api.LootrTags;
import noobanidus.mods.lootr.fabric.init.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class LootrBlockTagProvider extends FabricTagProvider.BlockTagProvider {
  public LootrBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
    super(output, lookupProvider);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    getOrCreateTagBuilder(BlockTags.SHULKER_BOXES).add(ModBlocks.SHULKER);
    getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.TROPHY);
    getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.CHEST, ModBlocks.TRAPPED_CHEST, ModBlocks.BARREL);
    getOrCreateTagBuilder(BlockTags.GUARDED_BY_PIGLINS).add(ModBlocks.CHEST, ModBlocks.TRAPPED_CHEST, ModBlocks.BARREL);

    getOrCreateTagBuilder(LootrTags.Blocks.BARRELS).add(ModBlocks.BARREL);
    getOrCreateTagBuilder(LootrTags.Blocks.CHESTS).add(ModBlocks.CHEST, ModBlocks.INVENTORY);
    getOrCreateTagBuilder(LootrTags.Blocks.TRAPPED_CHESTS).add(ModBlocks.TRAPPED_CHEST);
    getOrCreateTagBuilder(LootrTags.Blocks.SHULKERS).add(ModBlocks.SHULKER);
    //noinspection unchecked
    getOrCreateTagBuilder(LootrTags.Blocks.CONTAINERS).addTag(LootrTags.Blocks.BARRELS).addTag(LootrTags.Blocks.CHESTS).addTag(LootrTags.Blocks.TRAPPED_CHESTS).addTag(LootrTags.Blocks.SHULKERS);

    getOrCreateTagBuilder(LootrTags.Blocks.CONVERT_BARRELS).add(Blocks.BARREL);
    getOrCreateTagBuilder(LootrTags.Blocks.CONVERT_CHESTS).add(Blocks.CHEST);
    getOrCreateTagBuilder(LootrTags.Blocks.CONVERT_TRAPPED_CHESTS).add(Blocks.TRAPPED_CHEST);
    getOrCreateTagBuilder(LootrTags.Blocks.CONVERT_SHULKERS).add(Blocks.SHULKER_BOX);
    getOrCreateTagBuilder(LootrTags.Blocks.CONVERT_BLOCK).addTag(LootrTags.Blocks.CONVERT_BARRELS).addTag(LootrTags.Blocks.CONVERT_CHESTS).addTag(LootrTags.Blocks.CONVERT_TRAPPED_CHESTS).addTag(LootrTags.Blocks.CONVERT_SHULKERS);
  }

  @Override
  public String getName() {
    return "Lootr Block Tags";
  }
}