package noobanidus.mods.lootr.fabric.client.block;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
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
import java.util.function.Supplier;

public class BakedBarrelModel extends BakedBarrelModelBase implements FabricBakedModel {
  public BakedBarrelModel(boolean ambientOcclusion, boolean isSideLit, Material particle, BakedModel opened, BakedModel unopened, BakedModel vanilla, BakedModel old_opened, BakedModel old_unopened, ItemTransforms cameraTransforms) {
    super(ambientOcclusion, isSideLit, particle, opened, unopened, vanilla, old_opened, old_unopened, cameraTransforms);
  }

  @Override
  public boolean isVanillaAdapter() {
    return false;
  }

  @Override
  public void emitBlockQuads(QuadEmitter emitter, BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, Predicate<@Nullable Direction> cullTest) {
    Object data = blockView.getBlockEntityRenderData(pos);
    BakedModel model = LootrAPI.isOldTextures() ? old_unopened : unopened;
    if (LootrAPI.isVanillaTextures()) {
      model = vanilla;
    } else if (data == Boolean.TRUE) {
      model = LootrAPI.isOldTextures() ? old_opened : opened;
    }

    model.emitBlockQuads(emitter, blockView, state, pos, randomSupplier, cullTest);
  }
}
