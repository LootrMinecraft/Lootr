package noobanidus.mods.lootr.neoforge.client.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import noobanidus.mods.lootr.neoforge.init.ModBlockProperties;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class UnbakedBrushableModel extends noobanidus.mods.lootr.common.client.block.UnbakedBrushableModel implements CustomUnbakedBlockStateModel {
  public static final MapCodec<UnbakedBrushableModel> CODEC = getCodec(UnbakedBrushableModel::new);

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

  public static class Baked extends noobanidus.mods.lootr.common.client.block.UnbakedBrushableModel.Baked implements DynamicBlockStateModel {
    public Baked(BlockStateModel opened, BlockStateModel stage_0, BlockStateModel stage_1, BlockStateModel stage_2, BlockStateModel stage_3) {
      super(opened, stage_0, stage_1, stage_2, stage_3);
    }

    @Override
    public void collectParts(@NonNull BlockAndTintGetter blockAndTintGetter, @NonNull BlockPos blockPos, @NonNull BlockState blockState, @NonNull RandomSource randomSource, @NonNull List<BlockStateModelPart> list) {
      if (isOpenFromBATG(blockAndTintGetter, blockPos, blockState, randomSource)) {
        opened.collectParts(blockAndTintGetter, blockPos, blockState, randomSource, list);
        return;
      }
      int stage = blockState.isAir() ? 0 : blockState.getValue(BlockStateProperties.DUSTED);
      switch (stage) {
        case 1 -> stage_1.collectParts(blockAndTintGetter, blockPos, blockState, randomSource, list);
        case 2 -> stage_2.collectParts(blockAndTintGetter, blockPos, blockState, randomSource, list);
        case 3 -> stage_3.collectParts(blockAndTintGetter, blockPos, blockState, randomSource, list);
        default -> stage_0.collectParts(blockAndTintGetter, blockPos, blockState, randomSource, list);
      }
    }

    @Override
    protected boolean isOpenFromBATG(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
      return level.getModelData(pos).get(ModBlockProperties.OPENED) == Boolean.TRUE;
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
