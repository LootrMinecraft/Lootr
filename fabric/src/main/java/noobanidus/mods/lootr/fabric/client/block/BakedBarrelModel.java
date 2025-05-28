package noobanidus.mods.lootr.fabric.client.block;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockStateModel;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.client.block.BakedBarrelModelBase;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class BakedBarrelModel extends BakedBarrelModelBase implements FabricBlockStateModel {
  public BakedBarrelModel(boolean ambientOcclusion, boolean isSideLit, Material particle, BlockStateModel opened, BlockStateModel unopened, BlockStateModel vanilla, BlockStateModel old_opened, BlockStateModel old_unopened, ItemTransforms cameraTransforms) {
    super(ambientOcclusion, isSideLit, particle, opened, unopened, vanilla, old_opened, old_unopened, cameraTransforms);
  }

  @Override
  public void emitQuads(QuadEmitter emitter, BlockAndTintGetter blockView, BlockPos pos,  BlockState state, RandomSource random, Predicate<@Nullable Direction> cullTest) {
    Object data = blockView.getBlockEntityRenderData(pos);
    BlockStateModel model = LootrAPI.isOldTextures() ? old_unopened : unopened;
    if (LootrAPI.isVanillaTextures()) {
      model = vanilla;
    } else if (data == Boolean.TRUE) {
      model = LootrAPI.isOldTextures() ? old_opened : opened;
    }

    model.emitQuads(emitter, blockView, pos, state, random, cullTest);
  }
}
