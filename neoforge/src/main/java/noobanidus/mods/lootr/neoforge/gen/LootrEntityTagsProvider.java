package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import noobanidus.mods.lootr.common.api.LootrAPI;
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
    //noinspection unchecked
    tag(LootrTags.Entity.CONVERT_ENTITIES).addTags(LootrTags.Entity.CONVERT_CARTS);

    tag(LootrTags.Entity.MINECARTS).add(ModEntities.LOOTR_MINECART_ENTITY.get());
    tag(LootrTags.Entity.ITEM_FRAMES).add(ModEntities.ITEM_FRAME.get());

    //noinspection unchecked
    tag(LootrTags.Entity.CONTAINERS).addTags(LootrTags.Entity.MINECARTS, LootrTags.Entity.ITEM_FRAMES);
  }

  @Override
  public String getName() {
    return "Lootr Entity Type Tags";
  }
}
