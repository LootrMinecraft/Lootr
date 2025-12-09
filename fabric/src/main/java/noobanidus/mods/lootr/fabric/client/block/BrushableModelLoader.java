package noobanidus.mods.lootr.fabric.client.block;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import noobanidus.mods.lootr.common.api.LootrAPI;
import org.jetbrains.annotations.Nullable;

public class BrushableModelLoader implements ModelLoadingPlugin, ModelResolver {
  public static final BrushableModelLoader INSTANCE = new BrushableModelLoader();

  private static final ResourceLocation SAND_OPENED = LootrAPI.rl("block/sand_opened");
  private static final ResourceLocation SAND_STAGE_0 = LootrAPI.rl("block/sand_stage_0");
  private static final ResourceLocation SAND_STAGE_1 = LootrAPI.rl("block/sand_stage_1");
  private static final ResourceLocation SAND_STAGE_2 = LootrAPI.rl("block/sand_stage_2");
  private static final ResourceLocation SAND_STAGE_3 = LootrAPI.rl("block/sand_stage_3");

  private static final ResourceLocation GRAVEL_OPENED = LootrAPI.rl("block/gravel_opened");
  private static final ResourceLocation GRAVEL_STAGE_0 = LootrAPI.rl("block/gravel_stage_0");
  private static final ResourceLocation GRAVEL_STAGE_1 = LootrAPI.rl("block/gravel_stage_1");
  private static final ResourceLocation GRAVEL_STAGE_2 = LootrAPI.rl("block/gravel_stage_2");
  private static final ResourceLocation GRAVEL_STAGE_3 = LootrAPI.rl("block/gravel_stage_3");

  // Model references
  private static final ResourceLocation LOOTR_BARREL_MODEL_UNOPENED = LootrAPI.rl("block/lootr_barrel");
  private static final ResourceLocation LOOTR_BARREL_MODEL_OPENED = LootrAPI.rl("block/lootr_barrel_open");

  // Unopened models
  private static final ResourceLocation LOOTR_BARREL_UNOPENED = LootrAPI.rl("block/lootr_barrel_unopened");
  private static final ResourceLocation LOOTR_BARREL_UNOPENED_OPEN = LootrAPI.rl("block/lootr_barrel_unopened_open");

  // Opened models
  private static final ResourceLocation LOOTR_OPENED_BARREL = LootrAPI.rl("block/lootr_opened_barrel");
  private static final ResourceLocation LOOTR_OPENED_BARREL_OPEN = LootrAPI.rl("block/lootr_opened_barrel_open");

  // Vanilla models
  private static final ResourceLocation VANILLA = ResourceLocation.fromNamespaceAndPath("minecraft", "block/barrel");
  private static final ResourceLocation VANILLA_OPEN = ResourceLocation.fromNamespaceAndPath("minecraft", "block/barrel_open");

  // Old unopened models
  private static final ResourceLocation OLD_LOOTR_BARREL_UNOPENED = LootrAPI.rl("block/old_lootr_barrel_unopened");
  private static final ResourceLocation OLD_LOOTR_BARREL_UNOPENED_OPEN = LootrAPI.rl("block/old_lootr_barrel_unopened_open");

  // Old opened models
  private static final ResourceLocation OLD_LOOTR_OPENED_BARREL = LootrAPI.rl("block/old_lootr_opened_barrel");
  private static final ResourceLocation OLD_LOOTR_OPENED_BARREL_OPEN = LootrAPI.rl("block/old_lootr_opened_barrel_open");

  @Override
  public @Nullable UnbakedModel resolveModel(ModelResolver.Context context) {
    ResourceLocation resourceId = context.id();
    // Fix for #613:
    // It seems possible for other mods to try loading resource locations dynamically from maps
    // which end up being null. Thus, we check for null here to avoid causing crashes.
    if (resourceId == null) {
      return null;
    }
    if (resourceId.equals(LOOTR_BARREL_MODEL_UNOPENED)) {
      return new BarrelModel(context.getOrLoadModel(LOOTR_OPENED_BARREL), context.getOrLoadModel(LOOTR_BARREL_UNOPENED), context.getOrLoadModel(VANILLA), context.getOrLoadModel(OLD_LOOTR_OPENED_BARREL), context.getOrLoadModel(OLD_LOOTR_BARREL_UNOPENED));
    } else if (resourceId.equals(LOOTR_BARREL_MODEL_OPENED)) {
      return new BarrelModel(context.getOrLoadModel(LOOTR_OPENED_BARREL_OPEN), context.getOrLoadModel(LOOTR_BARREL_UNOPENED_OPEN), context.getOrLoadModel(VANILLA_OPEN), context.getOrLoadModel(OLD_LOOTR_OPENED_BARREL_OPEN), context.getOrLoadModel(OLD_LOOTR_BARREL_UNOPENED_OPEN));
    } else {
      return null;
    }
  }

  @Override
  public void onInitializeModelLoader(ModelLoadingPlugin.Context pluginContext) {
    pluginContext.resolveModel().register(this);
  }
}
