package noobanidus.mods.lootr.fabric.client.block;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrAPI;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BarrelModel implements UnbakedModel {
  private final ResourceLocation openedLocation;
  private final ResourceLocation unopenedLocation;
  private final ResourceLocation vanillaLocation;
  private final ResourceLocation oldOpenedLocation;
  private final ResourceLocation oldUnopenedLocation;
  private final ResourceLocation parentLocation;

  private UnbakedModel opened;
  private UnbakedModel unopened;
  private UnbakedModel vanilla;
  private UnbakedModel old_opened;
  private UnbakedModel old_unopened;
  private UnbakedModel parent;

  public BarrelModel(ResourceLocation opened, ResourceLocation unopened, ResourceLocation vanilla, ResourceLocation old_opened, ResourceLocation old_unopened) {
    this.openedLocation = opened;
    this.unopenedLocation = unopened;
    this.vanillaLocation = vanilla;
    this.oldOpenedLocation = old_opened;
    this.oldUnopenedLocation = old_unopened;
    this.parentLocation = ResourceLocation.withDefaultNamespace("block/block");
  }

  @Override
  public void resolveDependencies(Resolver resolver) {
    this.opened = resolver.resolve(this.openedLocation);
    this.unopened = resolver.resolve(this.unopenedLocation);
    this.vanilla = resolver.resolve(this.vanillaLocation);
    this.old_opened = resolver.resolve(this.oldOpenedLocation);
    this.old_unopened = resolver.resolve(this.oldUnopenedLocation);
    this.parent = resolver.resolve(this.parentLocation);
  }

  @Nullable
  @Override
  public UnbakedModel getParent() {
    return this.parent;
  }

  @Override
  public BakedModel bake(TextureSlots textureSlots, ModelBaker modelBaker, ModelState modelState, boolean bl, boolean bl2, ItemTransforms itemTransforms) {
    return new BakedBarrelModel(opened.bake(textureSlots, modelBaker, modelState, bl, bl2, itemTransforms), unopened.bake(textureSlots, modelBaker, modelState, bl, bl2, itemTransforms), vanilla.bake(textureSlots, modelBaker, modelState, bl, bl2, itemTransforms), old_opened.bake(textureSlots, modelBaker, modelState, bl, bl2, itemTransforms), old_unopened.bake(textureSlots, modelBaker, modelState, bl, bl2, itemTransforms));
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
    public void emitBlockQuads(QuadEmitter emitter, BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, Predicate<@Nullable Direction> cullTest) {
      Object data = blockView.getBlockEntityRenderData(pos);
      BakedModel model = LootrAPI.isOldTextures() ? old_unopened : unopened;
      if (LootrAPI.isVanillaTextures()) {
        model = vanilla;
      } else if (data == Boolean.TRUE) {
        model = LootrAPI.isOldTextures() ? old_opened : opened;
      }

      model.emitBlockQuads(emitter, blockView, state, pos, randomSupplier, cullTest);
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
    public TextureAtlasSprite getParticleIcon() {
      return this.unopened.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
      return ItemTransforms.NO_TRANSFORMS;
    }
  }
}
