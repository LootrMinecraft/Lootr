package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;

import java.util.concurrent.CompletableFuture;

public class LootrItemTagsProvider extends ItemTagsProvider {
  public LootrItemTagsProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_) {
    super(p_275343_, p_275729_, LootrAPI.MODID);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    tag(LootrTags.Items.BARRELS).add(LootrRegistry.getBarrelItem());
    tag(LootrTags.Items.CHESTS).add(LootrRegistry.getChestItem(), LootrRegistry.getTrappedChestItem(), LootrRegistry.getInventoryItem());
    tag(LootrTags.Items.TRAPPED_CHESTS).add(LootrRegistry.getTrappedChestItem());
    tag(LootrTags.Items.SHULKERS).add(LootrRegistry.getShulkerItem());
    //noinspection unchecked
    tag(LootrTags.Items.CONTAINERS).addTags(LootrTags.Items.BARRELS, LootrTags.Items.CHESTS, LootrTags.Items.TRAPPED_CHESTS, LootrTags.Items.SHULKERS);
  }

  @Override
  public String getName() {
    return "Lootr Item Tags";
  }
}
