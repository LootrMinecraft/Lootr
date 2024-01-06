package noobanidus.mods.lootr.mixin;

import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import noobanidus.mods.lootr.client.block.LootrChestBlockRenderer;
import noobanidus.mods.lootr.client.block.LootrShulkerBlockRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

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
