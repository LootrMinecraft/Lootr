package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypeIds;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.api.LootrTags;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unchecked")
public class LootrBlockEntityTagsProvider extends TagsProvider<BlockEntityType<?>> {
  public LootrBlockEntityTagsProvider(PackOutput arg, CompletableFuture<HolderLookup.Provider> completableFuture) {
    super(arg, Registries.BLOCK_ENTITY_TYPE, completableFuture, LootrAPI.MODID);
  }

  @Override
  protected void addTags(HolderLookup.@NonNull Provider provider) {
    tag(LootrTags.BlockEntity.LOOTR_OBJECT);
    tag(LootrTags.BlockEntity.TRAPPED).add(LootrConstants.LootrBlockEntityIds.TRAPPED_CHEST);
    tag(LootrTags.BlockEntity.CUSTOM_INELIGIBLE);
    tag(LootrTags.BlockEntity.CONVERT_BLACKLIST)
        // We just blacklist everything 'cos
        .add(BlockEntityTypeIds.BANNER, BlockEntityTypeIds.BEACON, BlockEntityTypeIds.BEEHIVE, BlockEntityTypeIds.BELL, BlockEntityTypeIds.BLAST_FURNACE, BlockEntityTypeIds.BREWING_STAND, BlockEntityTypeIds.CALIBRATED_SCULK_SENSOR, BlockEntityTypeIds.CAMPFIRE, BlockEntityTypeIds.CHISELED_BOOKSHELF, BlockEntityTypeIds.COMMAND_BLOCK, BlockEntityTypeIds.COMPARATOR, BlockEntityTypeIds.CONDUIT, BlockEntityTypeIds.CRAFTER, BlockEntityTypeIds.DAYLIGHT_DETECTOR, BlockEntityTypeIds.DISPENSER, BlockEntityTypeIds.DROPPER, BlockEntityTypeIds.ENCHANTING_TABLE, BlockEntityTypeIds.ENDER_CHEST, BlockEntityTypeIds.FURNACE, BlockEntityTypeIds.HANGING_SIGN, BlockEntityTypeIds.HOPPER, BlockEntityTypeIds.JIGSAW, BlockEntityTypeIds.JUKEBOX, BlockEntityTypeIds.LECTERN, BlockEntityTypeIds.PISTON, BlockEntityTypeIds.SCULK_CATALYST, BlockEntityTypeIds.SCULK_SENSOR, BlockEntityTypeIds.SCULK_SHRIEKER, BlockEntityTypeIds.SIGN, BlockEntityTypeIds.SKULL, BlockEntityTypeIds.SMOKER, BlockEntityTypeIds.MOB_SPAWNER, BlockEntityTypeIds.STRUCTURE_BLOCK, BlockEntityTypeIds.END_GATEWAY, BlockEntityTypeIds.END_PORTAL, BlockEntityTypeIds.TRIAL_SPAWNER, BlockEntityTypeIds.VAULT)
        // We also blacklist our own stuff from being converted again. This may cut down on instanceof checks.
        .add(LootrConstants.LootrBlockEntityIds.BARREL, LootrConstants.LootrBlockEntityIds.BRUSHABLE_BLOCK, LootrConstants.LootrBlockEntityIds.CHEST, LootrConstants.LootrBlockEntityIds.DECORATED_POT, LootrConstants.LootrBlockEntityIds.SHULKER_BOX, LootrConstants.LootrBlockEntityIds.TRAPPED_CHEST);
  }

  @Override
  public @NonNull String getName() {
    return "Lootr Block Entity Type Tags";
  }
}