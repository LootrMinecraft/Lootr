package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.references.ItemIds;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.api.LootrTags;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unchecked")
public class LootrItemTagsProvider extends ItemTagsProvider {
  public LootrItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
    super(output, lookup, LootrAPI.MODID);
  }

  @Override
  protected void addTags(HolderLookup.@NonNull Provider provider) {
    tag(LootrTags.Items.BARRELS).add(LootrConstants.LootrItemIds.BARREL);
    tag(LootrTags.Items.CHESTS).add(LootrConstants.LootrItemIds.CHEST, LootrConstants.LootrItemIds.TRAPPED_CHEST);
    tag(LootrTags.Items.TRAPPED_CHESTS).add(LootrConstants.LootrItemIds.TRAPPED_CHEST);
    tag(LootrTags.Items.SHULKERS).add(LootrConstants.LootrItemIds.SHULKER_BOX);
    tag(LootrTags.Items.SANDS).add(LootrConstants.LootrItemIds.SUSPICIOUS_SAND);
    tag(LootrTags.Items.GRAVELS).add(LootrConstants.LootrItemIds.SUSPICIOUS_GRAVEL);
    tag(LootrTags.Items.POTS).add(LootrConstants.LootrItemIds.DECORATED_POT);
    tag(LootrTags.Items.COPPER_CHESTS).add(LootrRegistry.getCopperChestItem());
    tag(LootrTags.Items.WEATHERED_COPPER_CHESTS).add(LootrRegistry.getWeatheredCopperChestItem());
    tag(LootrTags.Items.EXPOSED_COPPER_CHESTS).add(LootrRegistry.getExposedCopperChestItem());
    tag(LootrTags.Items.OXIDIZED_COPPER_CHESTS).add(LootrRegistry.getOxidizedCopperChestItem());
    //noinspection unchecked
    tag(LootrTags.Items.CONTAINERS).addTags(LootrTags.Items.BARRELS, LootrTags.Items.CHESTS, LootrTags.Items.TRAPPED_CHESTS, LootrTags.Items.SHULKERS, LootrTags.Items.SANDS, LootrTags.Items.GRAVELS, LootrTags.Items.POTS);
    tag(LootrTags.Items.ITEM_FRAME_CONVERT_BLACKLIST).add(ItemIds.FILLED_MAP, ItemIds.MAP);
  }

  @Override
  public @NonNull String getName() {
    return "Lootr Item Tags";
  }
}
