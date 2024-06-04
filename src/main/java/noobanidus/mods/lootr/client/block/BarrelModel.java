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
import noobanidus.mods.lootr.config.ConfigManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class BarrelModel implements IModelGeometry<BarrelModel> {
  private final UnbakedModel opened;
  private final UnbakedModel unopened;
  private final UnbakedModel vanilla;
  private final UnbakedModel old_opened;
  private final UnbakedModel old_unopened;

  public BarrelModel(UnbakedModel opened, UnbakedModel unopened, UnbakedModel vanilla, UnbakedModel old_unopened, UnbakedModel old_opened) {
    this.opened = opened;
    this.unopened = unopened;
    this.vanilla = vanilla;
    this.old_opened = old_opened;
    this.old_unopened = old_unopened;
  }

  @Override
  public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
    Set<Material> materials = Sets.newHashSet();
    materials.add(owner.resolveTexture("particle"));
    materials.addAll(unopened.getMaterials(modelGetter, missingTextureErrors));
    materials.addAll(opened.getMaterials(modelGetter, missingTextureErrors));
    materials.addAll(vanilla.getMaterials(modelGetter, missingTextureErrors));
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
        buildModel(vanilla, modelTransform, bakery, spriteGetter, modelLocation),
        buildModel(old_opened, modelTransform, bakery, spriteGetter, modelLocation),
        buildModel(old_unopened, modelTransform, bakery, spriteGetter, modelLocation),
        PerspectiveMapWrapper.getTransforms(new CompositeModelState(owner.getCombinedTransform(), modelTransform))
    );
  }

  private static final class BarrelBakedModel implements IDynamicBakedModel {
    private final boolean ambientOcclusion;
    private final boolean gui3d;
    private final boolean isSideLit;
    private final TextureAtlasSprite particle;
    private final ItemOverrides overrides;
    private final BakedModel opened;
    private final BakedModel unopened;
    private final BakedModel vanilla;
    private final BakedModel old_opened;
    private final BakedModel old_unopened;
    private final ImmutableMap<ItemTransforms.TransformType, Transformation> cameraTransforms;

    public BarrelBakedModel(boolean ambientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrides overrides, BakedModel opened, BakedModel unopened, BakedModel vanilla, BakedModel old_opened, BakedModel old_unopened, ImmutableMap<ItemTransforms.TransformType, Transformation> cameraTransforms) {
      this.isSideLit = isSideLit;
      this.cameraTransforms = cameraTransforms;
      this.ambientOcclusion = ambientOcclusion;
      this.gui3d = isGui3d;
      this.particle = particle;
      this.overrides = overrides;
      this.opened = opened;
      this.unopened = unopened;
      this.vanilla = vanilla;
      this.old_opened = old_opened;
      this.old_unopened = old_unopened;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
      BakedModel model;
      if (ConfigManager.isVanillaTextures()) {
        model = vanilla;
      } else {
        if (extraData.hasProperty(LootrBarrelBlock.OPENED)) {
          if (extraData.getData(LootrBarrelBlock.OPENED) == Boolean.TRUE) {
            model = ConfigManager.isOldTextures() ? old_opened : opened;
          } else {
            model = ConfigManager.isOldTextures() ? old_unopened : unopened;
          }
        } else {
          model = ConfigManager.isOldTextures() ? old_unopened : unopened;
        }
      }
      return model.getQuads(state, side, rand, extraData);
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
    public TextureAtlasSprite getParticleIcon(@Nonnull IModelData data) {
      if (ConfigManager.isVanillaTextures()) {
        return vanilla.getParticleIcon();
      }
      if (data.getData(LootrBarrelBlock.OPENED) == Boolean.TRUE) {
        return ConfigManager.isOldTextures() ? old_opened.getParticleIcon() : opened.getParticleIcon();
      } else {
        return ConfigManager.isOldTextures() ? old_unopened.getParticleIcon() : unopened.getParticleIcon();
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
      return overrides;
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
      UnbakedModel vanilla = deserializationContext.deserialize(GsonHelper.getAsJsonObject(modelContents, "vanilla"), BlockModel.class);
      UnbakedModel old_unopened = deserializationContext.deserialize(GsonHelper.getAsJsonObject(modelContents, "old_unopened"), BlockModel.class);
      UnbakedModel old_opened = deserializationContext.deserialize(GsonHelper.getAsJsonObject(modelContents, "old_opened"), BlockModel.class);
      return new BarrelModel(opened, unopened, vanilla, old_unopened, old_opened);
    }
  }
}
