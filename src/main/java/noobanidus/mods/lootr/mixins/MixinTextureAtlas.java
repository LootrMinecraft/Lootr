package noobanidus.mods.lootr.mixins;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import noobanidus.mods.lootr.client.block.LootrChestBlockRenderer;
import noobanidus.mods.lootr.client.block.LootrShulkerBlockRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Set;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
@Mixin(TextureAtlas.class)
public class MixinTextureAtlas {
  @Inject(method = "prepareToStitch", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", shift = At.Shift.AFTER, ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
  private void preStitch(ResourceManager resourceManager, Stream<ResourceLocation> spriteStream, ProfilerFiller profiler, int i, CallbackInfoReturnable<TextureAtlas.Preparations> cir, Set<ResourceLocation> spriteSet) {
    TextureAtlas atlas = (TextureAtlas) (Object) this;
    if (atlas.location().equals(TextureAtlas.LOCATION_BLOCKS)) {
      spriteSet.add(LootrChestBlockRenderer.MATERIAL.texture());
      spriteSet.add(LootrChestBlockRenderer.MATERIAL2.texture());
      spriteSet.add(LootrShulkerBlockRenderer.MATERIAL.texture());
      spriteSet.add(LootrShulkerBlockRenderer.MATERIAL2.texture());
    }
  }
}
