package noobanidus.mods.lootr.common.mixin.cat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CatSitOnBlockGoal.class)
public class MixinCatSitOnBlockGoal {
  @Redirect(method = "isValidTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
  protected boolean LootrIsIn(BlockState state, Object block) {
    if (LootrRegistry.isReady()) {
      return state.is((Block)block) || state.is(LootrTags.Blocks.CATS_CAN_BLOCK);
    } else {
      return state.is((Block)block);
    }
  }

  @Inject(method = "isValidTarget", at = @At(target = "Lnet/minecraft/world/level/block/entity/ChestBlockEntity;getOpenCount(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)I", value = "INVOKE"), cancellable = true)
  protected void LootrPlayersUsing(LevelReader reader, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
    BlockEntity blockEntity = reader.getBlockEntity(pos);
    if (LootrAPI.wrapBlockEntity(blockEntity) instanceof ILootrBlockEntity lootrBlockEntity) {
      if (lootrBlockEntity.getPhysicalOpenerCount() < 1) {
        info.setReturnValue(true);
        info.cancel();
      }
    }
  }

  // The rest of this is handled in ChestBlock::isCatSittingOnChest
}
