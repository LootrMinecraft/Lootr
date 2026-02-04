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
import noobanidus.mods.lootr.common.api.LootrAPI;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

public class UnbakedCustomModel extends noobanidus.mods.lootr.common.client.block.UnbakedCustomModel implements CustomUnbakedBlockStateModel {
  public static final MapCodec<UnbakedCustomModel> CODEC = getCodec(UnbakedCustomModel::new);

  public UnbakedCustomModel(Identifier opened, Identifier unopened, Identifier vanilla, Variant.SimpleModelState state, boolean open) {
    super(opened, unopened, vanilla, state, open);
  }

  @Override
  protected Baker getBaker() {
    return Baked::new;
  }

  @Override
  protected Provider<? extends noobanidus.mods.lootr.common.client.block.UnbakedCustomModel> getProvider() {
    return UnbakedCustomModel::new;
  }

  @Override
  public MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
    return CODEC;
  }

  public static class Baked extends noobanidus.mods.lootr.common.client.block.UnbakedCustomModel.Baked {

    public Baked(BlockStateModel unopened, BlockStateModel opened, BlockStateModel vanilla, boolean open) {
      super(unopened, opened, vanilla, open);
    }

    @Override
    protected boolean isOpenFromBATG(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
      return level.getBlockEntityRenderData(pos) == Boolean.TRUE;
    }

    @Override
    public void emitQuads(QuadEmitter emitter, BlockAndTintGetter blockView, BlockPos pos, BlockState state, RandomSource random, Predicate<@Nullable Direction> cullTest) {
      if (LootrAPI.isVanillaTextures()) {
        vanilla.emitQuads(emitter, blockView, pos, state, random, cullTest);
        return;
      }

      boolean visuallyOpen = isOpenFromBATG(blockView, pos, state, random);

      if (visuallyOpen) {
        opened.emitQuads(emitter, blockView, pos, state, random, cullTest);
      } else {
        unopened.emitQuads(emitter, blockView, pos, state, random, cullTest);
      }
    }

    @Override
    public TextureAtlasSprite particleSprite(BlockAndTintGetter blockView, BlockPos pos, BlockState state) {
      boolean visuallyOpen = isOpenFromBATG(blockView, pos, state, null);

      if (visuallyOpen) {
        return opened.particleSprite(blockView, pos, state);
      } else {
        return unopened.particleSprite(blockView, pos, state);
      }
    }

    @Override
    public @Nullable Object createGeometryKey(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
      return internalCreateObjectKey(level, pos, state, random);
    }
  }
}
