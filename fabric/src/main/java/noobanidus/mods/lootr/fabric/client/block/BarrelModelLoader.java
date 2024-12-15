package noobanidus.mods.lootr.fabric.client.block;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import noobanidus.mods.lootr.common.api.LootrAPI;
import org.jetbrains.annotations.Nullable;

public class BarrelModelLoader implements ModelLoadingPlugin {
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

  // Old trapped models
  private static final ResourceLocation OLD_LOOTR_OPENED_BARREL = LootrAPI.rl("block/old_lootr_opened_barrel");
  private static final ResourceLocation OLD_LOOTR_OPENED_BARREL_OPEN = LootrAPI.rl("block/old_lootr_opened_barrel_open");

  @Override
  public void initialize(ModelLoadingPlugin.Context pluginContext) {
    pluginContext.addModels(LOOTR_BARREL_MODEL_UNOPENED, LOOTR_BARREL_MODEL_OPENED);
    pluginContext.modifyModelOnLoad()
        .register(ModelModifier.OVERRIDE_PHASE, // TODO: Is this the right phrase?
        new ModelModifier.OnLoad() {
          @Override
          public @Nullable UnbakedModel modifyModelOnLoad(@Nullable UnbakedModel model, Context context) {
            ResourceLocation resourceId = context.id();
            if (resourceId.equals(LOOTR_BARREL_MODEL_UNOPENED)) {
              return new BarrelModel(LOOTR_OPENED_BARREL, LOOTR_BARREL_UNOPENED, VANILLA, OLD_LOOTR_OPENED_BARREL, OLD_LOOTR_BARREL_UNOPENED);
            } else if (resourceId.equals(LOOTR_BARREL_MODEL_OPENED)) {
              return new BarrelModel(LOOTR_OPENED_BARREL_OPEN, LOOTR_BARREL_UNOPENED_OPEN, VANILLA_OPEN, OLD_LOOTR_OPENED_BARREL_OPEN, OLD_LOOTR_BARREL_UNOPENED_OPEN);
            } else {
              return model;
            }
          }
        }
    );
  }
}
