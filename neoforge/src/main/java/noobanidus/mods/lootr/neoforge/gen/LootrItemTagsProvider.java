package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class LootrItemTagsProvider extends ItemTagsProvider {
  public LootrItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
    super(output, lookup, LootrAPI.MODID);
  }

  @Override
  protected void addTags(HolderLookup.@NonNull Provider provider) {
    tag(LootrTags.Items.BARRELS).add(LootrRegistry.getBarrelItem());
    tag(LootrTags.Items.CHESTS).add(LootrRegistry.getChestItem(), LootrRegistry.getTrappedChestItem());
    tag(LootrTags.Items.TRAPPED_CHESTS).add(LootrRegistry.getTrappedChestItem());
    tag(LootrTags.Items.SHULKERS).add(LootrRegistry.getShulkerBoxItem());
    tag(LootrTags.Items.SANDS).add(LootrRegistry.getSuspiciousSandItem());
    tag(LootrTags.Items.GRAVELS).add(LootrRegistry.getSuspiciousGravelItem());
    tag(LootrTags.Items.POTS).add(LootrRegistry.getDecoratedPotItem());
    //noinspection unchecked
    tag(LootrTags.Items.CONTAINERS).addTags(LootrTags.Items.BARRELS, LootrTags.Items.CHESTS, LootrTags.Items.TRAPPED_CHESTS, LootrTags.Items.SHULKERS, LootrTags.Items.SANDS, LootrTags.Items.GRAVELS, LootrTags.Items.POTS);
    tag(LootrTags.Items.ITEM_FRAME_CONVERT_BLACKLIST).add(Items.FILLED_MAP, Items.MAP);
  }

  @Override
  public @NonNull String getName() {
    return "Lootr Item Tags";
  }
}
