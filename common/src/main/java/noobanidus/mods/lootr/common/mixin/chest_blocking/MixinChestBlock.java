package noobanidus.mods.lootr.common.mixin.chest_blocking;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChestBlock.class)
public class MixinChestBlock {
  @WrapOperation(method="isBlockedChestByBlock", at=@At(value="INVOKE", target="Lnet/minecraft/world/level/block/state/BlockState;isRedstoneConductor(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z"))
  private static boolean LootrIsChestBlocked(BlockState instance, BlockGetter blockGetter, BlockPos blockPos, Operation<Boolean> original, @Local(argsOnly = true) BlockPos pos) {
    if (instance.is(LootrTags.Blocks.NON_BLOCKING)) {
      BlockState blockState = blockGetter.getBlockState(pos);
      if (blockState.is(LootrTags.Blocks.CONTAINERS)) {
        return false;
      }
    }
    return original.call(instance, blockGetter, blockPos);
  }
}
