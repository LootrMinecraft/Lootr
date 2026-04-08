package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class LootrBlockTagProvider extends BlockTagsProvider {
  public LootrBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
    super(output, lookupProvider, LootrAPI.MODID);
  }

  @Override
  protected void addTags(HolderLookup.@NonNull Provider provider) {
    tag(BlockTags.SAND).add(LootrRegistry.getSuspiciousSandBlock());
    tag(BlockTags.SHULKER_BOXES).add(LootrRegistry.getShulkerBoxBlock());
    tag(BlockTags.MINEABLE_WITH_PICKAXE).add(LootrRegistry.getTrophyBlock(), LootrRegistry.getDecoratedPotBlock());
    tag(BlockTags.MINEABLE_WITH_AXE).add(LootrRegistry.getChestBlock(), LootrRegistry.getTrappedChestBlock(), LootrRegistry.getBarrelBlock());
    tag(BlockTags.MINEABLE_WITH_SHOVEL).add(LootrRegistry.getSuspiciousGravelBlock(), LootrRegistry.getSuspiciousSandBlock());
    tag(BlockTags.GUARDED_BY_PIGLINS).add(LootrRegistry.getChestBlock(), LootrRegistry.getTrappedChestBlock(), LootrRegistry.getBarrelBlock());
    tag(Tags.Blocks.CHESTS_WOODEN).add(LootrRegistry.getChestBlock());
    tag(Tags.Blocks.CHESTS_TRAPPED).add(LootrRegistry.getTrappedChestBlock());
    tag(Tags.Blocks.BARRELS).add(LootrRegistry.getBarrelBlock());
    tag(LootrTags.Blocks.NON_BLOCKING); //.add(Blocks.SPAWNER);

    tag(LootrTags.Blocks.BARRELS).add(LootrRegistry.getBarrelBlock());
    tag(LootrTags.Blocks.CHESTS).add(LootrRegistry.getChestBlock());
    tag(LootrTags.Blocks.TRAPPED_CHESTS).add(LootrRegistry.getTrappedChestBlock());
    tag(LootrTags.Blocks.SHULKERS).add(LootrRegistry.getShulkerBoxBlock());
    tag(LootrTags.Blocks.SANDS).add(LootrRegistry.getSuspiciousSandBlock());
    tag(LootrTags.Blocks.GRAVELS).add(LootrRegistry.getSuspiciousGravelBlock());
    tag(LootrTags.Blocks.POTS).add(LootrRegistry.getDecoratedPotBlock());
    //noinspection unchecked
    tag(LootrTags.Blocks.CONTAINERS).addTags(LootrTags.Blocks.BARRELS, LootrTags.Blocks.CHESTS, LootrTags.Blocks.TRAPPED_CHESTS, LootrTags.Blocks.SHULKERS, LootrTags.Blocks.SANDS, LootrTags.Blocks.GRAVELS, LootrTags.Blocks.POTS);

    tag(LootrTags.Blocks.CONVERT_BARRELS).add(Blocks.BARREL).addTag(Tags.Blocks.BARRELS);
    tag(LootrTags.Blocks.CONVERT_CHESTS).add(Blocks.CHEST).addTag(Tags.Blocks.CHESTS_WOODEN);
    tag(LootrTags.Blocks.CONVERT_TRAPPED_CHESTS).add(Blocks.TRAPPED_CHEST).addTag(Tags.Blocks.CHESTS_TRAPPED);
    tag(LootrTags.Blocks.CONVERT_SHULKERS).add(Blocks.SHULKER_BOX);
    tag(LootrTags.Blocks.CONVERT_SANDS).add(Blocks.SUSPICIOUS_SAND);
    tag(LootrTags.Blocks.CONVERT_GRAVELS).add(Blocks.SUSPICIOUS_GRAVEL);
    tag(LootrTags.Blocks.CONVERT_POTS).add(Blocks.DECORATED_POT);
    //noinspection unchecked
    tag(LootrTags.Blocks.CONVERT_BLOCK).addTags(LootrTags.Blocks.CONVERT_BARRELS, LootrTags.Blocks.CONVERT_CHESTS, LootrTags.Blocks.CONVERT_TRAPPED_CHESTS, LootrTags.Blocks.CONVERT_SHULKERS, LootrTags.Blocks.CONVERT_SANDS, LootrTags.Blocks.CONVERT_GRAVELS, LootrTags.Blocks.CONVERT_POTS);

    tag(LootrTags.Blocks.CONVERT_BLACKLIST);

    tag(LootrTags.Blocks.CUSTOM_ELIGIBLE).add(Blocks.BARREL, Blocks.CHEST);

    //noinspection unchecked
    tag(LootrTags.Blocks.INTERACT_WHITELIST_BLOCKS).addTags(LootrTags.Blocks.BARRELS, LootrTags.Blocks.CHESTS, LootrTags.Blocks.TRAPPED_CHESTS, LootrTags.Blocks.SHULKERS, LootrTags.Blocks.SANDS, LootrTags.Blocks.GRAVELS, LootrTags.Blocks.POTS);

    tag(LootrTags.Blocks.INTERACT_WHITELIST).addTag(LootrTags.Blocks.INTERACT_WHITELIST_BLOCKS);
  }

  @Override
  public @NonNull String getName() {
    return "Lootr Block Tags";
  }
}
