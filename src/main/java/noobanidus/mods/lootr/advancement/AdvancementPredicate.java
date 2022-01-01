package noobanidus.mods.lootr.advancement;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import noobanidus.mods.lootr.api.advancement.IGenericPredicate;

import javax.annotation.Nullable;

public class AdvancementPredicate implements IGenericPredicate<ResourceLocation> {
  private ResourceLocation advancementId;

  public AdvancementPredicate() {
  }

  public AdvancementPredicate(ResourceLocation advancementId) {
    this.advancementId = advancementId;
  }

  @Override
  public boolean test(ServerPlayerEntity player, ResourceLocation location) {
    return advancementId != null && advancementId.equals(location);
  }

  @Override
  public IGenericPredicate<ResourceLocation> deserialize(@Nullable JsonObject element) {
    if (element == null) {
      throw new IllegalArgumentException("AdvancementPredicate requires an object");
    }
    ResourceLocation rl = new ResourceLocation(element.getAsJsonPrimitive("advancement").getAsString());
    return new AdvancementPredicate(rl);
  }
}
