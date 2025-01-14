package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class LootrBlockEntityTagsProvider extends IntrinsicHolderTagsProvider<BlockEntityType<?>> {
  public LootrBlockEntityTagsProvider(PackOutput arg, CompletableFuture<HolderLookup.Provider> completableFuture, @Nullable ExistingFileHelper existingFileHelper) {
    super(arg, Registries.BLOCK_ENTITY_TYPE, completableFuture, (BlockEntityType<?> arg2) -> arg2.builtInRegistryHolder().key(), LootrAPI.MODID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    tag(LootrTags.BlockEntity.LOOTR_OBJECT);
    tag(LootrTags.BlockEntity.TRAPPED).add(LootrRegistry.getTrappedChestBlockEntity());
    tag(LootrTags.BlockEntity.CUSTOM_INELIGIBlE);
  }

  @Override
  public String getName() {
    return "Lootr Block Entity Type Tags";
  }
}