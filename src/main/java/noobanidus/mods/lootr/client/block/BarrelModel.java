package noobanidus.mods.lootr.client.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelTransformComposition;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import noobanidus.mods.lootr.block.LootrBarrelBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class BarrelModel implements IModelGeometry<BarrelModel> {
  private final IUnbakedModel opened;
  private final IUnbakedModel unopened;

  public BarrelModel(IUnbakedModel opened, IUnbakedModel unopened) {
    this.opened = opened;
    this.unopened = unopened;
  }

  @Override
  public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
    Set<RenderMaterial> materials = Sets.newHashSet();
    materials.add(owner.resolveTexture("particle"));
    materials.addAll(unopened.getMaterials(modelGetter, missingTextureErrors));
    materials.addAll(opened.getMaterials(modelGetter, missingTextureErrors));
    return materials;
  }

  private static IBakedModel buildModel(IUnbakedModel entry, IModelTransform modelTransform, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, ResourceLocation modelLocation) {
    return entry.bake(bakery, spriteGetter, modelTransform, modelLocation);
  }

  @Override
  public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
    return new BarrelBakedModel(owner.useSmoothLighting(), owner.isShadedInGui(), owner.isSideLit(),
        spriteGetter.apply(owner.resolveTexture("particle")), overrides,
        buildModel(opened, modelTransform, bakery, spriteGetter, modelLocation),
        buildModel(unopened, modelTransform, bakery, spriteGetter, modelLocation),
        PerspectiveMapWrapper.getTransforms(new ModelTransformComposition(owner.getCombinedTransform(), modelTransform))
    );
  }

  private static final class BarrelBakedModel implements IDynamicBakedModel {
    private final IBakedModel opened;
    private final IBakedModel unopened;
    private final ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> cameraTransforms;
    protected final boolean ambientOcclusion;
    protected final boolean gui3d;
    protected final boolean isSideLit;
    protected final TextureAtlasSprite particle;
    protected final ItemOverrideList overrides;

    public BarrelBakedModel(boolean ambientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrideList overrides, IBakedModel opened, IBakedModel unopened, ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> cameraTransforms) {
      this.isSideLit = isSideLit;
      this.cameraTransforms = cameraTransforms;
      this.ambientOcclusion = ambientOcclusion;
      this.gui3d = isGui3d;
      this.particle = particle;
      this.overrides = overrides;
      this.opened = opened;
      this.unopened = unopened;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
      IBakedModel model;
      if (extraData.getData(LootrBarrelBlock.OPENED) == Boolean.TRUE) {
        model = opened;
      } else {
        model = unopened;
      }
      ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
      builder.addAll(model.getQuads(state, side, rand, extraData));
      return builder.build();
    }

    @Override
    public boolean useAmbientOcclusion() {
      return ambientOcclusion;
    }

    @Override
    public boolean isAmbientOcclusion(BlockState state) {
      return ambientOcclusion;
    }

    @Override
    public boolean isGui3d() {
      return gui3d;
    }

    @Override
    public boolean usesBlockLight() {
      return isSideLit;
    }

    @Override
    public boolean isCustomRenderer() {
      return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
      return particle;
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
      if (data.getData(LootrBarrelBlock.OPENED) == Boolean.TRUE) {
        return opened.getParticleIcon();
      } else {
        return unopened.getParticleIcon();
      }
    }

    @Override
    public boolean doesHandlePerspectives() {
      return true;
    }

    @Override
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
      return PerspectiveMapWrapper.handlePerspective(this, cameraTransforms, cameraTransformType, mat);
    }

    @Override
    public ItemOverrideList getOverrides() {
      return ItemOverrideList.EMPTY;
    }

    @Override
    public boolean isLayered() {
      return false;
    }
  }

  public static final class Loader implements IModelLoader<BarrelModel> {
    public static final Loader INSTANCE = new Loader();

    private Loader() {
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }

    @Override
    public BarrelModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
      IUnbakedModel unopened = deserializationContext.deserialize(JSONUtils.getAsJsonObject(modelContents, "unopened"), BlockModel.class);
      IUnbakedModel opened = deserializationContext.deserialize(JSONUtils.getAsJsonObject(modelContents, "opened"), BlockModel.class);
      return new BarrelModel(opened, unopened);
    }
  }
}
