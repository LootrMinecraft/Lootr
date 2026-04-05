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
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.neoforge.init.ModBlockProperties;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class UnbakedCustomModel extends noobanidus.mods.lootr.common.client.block.UnbakedCustomModel implements CustomUnbakedBlockStateModel {
  public static final MapCodec<UnbakedCustomModel> CODEC = getCodec(UnbakedCustomModel::new);

  public UnbakedCustomModel(Identifier opened, Identifier unopened, Identifier vanilla, Variant.SimpleModelState state, boolean open) {
    super(opened, unopened, vanilla, state, open);
  }

  public UnbakedCustomModel withState (Variant.SimpleModelState modelState) {
    return new UnbakedCustomModel(this.opened, this.unopened, this.vanilla, modelState, open);
  }

  @Override
  protected Baker getBaker() {
    return Baked::new;
  }

  @Override
  public @NonNull MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
    return CODEC;
  }

  public static class Baked extends noobanidus.mods.lootr.common.client.block.UnbakedCustomModel.Baked implements DynamicBlockStateModel {

    public Baked(BlockStateModel unopened, BlockStateModel opened, BlockStateModel vanilla, boolean open) {
      super(unopened, opened, vanilla, open);
    }

    @Override
    protected boolean isOpenFromBATG(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
      return level.getModelData(pos).get(ModBlockProperties.OPENED) == Boolean.TRUE;
    }

    @Override
    public void collectParts(@NonNull BlockAndTintGetter blockAndTintGetter, @NonNull BlockPos blockPos, @NonNull BlockState blockState, @NonNull RandomSource randomSource, @NonNull List<BlockStateModelPart> list) {
      if (LootrAPI.isVanillaTextures()) {
        vanilla.collectParts(blockAndTintGetter, blockPos, blockState, randomSource, list);
        return;
      }

      if (blockState.isAir()) {
        if (!LootrAPI.isVanillaTextures()) {
          unopened.collectParts(blockAndTintGetter, blockPos, blockState, randomSource, list);
        }
        return;
      }

      boolean visuallyOpen = isOpenFromBATG(blockAndTintGetter, blockPos, blockState, randomSource);

      if (visuallyOpen) {
        opened.collectParts(blockAndTintGetter, blockPos, blockState, randomSource, list);
      } else {
        unopened.collectParts(blockAndTintGetter, blockPos, blockState, randomSource, list);
      }
    }

    @Override
    public @Nullable Object createGeometryKey(@NonNull BlockAndTintGetter level, @NonNull BlockPos pos, @NonNull BlockState state, @NonNull RandomSource random) {
      return internalCreateObjectKey(level, pos, state, random);
    }

    @Override
    public Material.@NonNull Baked particleMaterial() {
      return opened.particleMaterial();
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
      return opened.materialFlags();
    }
  }
}
