package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import noobanidus.mods.lootr.LootrTags;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.registry.LootrRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class LootrBlockTagProvider extends BlockTagsProvider {
  public LootrBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, LootrAPI.MODID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    tag(BlockTags.SHULKER_BOXES).add(LootrRegistry.getShulkerBlock());
    tag(BlockTags.MINEABLE_WITH_PICKAXE).add(LootrRegistry.getTrophyBlock());
    tag(BlockTags.MINEABLE_WITH_AXE).add(LootrRegistry.getChestBlock(), LootrRegistry.getTrappedChestBlock(), LootrRegistry.getBarrelBlock(), LootrRegistry.getInventoryBlock());
    tag(BlockTags.GUARDED_BY_PIGLINS).add(LootrRegistry.getChestBlock(), LootrRegistry.getTrappedChestBlock(), LootrRegistry.getBarrelBlock(), LootrRegistry.getInventoryBlock());
    tag(Tags.Blocks.CHESTS_WOODEN).add(LootrRegistry.getChestBlock(), LootrRegistry.getInventoryBlock());
    tag(Tags.Blocks.CHESTS_TRAPPED).add(LootrRegistry.getTrappedChestBlock());
    tag(Tags.Blocks.BARRELS).add(LootrRegistry.getBarrelBlock());

    tag(LootrTags.Blocks.BARRELS).add(LootrRegistry.getBarrelBlock());
    tag(LootrTags.Blocks.CHESTS).add(LootrRegistry.getChestBlock(), LootrRegistry.getInventoryBlock());
    tag(LootrTags.Blocks.TRAPPED_CHESTS).add(LootrRegistry.getTrappedChestBlock());
    tag(LootrTags.Blocks.SHULKERS).add(LootrRegistry.getShulkerBlock());
    //noinspection unchecked
    tag(LootrTags.Blocks.CONTAINERS).addTags(LootrTags.Blocks.BARRELS, LootrTags.Blocks.CHESTS, LootrTags.Blocks.TRAPPED_CHESTS, LootrTags.Blocks.SHULKERS);

    tag(LootrTags.Blocks.CONVERT_BARRELS).add(Blocks.BARREL).addTag(Tags.Blocks.BARRELS);
    tag(LootrTags.Blocks.CONVERT_CHESTS).add(Blocks.CHEST).addTag(Tags.Blocks.CHESTS_WOODEN);
    tag(LootrTags.Blocks.CONVERT_TRAPPED_CHESTS).add(Blocks.TRAPPED_CHEST).addTag(Tags.Blocks.CHESTS_TRAPPED);
    tag(LootrTags.Blocks.CONVERT_SHULKERS).add(Blocks.SHULKER_BOX);
    //noinspection unchecked
    tag(LootrTags.Blocks.CONVERT_BLOCK).addTags(LootrTags.Blocks.CONVERT_BARRELS, LootrTags.Blocks.CONVERT_CHESTS, LootrTags.Blocks.CONVERT_TRAPPED_CHESTS, LootrTags.Blocks.CONVERT_SHULKERS);

    tag(LootrTags.Blocks.CONVERT_BLACKLIST);
  }

  @Override
  public String getName() {
    return "Lootr Block Tags";
  }
}
