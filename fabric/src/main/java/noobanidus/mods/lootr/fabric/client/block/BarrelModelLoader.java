package noobanidus.mods.lootr.fabric.client.block;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import noobanidus.mods.lootr.common.api.LootrAPI;
import org.jetbrains.annotations.Nullable;

public class BarrelModelLoader implements ModelLoadingPlugin, ModelResolver {
  public static final BarrelModelLoader INSTANCE = new BarrelModelLoader();

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
