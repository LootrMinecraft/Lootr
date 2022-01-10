package noobanidus.mods.lootr.client.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.CompositeModelState;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import noobanidus.mods.lootr.block.LootrBarrelBlock;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class BarrelModel implements IModelGeometry<BarrelModel> {
  private final UnbakedModel opened;
  private final UnbakedModel unopened;

  public BarrelModel(UnbakedModel opened, UnbakedModel unopened) {
    this.opened = opened;
    this.unopened = unopened;
  }

  @Override
  public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
    Set<Material> materials = Sets.newHashSet();
    materials.add(owner.resolveTexture("particle"));
    materials.addAll(unopened.getMaterials(modelGetter, missingTextureErrors));
    materials.addAll(opened.getMaterials(modelGetter, missingTextureErrors));
    return materials;
  }

  private static BakedModel buildModel(UnbakedModel entry, ModelState modelTransform, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ResourceLocation modelLocation) {
    return entry.bake(bakery, spriteGetter, modelTransform, modelLocation);
  }

  @Override
  public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
    return new BarrelBakedModel(owner.useSmoothLighting(), owner.isShadedInGui(), owner.isSideLit(),
        spriteGetter.apply(owner.resolveTexture("particle")), overrides,
        buildModel(opened, modelTransform, bakery, spriteGetter, modelLocation),
        buildModel(unopened, modelTransform, bakery, spriteGetter, modelLocation),
        PerspectiveMapWrapper.getTransforms(new CompositeModelState(owner.getCombinedTransform(), modelTransform))
    );
  }

  private static final class BarrelBakedModel implements IDynamicBakedModel {
    private final BakedModel opened;
    private final BakedModel unopened;
    private final ImmutableMap<ItemTransforms.TransformType, Transformation> cameraTransforms;
    protected final boolean ambientOcclusion;
    protected final boolean gui3d;
    protected final boolean isSideLit;
    protected final TextureAtlasSprite particle;
    protected final ItemOverrides overrides;

    public BarrelBakedModel(boolean ambientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrides overrides, BakedModel opened, BakedModel unopened, ImmutableMap<ItemTransforms.TransformType, Transformation> cameraTransforms) {
      this.isSideLit = isSideLit;
      this.cameraTransforms = cameraTransforms;
      this.ambientOcclusion = ambientOcclusion;
      this.gui3d = isGui3d;
      this.particle = particle;
      this.overrides = overrides;
      this.opened = opened;
      this.unopened = unopened;
    }

    
    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side,  Random rand,  IModelData extraData) {
      BakedModel model;
      if (extraData.hasProperty(LootrBarrelBlock.OPENED)) {
        if (extraData.getData(LootrBarrelBlock.OPENED) == Boolean.TRUE) {
          model = opened;
        } else {
          model = unopened;
        }
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
    public TextureAtlasSprite getParticleIcon( IModelData data) {
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
    public BakedModel handlePerspective(ItemTransforms.TransformType cameraTransformType, PoseStack mat) {
      return PerspectiveMapWrapper.handlePerspective(this, cameraTransforms, cameraTransformType, mat);
    }

    @Override
    public ItemOverrides getOverrides() {
      return ItemOverrides.EMPTY;
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
    public void onResourceManagerReload(ResourceManager resourceManager) {

    }

    @Override
    public BarrelModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
      UnbakedModel unopened = deserializationContext.deserialize(GsonHelper.getAsJsonObject(modelContents, "unopened"), BlockModel.class);
      UnbakedModel opened = deserializationContext.deserialize(GsonHelper.getAsJsonObject(modelContents, "opened"), BlockModel.class);
      return new BarrelModel(opened, unopened);
    }
  }
}
