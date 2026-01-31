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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import noobanidus.mods.lootr.common.api.LootrAPI;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BrushableModel implements UnbakedModel {
  private final UnbakedModel opened;
  private final UnbakedModel stage0;
  private final UnbakedModel stage1;
  private final UnbakedModel stage2;
  private final UnbakedModel stage3;

  private Collection<Identifier> dependencies = null;

  public BrushableModel(UnbakedModel opened, UnbakedModel stage0, UnbakedModel stage1, UnbakedModel stage2, UnbakedModel stage3) {
    this.opened = opened;
    this.stage0 = stage0;
    this.stage1 = stage1;
    this.stage2 = stage2;
    this.stage3 = stage3;
  }

  @Override
  public Collection<Identifier> getDependencies() {
    if (dependencies == null) {
      this.dependencies = Streams.concat(opened.getDependencies().stream(), stage0.getDependencies().stream(), stage1.getDependencies().stream(), stage2.getDependencies().stream(), stage3.getDependencies().stream()).collect(Collectors.toSet());
    }
    return dependencies;
  }

  @Override
  public void resolveParents(Function<Identifier, UnbakedModel> function) {
    this.opened.resolveParents(function);
    this.stage0.resolveParents(function);
    this.stage1.resolveParents(function);
    this.stage2.resolveParents(function);
    this.stage3.resolveParents(function);
  }

  @Nullable
  @Override
  public BakedModel bake(ModelBaker modelBakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform) {
    return new BakedBrushableModel(opened.bake(modelBakery, spriteGetter, transform), stage0.bake(modelBakery, spriteGetter, transform),
        stage1.bake(modelBakery, spriteGetter, transform),
        stage2.bake(modelBakery, spriteGetter, transform),
        stage3.bake(modelBakery, spriteGetter, transform));
  }

  public static class BakedBrushableModel implements BakedModel, FabricBakedModel {
    private final BakedModel opened;
    private final BakedModel stage0;
    private final BakedModel stage1;
    private final BakedModel stage2;
    private final BakedModel stage3;

    public BakedBrushableModel(BakedModel opened, BakedModel stage0, BakedModel stage1, BakedModel stage2, BakedModel stage3) {
      this.opened = opened;
      this.stage0 = stage0;
      this.stage1 = stage1;
      this.stage2 = stage2;
      this.stage3 = stage3;
    }

    @Override
    public boolean isVanillaAdapter() {
      return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
      Object data = blockView.getBlockEntityRenderData(pos);

      BakedModel model;

      if (data == Boolean.TRUE) {
        model = opened;
      } else {
        model = switch (state.getValue(BlockStateProperties.DUSTED)) {
          case 3 -> stage3;
          case 2 -> stage2;
          case 1 -> stage1;
          default -> stage0;
        };
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
      return stage0.getQuads(state, side, rand);
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
      return this.stage0.getParticleIcon();
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
