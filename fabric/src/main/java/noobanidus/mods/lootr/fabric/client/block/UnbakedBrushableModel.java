package noobanidus.mods.lootr.fabric.client.block;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

public class UnbakedBrushableModel extends noobanidus.mods.lootr.common.client.block.UnbakedBrushableModel implements CustomUnbakedBlockStateModel {
  public static final MapCodec<UnbakedBrushableModel> CODEC = getCodec(UnbakedBrushableModel::new);

  public UnbakedBrushableModel(Identifier opened, Identifier stage_0, Identifier stage_1, Identifier stage_2, Identifier stage_3, Variant.SimpleModelState state) {
    super(opened, stage_0, stage_1, stage_2, stage_3, state);
  }

  @Override
  public MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
    return CODEC;
  }

  @Override
  protected Baker getBaker() {
    return Baked::new;
  }

  public static class Baked extends noobanidus.mods.lootr.common.client.block.UnbakedBrushableModel.Baked {
    public Baked(BlockStateModel opened, BlockStateModel stage_0, BlockStateModel stage_1, BlockStateModel stage_2, BlockStateModel stage_3) {
      super(opened, stage_0, stage_1, stage_2, stage_3);
    }

    @Override
    protected boolean isOpenFromBATG(BlockAndTintGetter level, BlockPos pos, BlockState state, @Nullable RandomSource random) {
      return level.getBlockEntityRenderData(pos) == Boolean.TRUE;
    }

    @Override
    public void emitQuads(QuadEmitter emitter, BlockAndTintGetter blockView, BlockPos pos, BlockState state, RandomSource random, Predicate<@Nullable Direction> cullTest) {
      boolean visuallyOpen = isOpenFromBATG(blockView, pos, state, random);
      if (visuallyOpen) {
        opened.emitQuads(emitter, blockView, pos, state, random, cullTest);
      } else {
        switch (state.getValue(BlockStateProperties.DUSTED)) {
          case 1 -> stage_1.emitQuads(emitter, blockView, pos, state, random, cullTest);
          case 2 -> stage_2.emitQuads(emitter, blockView, pos, state, random, cullTest);
          case 3 -> stage_3.emitQuads(emitter, blockView, pos, state, random, cullTest);
          default -> stage_0.emitQuads(emitter, blockView, pos, state, random, cullTest);
        }
      }
    }

    @Override
    public @Nullable Object createGeometryKey(BlockAndTintGetter blockView, BlockPos pos, BlockState state, RandomSource random) {
      return internalCreateObjectKey(blockView, pos, state, random);
    }

    @Override
    public TextureAtlasSprite particleSprite(BlockAndTintGetter blockView, BlockPos pos, BlockState state) {
      if (isOpenFromBATG(blockView, pos, state, null)) {
        return opened.particleSprite(blockView, pos, state);
      }
      return switch (state.getValue(BlockStateProperties.DUSTED)) {
        case 1 -> stage_1.particleSprite(blockView, pos, state);
        case 2 -> stage_2.particleSprite(blockView, pos, state);
        case 3 -> stage_3.particleSprite(blockView, pos, state);
        default -> stage_0.particleSprite(blockView, pos, state);
      };
    }
  }
}
