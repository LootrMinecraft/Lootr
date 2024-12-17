package noobanidus.mods.lootr.neoforge.client.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.model.StandardModelParameters;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;
import noobanidus.mods.lootr.common.client.block.BarrelModel;

public final class BarrelModelLoader implements UnbakedModelLoader<BarrelModel> {
  private static final BarrelModelLoader INSTANCE = new BarrelModelLoader();

  public static BarrelModelLoader getInstance() {
    return INSTANCE;
  }

  private BarrelModelLoader() {
  }

  @Override
  public BarrelModel read(JsonObject modelContents, JsonDeserializationContext deserializationContext) {
    ResourceLocation opened = ResourceLocation.parse(GsonHelper.getAsJsonObject(modelContents, "opened").get("parent").getAsString());
    ResourceLocation unopened = ResourceLocation.parse(GsonHelper.getAsJsonObject(modelContents, "unopened").get("parent").getAsString());
    ResourceLocation vanilla = ResourceLocation.parse(GsonHelper.getAsJsonObject(modelContents, "vanilla").get("parent").getAsString());
    ResourceLocation old_unopened = ResourceLocation.parse(GsonHelper.getAsJsonObject(modelContents, "old_unopened").get("parent").getAsString());
    ResourceLocation old_opened = ResourceLocation.parse(GsonHelper.getAsJsonObject(modelContents, "old_opened").get("parent").getAsString());
    StandardModelParameters parameters = StandardModelParameters.parse(modelContents, deserializationContext);
    boolean ambientOcclusion = parameters.ambientOcclusion() != null && parameters.ambientOcclusion();
    return new BarrelModel(BakedBarrelModel::new, ambientOcclusion, parameters.guiLight(), parameters.itemTransforms(), parameters.textures(), parameters.parent(), opened, unopened, vanilla, old_unopened, old_opened);
  }
}
