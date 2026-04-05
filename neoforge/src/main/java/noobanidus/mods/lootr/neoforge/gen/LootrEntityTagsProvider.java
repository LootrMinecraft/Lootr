package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.TagEntry;
import net.minecraft.world.entity.EntityType;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.neoforge.init.ModEntities;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class LootrEntityTagsProvider extends EntityTypeTagsProvider {
  public LootrEntityTagsProvider(PackOutput arg, CompletableFuture<HolderLookup.Provider> completableFuture) {
    super(arg, completableFuture, LootrAPI.MODID);
  }

  @Override
  protected void addTags(HolderLookup.@NonNull Provider provider) {
    tag(LootrTags.Entity.CONVERT_CARTS).add(EntityType.CHEST_MINECART);
    tag(LootrTags.Entity.CONVERT_ITEM_FRAMES).add(EntityType.ITEM_FRAME, EntityType.GLOW_ITEM_FRAME);
    //noinspection unchecked
    tag(LootrTags.Entity.CONVERT_ENTITIES).addTags(LootrTags.Entity.CONVERT_CARTS /* DO NOT INCLUDE ITEM FRAMES HERE, IT BREAKS THINGS */);

    tag(LootrTags.Entity.MINECARTS).add(TagEntry.optionalTag(ModEntities.MINECART_WITH_CHEST.getId()));
    tag(LootrTags.Entity.ITEM_FRAMES).add(ModEntities.ITEM_FRAME.get());

    //noinspection unchecked
    tag(LootrTags.Entity.CONTAINERS).addTags(LootrTags.Entity.MINECARTS, LootrTags.Entity.ITEM_FRAMES);
  }

  @Override
  public @NonNull String getName() {
    return "Lootr Entity Type Tags";
  }
}
