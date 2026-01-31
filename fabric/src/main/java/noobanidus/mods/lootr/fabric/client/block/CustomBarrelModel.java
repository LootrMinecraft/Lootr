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
import net.minecraft.resources.Identifier;
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
import java.util.stream.Stream;

public class CustomBarrelModel implements UnbakedModel {
  private final UnbakedModel opened;
  private final UnbakedModel unopened;
  @Nullable
  private final UnbakedModel vanilla;
  private Collection<Identifier> dependencies = null;

  public CustomBarrelModel(UnbakedModel opened, UnbakedModel unopened, @Nullable UnbakedModel vanilla) {
    this.opened = opened;
    this.unopened = unopened;
    this.vanilla = vanilla;
  }

  @Override
  public Collection<Identifier> getDependencies() {
    if (dependencies == null) {
      Stream<Identifier> deps = Streams.concat(opened.getDependencies().stream(), unopened.getDependencies().stream());
      if (vanilla != null) {
        deps = Streams.concat(deps, vanilla.getDependencies().stream());
      }
      dependencies = deps.collect(Collectors.toSet());
    }
    return dependencies;
  }

  @Override
  public void resolveParents(Function<Identifier, UnbakedModel> function) {
    this.opened.resolveParents(function);
    this.unopened.resolveParents(function);
    if (vanilla != null) {
      this.vanilla.resolveParents(function);
    }
  }

  @Nullable
  @Override
  public BakedModel bake(ModelBaker modelBakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform) {
    return new CustomBakedBarrelModel(opened.bake(modelBakery, spriteGetter, transform), unopened.bake(modelBakery, spriteGetter, transform), vanilla == null ? null : vanilla.bake(modelBakery, spriteGetter, transform));
  }

  public static class CustomBakedBarrelModel implements BakedModel, FabricBakedModel {
    private final BakedModel opened;
    private final BakedModel unopened;
    @Nullable
    private final BakedModel vanilla;

    public CustomBakedBarrelModel(BakedModel opened, BakedModel unopened, @Nullable BakedModel vanilla) {
      this.opened = opened;
      this.unopened = unopened;
      this.vanilla = vanilla;
    }

    @Override
    public boolean isVanillaAdapter() {
      return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
      Object data = blockView.getBlockEntityRenderData(pos);
      BakedModel model = unopened;
      if (LootrAPI.isVanillaTextures() && vanilla != null) {
        model = vanilla;
      } if (data == Boolean.TRUE) {
        model = opened;
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
      if (LootrAPI.isVanillaTextures() && vanilla != null) {
        return vanilla.getQuads(state, side, rand);
      } else {
        return unopened.getQuads(state, side, rand);
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
