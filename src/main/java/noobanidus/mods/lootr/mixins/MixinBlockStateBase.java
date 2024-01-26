package noobanidus.mods.lootr.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.LootrTags;
import noobanidus.mods.lootr.config.ConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class MixinBlockStateBase {
  @Inject(method="getDestroySpeed", at=@At("HEAD"), cancellable=true)
  private void LootrBreakSpeed (BlockGetter p_60801_, BlockPos p_60802_, CallbackInfoReturnable<Float> cir) {
    BlockBehaviour.BlockStateBase state = (BlockBehaviour.BlockStateBase) (Object) this;
    if (state.is(LootrTags.Blocks.CONTAINERS) && ConfigManager.DISABLE_BREAK.get()) {
      cir.setReturnValue(-1f);
      cir.cancel();
    }
  }
}
