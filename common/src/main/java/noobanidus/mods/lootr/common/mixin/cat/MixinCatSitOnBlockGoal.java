package noobanidus.mods.lootr.common.mixin.cat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CatSitOnBlockGoal.class)
public class MixinCatSitOnBlockGoal {
  @WrapOperation(method = "isValidTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
  protected boolean LootrIsIn(BlockState instance, Object o, Operation<Boolean> original) {
    var result = original.call(instance, o);

    if (o.equals(Blocks.CHEST) && LootrRegistry.isReady()) {
      return result || instance.is(LootrTags.Blocks.CATS_CAN_BLOCK);
    }

    return result;
  }

  @Inject(method = "isValidTarget", at = @At(target = "Lnet/minecraft/world/level/block/entity/ChestBlockEntity;getOpenCount(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)I", value = "INVOKE"), cancellable = true)
  protected void LootrPlayersUsing(LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (LootrAPI.wrapBlockEntity(blockEntity) instanceof ILootrBlockEntity lootrBlockEntity) {
      if (lootrBlockEntity.getPhysicalOpenerCount() < 1) {
        info.setReturnValue(true);
        info.cancel();
      }
    }
  }

  // The rest of this is handled in ChestBlock::isCatSittingOnChest
}
