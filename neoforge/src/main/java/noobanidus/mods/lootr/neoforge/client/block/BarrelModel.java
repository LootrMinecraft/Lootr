package noobanidus.mods.lootr.neoforge.client.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.ExtendedUnbakedModel;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.StandardModelParameters;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;
import net.neoforged.neoforge.client.model.data.ModelData;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.neoforge.init.ModBlockProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BarrelModel implements ExtendedUnbakedModel {
  private final ResourceLocation opened;
  private final ResourceLocation unopened;
  private final ResourceLocation vanilla;
  private final ResourceLocation old_opened;
  private final ResourceLocation old_unopened;
  private final StandardModelParameters parameters;

  public BarrelModel(StandardModelParameters parameters, ResourceLocation opened, ResourceLocation unopened, ResourceLocation vanilla, ResourceLocation old_unopened, ResourceLocation old_opened) {
    this.parameters = parameters;
    this.opened = opened;
    this.unopened = unopened;
    this.vanilla = vanilla;
    this.old_opened = old_opened;
    this.old_unopened = old_unopened;
  }

  @Nullable
  @Override
  public Boolean getAmbientOcclusion() {
    return parameters.ambientOcclusion();
  }

  @Nullable
  @Override
  public GuiLight getGuiLight() {
    return parameters.guiLight();
  }

  @Nullable
  @Override
  public ItemTransforms getTransforms() {
    return parameters.itemTransforms();
  }

  @Override
  public TextureSlots.Data getTextureSlots() {
    return parameters.textures();
  }

  private static BakedModel buildModel(UnbakedModel entry, TextureSlots textures, ModelBaker baker, ModelState modelState, boolean useAmbientOcclusion, boolean usesBlockLight, ItemTransforms itemTransforms, ContextMap additionalProperties) {
    return entry.bake(textures, baker, modelState, useAmbientOcclusion, usesBlockLight, itemTransforms, additionalProperties);
  }

  @Override
  public BakedModel bake(TextureSlots textures, ModelBaker baker, ModelState modelState, boolean useAmbientOcclusion, boolean usesBlockLight, ItemTransforms itemTransforms, ContextMap additionalProperties) {
    return new BarrelBakedModel(useAmbientOcclusion, usesBlockLight, textures.getMaterial("particle"),
        baker.bake(opened, modelState),
        baker.bake(unopened, modelState),
        baker.bake(vanilla, modelState),
        baker.bake(old_opened, modelState),
        baker.bake(old_unopened, modelState),
        itemTransforms);

  }

  @Override
  public void resolveDependencies(UnbakedModel.Resolver modelGetter) {
    modelGetter.resolve(opened);
    modelGetter.resolve(unopened);
    modelGetter.resolve(vanilla);
    modelGetter.resolve(old_opened);
    modelGetter.resolve(old_unopened);
  }

  private static final class BarrelBakedModel implements IDynamicBakedModel {
    private final boolean ambientOcclusion;
    private final boolean isSideLit;
    private final Material particle;
    private final BakedModel opened;
    private final BakedModel unopened;
    private final BakedModel vanilla;
    private final BakedModel old_opened;
    private final BakedModel old_unopened;
    private final ItemTransforms cameraTransforms;

    public BarrelBakedModel(boolean ambientOcclusion, boolean isSideLit, Material particle, BakedModel opened, BakedModel unopened, BakedModel vanilla, BakedModel old_opened, BakedModel old_unopened, ItemTransforms cameraTransforms) {
      this.isSideLit = isSideLit;
      this.cameraTransforms = cameraTransforms;
      this.ambientOcclusion = ambientOcclusion;
      this.particle = particle;
      this.opened = opened;
      this.unopened = unopened;
      this.vanilla = vanilla;
      this.old_opened = old_opened;
      this.old_unopened = old_unopened;
    }


    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @NotNull RenderType renderType) {
      BakedModel model;
      if (LootrAPI.isVanillaTextures()) {
        model = vanilla;
      } else {
        if (extraData.has(ModBlockProperties.OPENED)) {
          if (extraData.get(ModBlockProperties.OPENED) == Boolean.TRUE) {
            model = LootrAPI.isOldTextures() ? old_opened : opened;
          } else {
            model = LootrAPI.isOldTextures() ? old_unopened : unopened;
          }
        } else {
          model = LootrAPI.isOldTextures() ? old_unopened : unopened;
        }
      }
      return model.getQuads(state, side, rand, extraData, renderType);
    }

    @Override
    public boolean useAmbientOcclusion() {
      return ambientOcclusion;
    }

    @Override
    public boolean isGui3d() {
      return true;
    }

    @Override
    public boolean usesBlockLight() {
      return isSideLit;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
      return particle.sprite();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
      if (LootrAPI.isVanillaTextures()) {
        return vanilla.getParticleIcon();
      }
      if (data.get(ModBlockProperties.OPENED) == Boolean.TRUE) {
        return LootrAPI.isOldTextures() ? old_opened.getParticleIcon() : opened.getParticleIcon();
      } else {
        return LootrAPI.isOldTextures() ? old_unopened.getParticleIcon() : unopened.getParticleIcon();
      }
    }

    @Override
    public ItemTransforms getTransforms() {
      return cameraTransforms;
    }
  }

  public static final class Loader implements UnbakedModelLoader<BarrelModel> {
    public static final Loader INSTANCE = new Loader();

    private Loader() {
    }

    @Override
    public BarrelModel read(JsonObject modelContents, JsonDeserializationContext deserializationContext) {
      ResourceLocation opened = ResourceLocation.parse(GsonHelper.getAsJsonObject(modelContents, "opened").get("parent").getAsString());
      ResourceLocation unopened = ResourceLocation.parse(GsonHelper.getAsJsonObject(modelContents, "unopened").get("parent").getAsString());
      ResourceLocation vanilla = ResourceLocation.parse(GsonHelper.getAsJsonObject(modelContents, "vanilla").get("parent").getAsString());
      ResourceLocation old_unopened = ResourceLocation.parse(GsonHelper.getAsJsonObject(modelContents, "old_unopened").get("parent").getAsString());
      ResourceLocation old_opened = ResourceLocation.parse(GsonHelper.getAsJsonObject(modelContents, "old_opened").get("parent").getAsString());
      return new BarrelModel(StandardModelParameters.parse(modelContents, deserializationContext), opened, unopened, vanilla, old_unopened, old_opened);
    }
  }
}
