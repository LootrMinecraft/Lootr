package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class LootrBlockEntityTagsProvider extends IntrinsicHolderTagsProvider<BlockEntityType<?>> {
  public LootrBlockEntityTagsProvider(PackOutput arg, CompletableFuture<HolderLookup.Provider> completableFuture) {
    super(arg, Registries.BLOCK_ENTITY_TYPE, completableFuture, (BlockEntityType<?> arg2) -> Objects.requireNonNull(arg2.builtInRegistryHolder())
        .key(), LootrAPI.MODID);
  }

  @Override
  protected void addTags(HolderLookup.@NonNull Provider provider) {
    tag(LootrTags.BlockEntity.LOOTR_OBJECT);
    tag(LootrTags.BlockEntity.TRAPPED).add(LootrRegistry.getTrappedChestBlockEntity());
    tag(LootrTags.BlockEntity.CUSTOM_INELIGIBLE);
    tag(LootrTags.BlockEntity.CONVERT_BLACKLIST)
        // We just blacklist everything 'cos
        .add(BlockEntityType.BANNER, BlockEntityType.BEACON, BlockEntityType.BED, BlockEntityType.BEEHIVE, BlockEntityType.BELL, BlockEntityType.BLAST_FURNACE, BlockEntityType.BREWING_STAND, BlockEntityType.CALIBRATED_SCULK_SENSOR, BlockEntityType.CAMPFIRE, BlockEntityType.CHISELED_BOOKSHELF, BlockEntityType.COMMAND_BLOCK, BlockEntityType.COMPARATOR, BlockEntityType.CONDUIT, BlockEntityType.CRAFTER, BlockEntityType.DAYLIGHT_DETECTOR, BlockEntityType.DISPENSER, BlockEntityType.DROPPER, BlockEntityType.ENCHANTING_TABLE, BlockEntityType.ENDER_CHEST, BlockEntityType.FURNACE, BlockEntityType.HANGING_SIGN, BlockEntityType.HOPPER, BlockEntityType.JIGSAW, BlockEntityType.JUKEBOX, BlockEntityType.LECTERN, BlockEntityType.PISTON, BlockEntityType.SCULK_CATALYST, BlockEntityType.SCULK_SENSOR, BlockEntityType.SCULK_SHRIEKER, BlockEntityType.SIGN, BlockEntityType.SKULL, BlockEntityType.SMOKER, BlockEntityType.MOB_SPAWNER, BlockEntityType.STRUCTURE_BLOCK, BlockEntityType.END_GATEWAY, BlockEntityType.END_PORTAL, BlockEntityType.TRIAL_SPAWNER, BlockEntityType.VAULT)
        // We also blacklist our own stuff from being converted again. This may cut down on instanceof checks.
        .add(LootrRegistry.getBrushableBlockEntity(), LootrRegistry.getBarrelBlockEntity(), LootrRegistry.getChestBlockEntity(), LootrRegistry.getTrappedChestBlockEntity(), LootrRegistry.getShulkerBoxBlockEntity(), LootrRegistry.getDecoratedPotBlockEntity());
  }

  @Override
  public @NonNull String getName() {
    return "Lootr Block Entity Type Tags";
  }
}