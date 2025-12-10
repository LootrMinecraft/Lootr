package noobanidus.mods.lootr.fabric.client.block;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import noobanidus.mods.lootr.common.api.LootrAPI;
import org.jetbrains.annotations.Nullable;

public class BrushableModelLoader implements ModelLoadingPlugin, ModelResolver {
  public static final BrushableModelLoader INSTANCE = new BrushableModelLoader();

  private static final ResourceLocation SAND_OPENED = LootrAPI.rl("block/suspicious_sand_open");
  private static final ResourceLocation SAND_STAGE_0 = LootrAPI.mc("block/suspicious_sand_0");
  private static final ResourceLocation SAND_STAGE_1 = LootrAPI.mc("block/suspicious_sand_1");
  private static final ResourceLocation SAND_STAGE_2 = LootrAPI.mc("block/suspicious_sand_2");
  private static final ResourceLocation SAND_STAGE_3 = LootrAPI.mc("block/suspicious_sand_3");

  private static final ResourceLocation GRAVEL_OPENED = LootrAPI.rl("block/suspicious_gravel_open");
  private static final ResourceLocation GRAVEL_STAGE_0 = LootrAPI.mc("block/suspicious_gravel_0");
  private static final ResourceLocation GRAVEL_STAGE_1 = LootrAPI.mc("block/suspicious_gravel_1");
  private static final ResourceLocation GRAVEL_STAGE_2 = LootrAPI.mc("block/suspicious_gravel_2");
  private static final ResourceLocation GRAVEL_STAGE_3 = LootrAPI.mc("block/suspicious_gravel_3");

  // Model references
  private static final ResourceLocation SUSPICIOUS_SAND = LootrAPI.rl("block/suspicious_sand");
  private static final ResourceLocation SUSPICIOUS_GRAVEL = LootrAPI.rl("block/suspicious_gravel");

  @Override
  public @Nullable UnbakedModel resolveModel(ModelResolver.Context context) {
    ResourceLocation resourceId = context.id();
    // Fix for #613:
    // It seems possible for other mods to try loading resource locations dynamically from maps
    // which end up being null. Thus, we check for null here to avoid causing crashes.
    if (resourceId == null) {
      return null;
    }
    if (resourceId.equals(SUSPICIOUS_SAND)) {
      return new BrushableModel(context.getOrLoadModel(SAND_OPENED), context.getOrLoadModel(SAND_STAGE_0), context.getOrLoadModel(SAND_STAGE_1), context.getOrLoadModel(SAND_STAGE_2), context.getOrLoadModel(SAND_STAGE_3));
    } else if (resourceId.equals(SUSPICIOUS_GRAVEL)) {
      return new BrushableModel(context.getOrLoadModel(GRAVEL_OPENED), context.getOrLoadModel(GRAVEL_STAGE_0), context.getOrLoadModel(GRAVEL_STAGE_1), context.getOrLoadModel(GRAVEL_STAGE_2), context.getOrLoadModel(GRAVEL_STAGE_3));
    } else {
      return null;
    }
  }

  @Override
  public void onInitializeModelLoader(ModelLoadingPlugin.Context pluginContext) {
    pluginContext.resolveModel().register(this);
  }
}
