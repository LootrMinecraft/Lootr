package noobanidus.mods.lootr.fabric.client.block;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import noobanidus.mods.lootr.common.api.client.ILootrFabricModelProvider;
import noobanidus.mods.lootr.common.impl.LootrServiceRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CustomModelLoader implements ModelLoadingPlugin, ModelResolver, ILootrFabricModelProvider.Acceptor {
  public static final CustomModelLoader INSTANCE = new CustomModelLoader();
  private static final Map<ResourceLocation, CustomBarrelModelInfo> BARREL_MODEL_MAP = new HashMap<>();
  private static final Map<ResourceLocation, CustomBrushableModelInfo> BRUSHABLE_MODEL_MAP = new HashMap<>();

  @Override
  public @Nullable UnbakedModel resolveModel(ModelResolver.Context context) {
    ResourceLocation resourceId = context.id();
    // Fix for #613:
    // It seems possible for other mods to try loading resource locations dynamically from maps
    // which end up being null. Thus, we check for null here to avoid causing crashes.
    if (resourceId == null) {
      return null;
    }
    if (BARREL_MODEL_MAP.containsKey(resourceId)) {
      CustomBarrelModelInfo info = BARREL_MODEL_MAP.get(resourceId);
      return new CustomBarrelModel(context.getOrLoadModel(info.opened), context.getOrLoadModel(info.unopened), info.vanilla != null ? context.getOrLoadModel(info.vanilla) : null);
    } else if (BRUSHABLE_MODEL_MAP.containsKey(resourceId)) {
      CustomBrushableModelInfo info = BRUSHABLE_MODEL_MAP.get(resourceId);
      return new BrushableModel(context.getOrLoadModel(info.opened), context.getOrLoadModel(info.stage0), context.getOrLoadModel(info.stage1), context.getOrLoadModel(info.stage2), context.getOrLoadModel(info.stage3));
    } else {
      return null;
    }
  }

  @Override
  public void onInitializeModelLoader(ModelLoadingPlugin.Context pluginContext) {
    pluginContext.resolveModel().register(this);
    for (ILootrFabricModelProvider provider : LootrServiceRegistry.getModelAppenders()) {
      provider.provideModels(this);
    }
  }

  @Override
  public void acceptCustomModel(ResourceLocation modelName, ResourceLocation modelOpenedLocation, ResourceLocation modelUnopenedLocation, @Nullable ResourceLocation modelVanillaLocation) {
    BARREL_MODEL_MAP.put(modelName, new CustomBarrelModelInfo(modelOpenedLocation, modelUnopenedLocation, modelVanillaLocation));
  }

  @Override
  public void acceptBrushableModel(ResourceLocation modelName, ResourceLocation opened, ResourceLocation stage0, ResourceLocation stage1, ResourceLocation stage2, ResourceLocation stage3) {
    BRUSHABLE_MODEL_MAP.put(modelName, new CustomBrushableModelInfo(opened, stage0, stage1, stage2, stage3));
  }

  record CustomBarrelModelInfo (ResourceLocation opened, ResourceLocation unopened, @Nullable ResourceLocation vanilla) {
  }

  record CustomBrushableModelInfo (ResourceLocation opened, ResourceLocation stage0, ResourceLocation stage1, ResourceLocation stage2, ResourceLocation stage3) {
  }
}
