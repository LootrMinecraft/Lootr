package noobanidus.mods.lootr.common.mixin.accessor;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(LevelRenderer.class)
public interface AccessorMixinLevelRenderer {
  @Accessor("capturedFrustum")
  Frustum lootr$getCapturedFrustum();

  @Accessor("cullingFrustum")
  Frustum lootr$getCullingFrustum();

  @Accessor("visibleSections")
  ObjectArrayList<SectionRenderDispatcher.RenderSection> lootr$getVisibleSections();

  @Accessor("globalBlockEntities")
  Set<BlockEntity> lootr$getGlobalBlockEntities();
}
