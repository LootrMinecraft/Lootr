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
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import noobanidus.mods.lootr.neoforge.init.ModBlockProperties;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class BrushableModel implements IUnbakedGeometry<BrushableModel> {
  private final UnbakedModel opened;
  private final UnbakedModel stage_0;
  private final UnbakedModel stage_1;
  private final UnbakedModel stage_2;
  private final UnbakedModel stage_3;

  public BrushableModel(UnbakedModel opened, UnbakedModel stage_0, UnbakedModel stage_1, UnbakedModel stage_2, UnbakedModel stage_3) {
    this.opened = opened;
    this.stage_0 = stage_0;
    this.stage_1 = stage_1;
    this.stage_2 = stage_2;
    this.stage_3 = stage_3;
  }

  @Nullable
  private static BakedModel buildModel(UnbakedModel entry, ModelState modelTransform, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter) {
    return entry.bake(bakery, spriteGetter, modelTransform);
  }

  @Override
  public BakedModel bake(IGeometryBakingContext context, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides) {
    var opened = buildModel(this.opened, modelTransform, bakery, spriteGetter);
    var stage_0 = buildModel(this.stage_0, modelTransform, bakery, spriteGetter);
    var stage_1 = buildModel(this.stage_1, modelTransform, bakery, spriteGetter);
    var stage_2 = buildModel(this.stage_2, modelTransform, bakery, spriteGetter);
    var stage_3 = buildModel(this.stage_3, modelTransform, bakery, spriteGetter);

    if (opened == null || stage_0 == null || stage_1 == null || stage_2 == null || stage_3 == null) {
      throw new IllegalStateException("BrushableModel could not bake all sub-models");
    }

    return new BrushableBakedModel(context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(),
        spriteGetter.apply(context.getMaterial("particle")), overrides,
        opened, stage_0, stage_1, stage_2, stage_3,
        context.getTransforms()
    );
  }

  @Override
  public void resolveParents(Function<Identifier, UnbakedModel> modelGetter, IGeometryBakingContext context) {
    opened.resolveParents(modelGetter);
    stage_0.resolveParents(modelGetter);
    stage_1.resolveParents(modelGetter);
    stage_2.resolveParents(modelGetter);
    stage_3.resolveParents(modelGetter);
  }

  @SuppressWarnings("deprecation")
  private record BrushableBakedModel(boolean ambientOcclusion, boolean gui3d, boolean isSideLit,
                                     TextureAtlasSprite particle, ItemOverrides overrides, BakedModel opened,
                                     BakedModel stage_0, BakedModel stage_1, BakedModel stage_2, BakedModel stage_3,
                                     ItemTransforms cameraTransforms) implements IDynamicBakedModel {

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
      BakedModel model = stage_0;

      if (extraData.has(ModBlockProperties.OPENED) && extraData.get(ModBlockProperties.OPENED) == Boolean.TRUE) {
        model = opened;
      } else if (state != null) {
        switch (state.getValue(BlockStateProperties.DUSTED)) {
          case 3 -> model = stage_3;
          case 2 -> model = stage_2;
          case 1 -> model = stage_1;
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
    public TextureAtlasSprite getParticleIcon(ModelData data) {
      return stage_0.getParticleIcon(data);
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

  public static final class Loader implements IGeometryLoader<BrushableModel> {
    public static final Loader INSTANCE = new Loader();

    private Loader() {
    }

    @Override
    public BrushableModel read(JsonObject modelContents, JsonDeserializationContext deserializationContext) {
      UnbakedModel opened = deserializationContext.deserialize(GsonHelper.getAsJsonObject(modelContents, "opened"), BlockModel.class);
      UnbakedModel stage_0 = deserializationContext.deserialize(GsonHelper.getAsJsonObject(modelContents, "stage_0"), BlockModel.class);
      UnbakedModel stage_1 = deserializationContext.deserialize(GsonHelper.getAsJsonObject(modelContents, "stage_1"), BlockModel.class);
      UnbakedModel stage_2 = deserializationContext.deserialize(GsonHelper.getAsJsonObject(modelContents, "stage_2"), BlockModel.class);
      UnbakedModel stage_3 = deserializationContext.deserialize(GsonHelper.getAsJsonObject(modelContents, "stage_3"), BlockModel.class);
      return new BrushableModel(opened, stage_0, stage_1, stage_2, stage_3);
    }
  }
}
