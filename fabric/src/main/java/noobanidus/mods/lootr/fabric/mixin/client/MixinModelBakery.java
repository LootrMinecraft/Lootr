package noobanidus.mods.lootr.fabric.mixin.client;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelIdentifier;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.profiling.ProfilerFiller;
import noobanidus.mods.lootr.common.client.entity.LootrItemFrameRenderer;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModelBakery.class)
public class MixinModelBakery {
  @Shadow
  private void registerModelAndLoadDependencies(ModelIdentifier modelLocation, UnbakedModel model) {
    throw new NotImplementedException();
  }

  @Shadow
  UnbakedModel getModel(Identifier modelLocation) {
    throw new NotImplementedException();
  }

  @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/Collection;forEach(Ljava/util/function/Consumer;)V"))
  private void lootr$RegisterAdditionalModels(BlockColors blockColors, ProfilerFiller profilerFiller, Map modelResources, Map blockStateResources, CallbackInfo ci) {
    UnbakedModel unbakedmodel = this.getModel(LootrItemFrameRenderer.FRAME_LOCATION.id());
    this.registerModelAndLoadDependencies(LootrItemFrameRenderer.FRAME_LOCATION, unbakedmodel);
    unbakedmodel = this.getModel(LootrItemFrameRenderer.FRAME_OPEN_LOCATION.id());
    this.registerModelAndLoadDependencies(LootrItemFrameRenderer.FRAME_OPEN_LOCATION, unbakedmodel);
  }
}
