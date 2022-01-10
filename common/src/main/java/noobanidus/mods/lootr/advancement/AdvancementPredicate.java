package noobanidus.mods.lootr.advancement;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import noobanidus.mods.lootr.api.advancement.IGenericPredicate;

public class AdvancementPredicate implements IGenericPredicate<ResourceLocation> {
  private ResourceLocation advancementId;

  public AdvancementPredicate() {
  }

  public AdvancementPredicate(ResourceLocation advancementId) {
    this.advancementId = advancementId;
  }

  @Override
  public boolean test(ServerPlayer player, ResourceLocation location) {
    return advancementId != null && advancementId.equals(location);
  }

  @Override
  public IGenericPredicate<ResourceLocation> deserialize(JsonObject element) {
    if (element == null) {
      throw new IllegalArgumentException("AdvancementPredicate requires an object");
    }
    ResourceLocation rl = new ResourceLocation(element.getAsJsonPrimitive("advancement").getAsString());
    return new AdvancementPredicate(rl);
  }
}
