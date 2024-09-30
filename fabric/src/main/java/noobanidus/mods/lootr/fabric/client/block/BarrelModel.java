package noobanidus.mods.lootr.fabric.client.block;

import com.google.common.collect.Streams;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrAPI;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BarrelModel implements UnbakedModel {
  private final UnbakedModel opened;
  private final UnbakedModel unopened;
  private final UnbakedModel vanilla;
  private final UnbakedModel old_opened;
  private final UnbakedModel old_unopened;
  private Collection<ResourceLocation> dependencies = null;

  public BarrelModel(UnbakedModel opened, UnbakedModel unopened, UnbakedModel vanilla, UnbakedModel old_opened, UnbakedModel old_unopened) {
    this.opened = opened;
    this.unopened = unopened;
    this.vanilla = vanilla;
    this.old_opened = old_opened;
    this.old_unopened = old_unopened;
  }

  @Override
  public Collection<ResourceLocation> getDependencies() {
    if (dependencies == null) {
      this.dependencies = Streams.concat(opened.getDependencies().stream(), unopened.getDependencies().stream(), vanilla.getDependencies().stream(), old_opened.getDependencies().stream(), old_unopened.getDependencies().stream()).collect(Collectors.toSet());
    }
    return dependencies;
  }

  @Override
  public void resolveParents(Function<ResourceLocation, UnbakedModel> function) {
    this.opened.resolveParents(function);
    this.unopened.resolveParents(function);
    this.vanilla.resolveParents(function);
    this.old_opened.resolveParents(function);
    this.old_unopened.resolveParents(function);
  }

  @Nullable
  @Override
  public BakedModel bake(ModelBaker modelBakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform) {
    return new BakedBarrelModel(opened.bake(modelBakery, spriteGetter, transform), unopened.bake(modelBakery, spriteGetter, transform), vanilla.bake(modelBakery, spriteGetter, transform), old_opened.bake(modelBakery, spriteGetter, transform), old_unopened.bake(modelBakery, spriteGetter, transform));
  }

  public static class BakedBarrelModel implements BakedModel, FabricBakedModel {
    private final BakedModel opened;
    private final BakedModel unopened;
    private final BakedModel vanilla;
    private final BakedModel old_opened;
    private final BakedModel old_unopened;

    public BakedBarrelModel(BakedModel opened, BakedModel unopened, BakedModel vanilla, BakedModel old_opened, BakedModel old_unopened) {
      this.opened = opened;
      this.unopened = unopened;
      this.vanilla = vanilla;
      this.old_opened = old_opened;
      this.old_unopened = old_unopened;
    }

    @Override
    public boolean isVanillaAdapter() {
      return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
      Object data = blockView.getBlockEntityRenderData(pos);
      BakedModel model = LootrAPI.isOldTextures() ? old_unopened : unopened;
      if (LootrAPI.isVanillaTextures()) {
        model = vanilla;
      } else if (data == Boolean.TRUE) {
        model = LootrAPI.isOldTextures() ? old_opened : opened;
      }

      if (model != null) {
        QuadEmitter emitter = context.getEmitter();
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        if (renderer != null) {
          RenderMaterial material = renderer.materialById(RenderMaterial.MATERIAL_STANDARD);
          for (Direction dir : Direction.values()) {
            for (BakedQuad quad : model.getQuads(state, dir, randomSupplier.get())) {
              emitter.fromVanilla(quad, material, dir);
              emitter.emit();
            }
          }
          for (BakedQuad quad : model.getQuads(state, null, randomSupplier.get())) {
            emitter.fromVanilla(quad, material, null);
            emitter.emit();
          }
        }
      }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
      if (LootrAPI.isVanillaTextures()) {
        return vanilla.getQuads(state, side, rand);
      } else if (LootrAPI.isNewTextures()) {
        return unopened.getQuads(state, side, rand);
      } else {
        return old_unopened.getQuads(state, side, rand);
      }
    }

    @Override
    public boolean useAmbientOcclusion() {
      return true;
    }

    @Override
    public boolean isGui3d() {
      return true;
    }

    @Override
    public boolean usesBlockLight() {
      return true;
    }

    @Override
    public boolean isCustomRenderer() {
      return true;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
      return this.unopened.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
      return ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public ItemOverrides getOverrides() {
      return ItemOverrides.EMPTY;
    }
  }

}
