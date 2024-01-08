package noobanidus.mods.lootr.mixin;

import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("deprecation")
@Mixin(TextureAtlas.class)
public class MixinTextureAtlas {
  /*
  @Inject(method = "prepareToStitch", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", shift = At.Shift.AFTER, ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
  private void preStitch(ResourceManager resourceManager, Stream<ResourceLocation> spriteStream, ProfilerFiller profiler, int i, CallbackInfoReturnable<TextureAtlas.Preparations> cir, Set<ResourceLocation> spriteSet) {
    TextureAtlas atlas = (TextureAtlas) (Object) this;
    if(atlas.location().equals(TextureAtlas.LOCATION_BLOCKS)) {
      spriteSet.add(LootrChestBlockRenderer.MATERIAL.texture());
      spriteSet.add(LootrChestBlockRenderer.MATERIAL2.texture());
      spriteSet.add(LootrShulkerBlockRenderer.MATERIAL.texture());
      spriteSet.add(LootrShulkerBlockRenderer.MATERIAL2.texture());
    }
  }
  @Inject(method = "upload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;uploadFirstFrame()V", shift = At.Shift.AFTER, ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
  private void lootr_upload(SpriteLoader.Preparations preparations, CallbackInfo ci, List list, List list2, Iterator var4, TextureAtlasSprite textureAtlasSprite) {
    TextureAtlas atlas = (TextureAtlas) (Object) this;
    if(atlas.location().equals(TextureAtlas.LOCATION_BLOCKS)) {
      list.add(LootrChestBlockRenderer.MATERIAL.texture());
      list.add(LootrChestBlockRenderer.MATERIAL2.texture());
      list.add(LootrShulkerBlockRenderer.MATERIAL.texture());
      list.add(LootrShulkerBlockRenderer.MATERIAL2.texture());
    }
  }

   */
}
