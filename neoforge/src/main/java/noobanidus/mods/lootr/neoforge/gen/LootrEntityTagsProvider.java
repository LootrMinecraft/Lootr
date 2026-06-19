package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityTypeIds;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.api.LootrTags;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unchecked")
public class LootrEntityTagsProvider extends EntityTypeTagsProvider {
  public LootrEntityTagsProvider(PackOutput arg, CompletableFuture<HolderLookup.Provider> completableFuture) {
    super(arg, completableFuture, LootrAPI.MODID);
  }

  @Override
  protected void addTags(HolderLookup.@NonNull Provider provider) {
    tag(LootrTags.Entity.CONVERT_CARTS).add(EntityTypeIds.CHEST_MINECART);
    tag(LootrTags.Entity.CONVERT_ITEM_FRAMES).add(EntityTypeIds.ITEM_FRAME, EntityTypeIds.GLOW_ITEM_FRAME);
    //noinspection unchecked
    tag(LootrTags.Entity.CONVERT_ENTITIES).addTags(LootrTags.Entity.CONVERT_CARTS /* DO NOT INCLUDE ITEM FRAMES HERE, IT BREAKS THINGS */);

    tag(LootrTags.Entity.MINECARTS).add(LootrConstants.LootrEntityIds.MINECART_WITH_CHEST_ENTITY);
    tag(LootrTags.Entity.ITEM_FRAMES).add(LootrConstants.LootrEntityIds.ITEM_FRAME_ENTITY);

    tag(LootrTags.Entity.PREVENT_BREAK_MINECARTS).addTag(LootrTags.Entity.MINECARTS);
    tag(LootrTags.Entity.PREVENT_BREAK_ITEM_FRAMES).addTag(LootrTags.Entity.ITEM_FRAMES);
    // noinspection unchecked
    tag(LootrTags.Entity.PREVENT_BREAK).addTags(LootrTags.Entity.PREVENT_BREAK_ITEM_FRAMES, LootrTags.Entity.PREVENT_BREAK_MINECARTS);

    tag(LootrTags.Entity.ENABLE_BREAK);

    //noinspection unchecked
    tag(LootrTags.Entity.CONTAINERS).addTags(LootrTags.Entity.MINECARTS, LootrTags.Entity.ITEM_FRAMES);
  }

  @Override
  public @NonNull String getName() {
    return "Lootr Entity Type Tags";
  }
}
