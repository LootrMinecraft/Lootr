package noobanidus.mods.lootr.common.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public class MixinChestBlock {
  @Inject(method = "isBlockedChestByBlock", at = @At("HEAD"), cancellable = true)
  private static void LootrIsChestBlocked(BlockGetter blockGetter, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
    BlockState thisState = blockGetter.getBlockState(blockPos);
    BlockState aboveState = blockGetter.getBlockState(blockPos.above());
    if (thisState.is(LootrTags.Blocks.CONTAINERS) && aboveState.is(LootrTags.Blocks.NON_BLOCKING)) {
      cir.setReturnValue(false);
      cir.cancel();
    }
  }
}
