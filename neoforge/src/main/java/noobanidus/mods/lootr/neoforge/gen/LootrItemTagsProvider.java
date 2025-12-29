package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;

import java.util.concurrent.CompletableFuture;

public class LootrItemTagsProvider extends ItemTagsProvider {
  public LootrItemTagsProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagsProvider.TagLookup<Block>> p_275322_, @org.jetbrains.annotations.Nullable ExistingFileHelper existingFileHelper) {
    super(p_275343_, p_275729_, p_275322_, LootrAPI.MODID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    tag(LootrTags.Items.BARRELS).add(LootrRegistry.getBarrelItem());
    tag(LootrTags.Items.CHESTS).add(LootrRegistry.getChestItem(), LootrRegistry.getTrappedChestItem(), LootrRegistry.getInventoryItem());
    tag(LootrTags.Items.TRAPPED_CHESTS).add(LootrRegistry.getTrappedChestItem());
    tag(LootrTags.Items.SHULKERS).add(LootrRegistry.getShulkerItem());
    tag(LootrTags.Items.SANDS).add(LootrRegistry.getSuspiciousSandItem());
    tag(LootrTags.Items.GRAVELS).add(LootrRegistry.getSuspiciousGravelItem());
    tag(LootrTags.Items.POTS).add(LootrRegistry.getDecoratedPotItem());
    //noinspection unchecked
    tag(LootrTags.Items.CONTAINERS).addTags(LootrTags.Items.BARRELS, LootrTags.Items.CHESTS, LootrTags.Items.TRAPPED_CHESTS, LootrTags.Items.SHULKERS, LootrTags.Items.SANDS, LootrTags.Items.GRAVELS, LootrTags.Items.POTS);
    tag(LootrTags.Items.ITEM_FRAME_CONVERT_BLACKLIST).add(Items.FILLED_MAP, Items.MAP);
  }

  @Override
  public String getName() {
    return "Lootr Item Tags";
  }
}
