package noobanidus.mods.lootr.gen;

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
    tag(BlockTags.SHULKER_BOXES).add(LootrRegistry.getShulker());
    tag(BlockTags.MINEABLE_WITH_PICKAXE).add(LootrRegistry.getTrophy());
    tag(BlockTags.MINEABLE_WITH_AXE).add(LootrRegistry.getChest(), LootrRegistry.getTrappedChest(), LootrRegistry.getBarrel(), LootrRegistry.getInventory());
    tag(BlockTags.GUARDED_BY_PIGLINS).add(LootrRegistry.getChest(), LootrRegistry.getTrappedChest(), LootrRegistry.getBarrel(), LootrRegistry.getInventory());
    tag(Tags.Blocks.CHESTS_WOODEN).add(LootrRegistry.getChest(), LootrRegistry.getInventory());
    tag(Tags.Blocks.CHESTS_TRAPPED).add(LootrRegistry.getTrappedChest());
    tag(Tags.Blocks.BARRELS).add(LootrRegistry.getBarrel());

    tag(LootrTags.Blocks.BARRELS).add(LootrRegistry.getBarrel());
    tag(LootrTags.Blocks.CHESTS).add(LootrRegistry.getChest(), LootrRegistry.getInventory());
    tag(LootrTags.Blocks.TRAPPED_CHESTS).add(LootrRegistry.getTrappedChest());
    tag(LootrTags.Blocks.SHULKERS).add(LootrRegistry.getShulker());
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
