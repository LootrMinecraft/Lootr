package noobanidus.mods.lootr.common.mixin.cat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CatSitOnBlockGoal.class)
public class MixinCatSitOnBlockGoal {
  @Inject(method = "isValidTarget", at = @At("HEAD"), cancellable = true)
  protected void LootrPlayersUsing(LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
    if (level.isEmptyBlock(pos.above())) {
      BlockEntity blockEntity = level.getBlockEntity(pos);
      if (LootrAPI.wrapBlockEntity(blockEntity) instanceof ILootrBlockEntity lootrBlockEntity) {
        if (lootrBlockEntity.getPhysicalOpenerCount() < 1) {
          info.setReturnValue(true);
          info.cancel();
        }
      }
    }
  }
}
