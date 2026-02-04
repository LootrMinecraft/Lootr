package noobanidus.mods.lootr.common.client.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import noobanidus.mods.lootr.common.api.LootrAPI;
import org.jspecify.annotations.NonNull;

import java.util.List;

public abstract class UnbakedBrushableModel implements BlockStateModel.Unbaked {
  public static <T extends UnbakedBrushableModel> MapCodec<T> getCodec(Provider<T> provider) {
    return RecordCodecBuilder.mapCodec(instance ->
        instance.group(Identifier.CODEC.fieldOf("opened")
            .forGetter(UnbakedBrushableModel::getOpened), Identifier.CODEC.fieldOf("stage_0")
            .forGetter(UnbakedBrushableModel::getStage0), Identifier.CODEC.fieldOf("stage_1")
            .forGetter(UnbakedBrushableModel::getStage1), Identifier.CODEC.fieldOf("stage_2")
            .forGetter(UnbakedBrushableModel::getStage2), Identifier.CODEC.fieldOf("stage_3")
            .forGetter(UnbakedBrushableModel::getStage3), Variant.SimpleModelState.MAP_CODEC.fieldOf("state")
            .forGetter(UnbakedBrushableModel::getState)
        ).apply(instance, provider::create));
  }

  @FunctionalInterface
  public interface Provider<T extends UnbakedBrushableModel> {
    T create(Identifier opened, Identifier stage_0, Identifier stage_1, Identifier stage_2, Identifier stage_3, Variant.SimpleModelState state);
  }

  @FunctionalInterface
  public interface Baker {
    BlockStateModel bake(BlockStateModel opened, BlockStateModel stage_0, BlockStateModel stage_1, BlockStateModel stage_2, BlockStateModel stage_3);
  }

  protected final Identifier opened, stage_0, stage_1, stage_2, stage_3;
  protected final Variant.SimpleModelState state;

  public UnbakedBrushableModel(Identifier opened, Identifier stage_0, Identifier stage_1, Identifier stage_2, Identifier stage_3, Variant.SimpleModelState state) {
    this.opened = opened;
    this.stage_0 = stage_0;
    this.stage_1 = stage_1;
    this.stage_2 = stage_2;
    this.stage_3 = stage_3;
    this.state = state;
  }

  public Identifier getOpened() {
    return opened;
  }

  public Identifier getStage0() {
    return stage_0;
  }

  public Identifier getStage1() {
    return stage_1;
  }

  public Identifier getStage2() {
    return stage_2;
  }

  public Identifier getStage3() {
    return stage_3;
  }

  public Variant.SimpleModelState getState() {
    return state;
  }

  protected abstract Baker getBaker();

  @Override
  public BlockStateModel bake(ModelBaker baker) {
    return getBaker().bake(
        new SingleVariant(SimpleModelWrapper.bake(baker, opened, state.asModelState())),
        new SingleVariant(SimpleModelWrapper.bake(baker, stage_0, state.asModelState())),
        new SingleVariant(SimpleModelWrapper.bake(baker, stage_1, state.asModelState())),
        new SingleVariant(SimpleModelWrapper.bake(baker, stage_2, state.asModelState())),
        new SingleVariant(SimpleModelWrapper.bake(baker, stage_3, state.asModelState()))
    );
  }

  @Override
  public void resolveDependencies(Resolver resolver) {
    resolver.markDependency(opened);
    resolver.markDependency(stage_0);
    resolver.markDependency(stage_1);
    resolver.markDependency(stage_2);
    resolver.markDependency(stage_3);
  }

  public abstract static class Baked implements BlockStateModel {
    protected final BlockStateModel opened, stage_0, stage_1, stage_2, stage_3;

    public Baked(BlockStateModel opened, BlockStateModel stage_0, BlockStateModel stage_1, BlockStateModel stage_2, BlockStateModel stage_3) {
      this.opened = opened;
      this.stage_0 = stage_0;
      this.stage_1 = stage_1;
      this.stage_2 = stage_2;
      this.stage_3 = stage_3;
    }

    @Override
    public void collectParts(@NonNull RandomSource random, @NonNull List<BlockModelPart> output) {
      this.stage_0.collectParts(random, output);
    }

    @Override
    public TextureAtlasSprite particleIcon() {
      return opened.particleIcon();
    }

    protected abstract boolean isOpenFromBATG(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random);

    public Object internalCreateObjectKey(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
      if (isOpenFromBATG(level, pos, state, random)) {
        return 4;
      } else {
        return state.getValue(BlockStateProperties.DUSTED);
      }
    }
  }
}
