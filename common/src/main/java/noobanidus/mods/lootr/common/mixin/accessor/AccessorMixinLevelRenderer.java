package noobanidus.mods.lootr.common.mixin.accessor;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelRenderer.class)
public interface AccessorMixinLevelRenderer {
  @Accessor("capturedFrustum")
  Frustum lootr$getCapturedFrustum ();

  @Accessor("cullingFrustum")
  Frustum lootr$getCullingFrustum ();
}
