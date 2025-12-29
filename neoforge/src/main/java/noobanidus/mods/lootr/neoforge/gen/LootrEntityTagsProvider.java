package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.TagEntry;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.neoforge.init.ModEntities;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class LootrEntityTagsProvider extends EntityTypeTagsProvider {
  public LootrEntityTagsProvider(PackOutput arg, CompletableFuture<HolderLookup.Provider> completableFuture,@Nullable ExistingFileHelper existingFileHelper) {
    super(arg, completableFuture, LootrAPI.MODID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    tag(LootrTags.Entity.CONVERT_CARTS).add(EntityType.CHEST_MINECART);
    tag(LootrTags.Entity.CONVERT_ITEM_FRAMES).add(EntityType.ITEM_FRAME, EntityType.GLOW_ITEM_FRAME);
    //noinspection unchecked
    tag(LootrTags.Entity.CONVERT_ENTITIES).addTags(LootrTags.Entity.CONVERT_CARTS, LootrTags.Entity.CONVERT_ITEM_FRAMES);

    tag(LootrTags.Entity.MINECARTS).add(TagEntry.optionalTag(ModEntities.LOOTR_MINECART_ENTITY.getId())).add(TagEntry.optionalTag(LootrConstants.LOOTR_CART)); // Disparity between Fabric and NeoForge: `lootr_cart` for Fabric, `lootr_minecart` for NeoForge.
    tag(LootrTags.Entity.ITEM_FRAMES).add(ModEntities.ITEM_FRAME.get());

    //noinspection unchecked
    tag(LootrTags.Entity.CONTAINERS).addTags(LootrTags.Entity.MINECARTS, LootrTags.Entity.ITEM_FRAMES);
  }

  @Override
  public String getName() {
    return "Lootr Entity Type Tags";
  }
}
