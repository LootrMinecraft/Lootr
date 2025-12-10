package noobanidus.mods.lootr.neoforge.client.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.neoforge.init.ModBlockProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class CustomBarrelModel implements IUnbakedGeometry<CustomBarrelModel> {
  private final UnbakedModel opened;
  private final UnbakedModel unopened;
  @Nullable
  private final UnbakedModel vanilla;

  public CustomBarrelModel(UnbakedModel opened, UnbakedModel unopened, UnbakedModel vanilla) {
    this.opened = opened;
    this.unopened = unopened;
    this.vanilla = vanilla;
  }

  private static BakedModel buildModel(UnbakedModel entry, ModelState modelTransform, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter) {
    return entry.bake(bakery, spriteGetter, modelTransform);
  }

  @Override
  public BakedModel bake(IGeometryBakingContext context, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides) {
    return new CustomBarrelBakedModel(context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(),
        spriteGetter.apply(context.getMaterial("particle")), overrides,
        buildModel(opened, modelTransform, bakery, spriteGetter),
        buildModel(unopened, modelTransform, bakery, spriteGetter),
        vanilla == null ? null : buildModel(vanilla, modelTransform, bakery, spriteGetter),
        context.getTransforms()
    );
  }

  @Override
  public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
    opened.resolveParents(modelGetter);
    unopened.resolveParents(modelGetter);
    if (vanilla != null) {
      vanilla.resolveParents(modelGetter);
    }
  }

  private static final class CustomBarrelBakedModel implements IDynamicBakedModel {
    private final boolean ambientOcclusion;
    private final boolean gui3d;
    private final boolean isSideLit;
    private final TextureAtlasSprite particle;
    private final ItemOverrides overrides;
    private final BakedModel opened;
    private final BakedModel unopened;
    private final BakedModel vanilla;
    private final ItemTransforms cameraTransforms;

    public CustomBarrelBakedModel(boolean ambientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrides overrides, BakedModel opened, BakedModel unopened, @Nullable
    BakedModel vanilla, ItemTransforms cameraTransforms) {
      this.isSideLit = isSideLit;
      this.cameraTransforms = cameraTransforms;
      this.ambientOcclusion = ambientOcclusion;
      this.gui3d = isGui3d;
      this.particle = particle;
      this.overrides = overrides;
      this.opened = opened;
      this.unopened = unopened;
      this.vanilla = vanilla;
    }


    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @NotNull RenderType renderType) {
      BakedModel model;
      if (LootrAPI.isVanillaTextures() && vanilla != null) {
        model = vanilla;
      } else {
        if (extraData.has(ModBlockProperties.OPENED)) {
          if (extraData.get(ModBlockProperties.OPENED) == Boolean.TRUE) {
            model = opened;
          } else {
            model = unopened;
          }
        } else {
          model = unopened;
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
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
      if (LootrAPI.isVanillaTextures() && vanilla != null) {
        return vanilla.getParticleIcon(data);
      }
      if (data.get(ModBlockProperties.OPENED) == Boolean.TRUE) {
        return opened.getParticleIcon(data);
      } else {
        return unopened.getParticleIcon(data);
      }
    }

    @Override
    public ItemTransforms getTransforms() {
      return cameraTransforms;
    }

    @Override
    public ItemOverrides getOverrides() {
      return overrides;
    }
  }

  public static final class Loader implements IGeometryLoader<CustomBarrelModel> {
    public static final Loader INSTANCE = new Loader();

    private Loader() {
    }

    @Override
    public CustomBarrelModel read(JsonObject modelContents, JsonDeserializationContext deserializationContext) {
      UnbakedModel unopened = deserializationContext.deserialize(GsonHelper.getAsJsonObject(modelContents, "unopened"), BlockModel.class);
      UnbakedModel opened = deserializationContext.deserialize(GsonHelper.getAsJsonObject(modelContents, "opened"), BlockModel.class);
      UnbakedModel vanilla = null;
      if (modelContents.has("vanilla")) {
        vanilla = deserializationContext.deserialize(GsonHelper.getAsJsonObject(modelContents, "vanilla"), BlockModel.class);
      }
      return new CustomBarrelModel(opened, unopened, vanilla);
    }
  }
}