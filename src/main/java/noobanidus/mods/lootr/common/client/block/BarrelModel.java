package noobanidus.mods.lootr.common.client.block;

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
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.common.block.LootrBarrelBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class BarrelModel implements IUnbakedGeometry<BarrelModel> {
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

  private static BakedModel buildModel(UnbakedModel entry, ModelState modelTransform, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter) {
    return entry.bake(bakery, spriteGetter, modelTransform);
  }

  @Override
  public BakedModel bake(IGeometryBakingContext context, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides) {
    return new BarrelBakedModel(context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(),
        spriteGetter.apply(context.getMaterial("particle")), overrides,
        buildModel(opened, modelTransform, bakery, spriteGetter),
        buildModel(unopened, modelTransform, bakery, spriteGetter),
        buildModel(vanilla, modelTransform, bakery, spriteGetter),
        buildModel(old_opened, modelTransform, bakery, spriteGetter),
        buildModel(old_unopened, modelTransform, bakery, spriteGetter),
        context.getTransforms()
    );
  }

  @Override
  public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
    opened.resolveParents(modelGetter);
    unopened.resolveParents(modelGetter);
    vanilla.resolveParents(modelGetter);
    old_opened.resolveParents(modelGetter);
    old_unopened.resolveParents(modelGetter);
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
    private final ItemTransforms cameraTransforms;

    public BarrelBakedModel(boolean ambientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrides overrides, BakedModel opened, BakedModel unopened, BakedModel vanilla, BakedModel old_opened, BakedModel old_unopened, ItemTransforms cameraTransforms) {
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


    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @NotNull RenderType renderType) {
      BakedModel model;
      if (LootrAPI.isVanillaTextures()) {
        model = vanilla;
      } else {
        if (extraData.has(LootrBarrelBlock.OPENED)) {
          if (extraData.get(LootrBarrelBlock.OPENED) == Boolean.TRUE) {
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
      if (LootrAPI.isVanillaTextures()) {
        return vanilla.getParticleIcon();
      }
      if (data.get(LootrBarrelBlock.OPENED) == Boolean.TRUE) {
        return LootrAPI.isOldTextures() ? old_opened.getParticleIcon() : opened.getParticleIcon();
      } else {
        return LootrAPI.isOldTextures() ? old_unopened.getParticleIcon() : unopened.getParticleIcon();
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

  public static final class Loader implements IGeometryLoader<BarrelModel> {
    public static final Loader INSTANCE = new Loader();

    private Loader() {
    }

    @Override
    public BarrelModel read(JsonObject modelContents, JsonDeserializationContext deserializationContext) {
      UnbakedModel unopened = deserializationContext.deserialize(GsonHelper.getAsJsonObject(modelContents, "unopened"), BlockModel.class);
      UnbakedModel opened = deserializationContext.deserialize(GsonHelper.getAsJsonObject(modelContents, "opened"), BlockModel.class);
      UnbakedModel vanilla = deserializationContext.deserialize(GsonHelper.getAsJsonObject(modelContents, "vanilla"), BlockModel.class);
      UnbakedModel old_unopened = deserializationContext.deserialize(GsonHelper.getAsJsonObject(modelContents, "old_unopened"), BlockModel.class);
      UnbakedModel old_opened = deserializationContext.deserialize(GsonHelper.getAsJsonObject(modelContents, "old_opened"), BlockModel.class);
      return new BarrelModel(opened, unopened, vanilla, old_unopened, old_opened);
    }
  }
}
