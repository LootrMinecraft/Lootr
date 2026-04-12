package noobanidus.mods.lootr.fabric.client.block;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import noobanidus.mods.lootr.common.api.LootrAPI;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

public class UnbakedBrushableModel extends noobanidus.mods.lootr.common.client.block.UnbakedBrushableModel implements CustomUnbakedBlockStateModel {
  public static final MapCodec<UnbakedBrushableModel> CODEC = getCodec(UnbakedBrushableModel::new);
  public static final Identifier IDENTIFIER = LootrAPI.rl("brushable");

  public UnbakedBrushableModel(Identifier opened, Identifier stage_0, Identifier stage_1, Identifier stage_2, Identifier stage_3, Variant.SimpleModelState state) {
    super(opened, stage_0, stage_1, stage_2, stage_3, state);
  }

  @Override
  public @NonNull MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
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
    public void emitQuads(@NonNull QuadEmitter emitter, @NonNull BlockAndTintGetter blockView, @NonNull BlockPos pos, @NonNull BlockState state, @NonNull RandomSource random, @NonNull Predicate<@Nullable Direction> cullTest) {
      boolean visuallyOpen = isOpenFromBATG(blockView, pos, state, random);
      if (visuallyOpen) {
        opened.emitQuads(emitter, blockView, pos, state, random, cullTest);
      } else {
        if (state.isAir()) {
          stage_0.emitQuads(emitter, blockView, pos, state, random, cullTest);
        } else {
          switch (state.getValue(BlockStateProperties.DUSTED)) {
            case 1 -> stage_1.emitQuads(emitter, blockView, pos, state, random, cullTest);
            case 2 -> stage_2.emitQuads(emitter, blockView, pos, state, random, cullTest);
            case 3 -> stage_3.emitQuads(emitter, blockView, pos, state, random, cullTest);
            default -> stage_0.emitQuads(emitter, blockView, pos, state, random, cullTest);
          }
        }
      }
    }

    @Override
    public @Nullable Object createGeometryKey(@NonNull BlockAndTintGetter blockView, @NonNull BlockPos pos, @NonNull BlockState state, @NonNull RandomSource random) {
      return internalCreateObjectKey(blockView, pos, state, random);
    }

    @Override
    public Material.@NonNull Baked particleMaterial(@NonNull BlockAndTintGetter blockView, @NonNull BlockPos pos, @NonNull BlockState state) {
      if (isOpenFromBATG(blockView, pos, state, null)) {
        return opened.particleMaterial(blockView, pos, state);
      }
      return switch (state.getValue(BlockStateProperties.DUSTED)) {
        case 1 -> stage_1.particleMaterial(blockView, pos, state);
        case 2 -> stage_2.particleMaterial(blockView, pos, state);
        case 3 -> stage_3.particleMaterial(blockView, pos, state);
        default -> stage_0.particleMaterial(blockView, pos, state);
      };
    }

    @Override
    public Material.@NonNull Baked particleMaterial() {
      return stage_0.particleMaterial();
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
      return stage_0.materialFlags();
    }
  }
}
