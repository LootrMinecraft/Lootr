package noobanidus.mods.lootr.common.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ChestBlock;
import noobanidus.mods.lootr.common.api.LootrTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public class MixinChestBlock {
  @Inject(method = "isBlockedChestByBlock", at = @At("HEAD"), cancellable = true)
  private static void LootrIsChestBlocked(BlockGetter blockGetter, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
    if (blockGetter.getBlockState(blockPos.above()).is(LootrTags.Blocks.NON_BLOCKING)) {
      cir.setReturnValue(false);
      cir.cancel();
    }
  }
}
