package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.references.BlockItemIds;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.api.LootrTags;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unchecked")
public class LootrBlockTagProvider extends BlockTagsProvider {
  public LootrBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
    super(output, lookupProvider, LootrAPI.MODID);
  }

  @Override
  protected void addTags(HolderLookup.@NonNull Provider provider) {
    tag(BlockTags.SAND).add(LootrConstants.LootrBlockIds.SUSPICIOUS_SAND);
    tag(BlockTags.SHULKER_BOXES).add(LootrConstants.LootrBlockIds.SHULKER_BOX);
    tag(BlockTags.MINEABLE_WITH_PICKAXE).add(LootrConstants.LootrBlockIds.TROPHY, LootrConstants.LootrBlockIds.DECORATED_POT);
    tag(BlockTags.MINEABLE_WITH_AXE).add(LootrConstants.LootrBlockIds.CHEST, LootrConstants.LootrBlockIds.TRAPPED_CHEST, LootrConstants.LootrBlockIds.BARREL);
    tag(BlockTags.MINEABLE_WITH_SHOVEL).add(LootrConstants.LootrBlockIds.SUSPICIOUS_SAND, LootrConstants.LootrBlockIds.SUSPICIOUS_GRAVEL);
    tag(BlockTags.GUARDED_BY_PIGLINS).add(LootrConstants.LootrBlockIds.CHEST, LootrConstants.LootrBlockIds.TRAPPED_CHEST, LootrConstants.LootrBlockIds.BARREL);
    tag(Tags.Blocks.CHESTS_WOODEN).add(LootrConstants.LootrBlockIds.CHEST);
    tag(Tags.Blocks.CHESTS_TRAPPED).add(LootrConstants.LootrBlockIds.TRAPPED_CHEST);
    tag(Tags.Blocks.BARRELS).add(LootrConstants.LootrBlockIds.BARREL);
    tag(LootrTags.Blocks.NON_BLOCKING); //.add(Blocks.SPAWNER);

    tag(LootrTags.Blocks.BARRELS).add(LootrConstants.LootrBlockIds.BARREL);
    tag(LootrTags.Blocks.CHESTS).add(LootrConstants.LootrBlockIds.CHEST);
    tag(LootrTags.Blocks.TRAPPED_CHESTS).add(LootrConstants.LootrBlockIds.TRAPPED_CHEST);
    tag(LootrTags.Blocks.SHULKERS).add(LootrConstants.LootrBlockIds.SHULKER_BOX);
    tag(LootrTags.Blocks.SANDS).add(LootrConstants.LootrBlockIds.SUSPICIOUS_SAND);
    tag(LootrTags.Blocks.GRAVELS).add(LootrConstants.LootrBlockIds.SUSPICIOUS_GRAVEL);
    tag(LootrTags.Blocks.POTS).add(LootrConstants.LootrBlockIds.DECORATED_POT);

    tag(LootrTags.Blocks.PREVENT_BREAK_BARRELS).addTag(LootrTags.Blocks.BARRELS);
    tag(LootrTags.Blocks.PREVENT_BREAK_CHESTS).addTag(LootrTags.Blocks.CHESTS);
    tag(LootrTags.Blocks.PREVENT_BREAK_TRAPPED_CHESTS).addTag(LootrTags.Blocks.TRAPPED_CHESTS);
    tag(LootrTags.Blocks.PREVENT_BREAK_SHULKERS).addTag(LootrTags.Blocks.SHULKERS);
    tag(LootrTags.Blocks.PREVENT_BREAK_SANDS).addTag(LootrTags.Blocks.SANDS);
    tag(LootrTags.Blocks.PREVENT_BREAK_GRAVELS).addTag(LootrTags.Blocks.GRAVELS);
    tag(LootrTags.Blocks.PREVENT_BREAK_POTS).addTag(LootrTags.Blocks.POTS);

    tag(LootrTags.Blocks.ENABLE_BREAK);


    //noinspection unchecked
    tag(LootrTags.Blocks.PREVENT_BREAK).addTags(LootrTags.Blocks.PREVENT_BREAK_BARRELS, LootrTags.Blocks.PREVENT_BREAK_CHESTS, LootrTags.Blocks.PREVENT_BREAK_TRAPPED_CHESTS, LootrTags.Blocks.PREVENT_BREAK_SHULKERS, LootrTags.Blocks.PREVENT_BREAK_SANDS, LootrTags.Blocks.PREVENT_BREAK_GRAVELS, LootrTags.Blocks.PREVENT_BREAK_POTS);

    //noinspection unchecked
    tag(LootrTags.Blocks.CONTAINERS).addTags(LootrTags.Blocks.BARRELS, LootrTags.Blocks.CHESTS, LootrTags.Blocks.TRAPPED_CHESTS, LootrTags.Blocks.SHULKERS, LootrTags.Blocks.SANDS, LootrTags.Blocks.GRAVELS, LootrTags.Blocks.POTS);

    tag(LootrTags.Blocks.CONVERT_BARRELS).add(BlockItemIds.BARREL.block()).addTag(Tags.Blocks.BARRELS);
    tag(LootrTags.Blocks.CONVERT_CHESTS).add(BlockItemIds.CHEST.block()).addTag(Tags.Blocks.CHESTS_WOODEN);
    tag(LootrTags.Blocks.CONVERT_TRAPPED_CHESTS).add(BlockItemIds.TRAPPED_CHEST.block())
        .addTag(Tags.Blocks.CHESTS_TRAPPED);
    tag(LootrTags.Blocks.CONVERT_SHULKERS).add(BlockItemIds.SHULKER_BOX.block());
    tag(LootrTags.Blocks.CONVERT_SANDS).add(BlockItemIds.SUSPICIOUS_SAND.block());
    tag(LootrTags.Blocks.CONVERT_GRAVELS).add(BlockItemIds.SUSPICIOUS_GRAVEL.block());
    tag(LootrTags.Blocks.CONVERT_POTS).add(BlockItemIds.DECORATED_POT.block());
    //noinspection unchecked
    tag(LootrTags.Blocks.CONVERT_BLOCK).addTags(LootrTags.Blocks.CONVERT_BARRELS, LootrTags.Blocks.CONVERT_CHESTS, LootrTags.Blocks.CONVERT_TRAPPED_CHESTS, LootrTags.Blocks.CONVERT_SHULKERS, LootrTags.Blocks.CONVERT_SANDS, LootrTags.Blocks.CONVERT_GRAVELS, LootrTags.Blocks.CONVERT_POTS);

    tag(LootrTags.Blocks.CONVERT_BLACKLIST);

    tag(LootrTags.Blocks.CUSTOM_ELIGIBLE).add(BlockItemIds.BARREL.block(), BlockItemIds.CHEST.block());

    //noinspection unchecked
    tag(LootrTags.Blocks.INTERACT_WHITELIST_BLOCKS).addTags(LootrTags.Blocks.BARRELS, LootrTags.Blocks.CHESTS, LootrTags.Blocks.TRAPPED_CHESTS, LootrTags.Blocks.SHULKERS, LootrTags.Blocks.SANDS, LootrTags.Blocks.GRAVELS, LootrTags.Blocks.POTS);

    tag(LootrTags.Blocks.INTERACT_WHITELIST).addTag(LootrTags.Blocks.INTERACT_WHITELIST_BLOCKS);
  }

  @Override
  public @NonNull String getName() {
    return "Lootr Block Tags";
  }
}
