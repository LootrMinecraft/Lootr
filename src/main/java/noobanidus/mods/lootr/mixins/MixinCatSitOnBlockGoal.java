package noobanidus.mods.lootr.mixins;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import noobanidus.mods.lootr.blocks.entities.LootrChestBlockEntity;
import noobanidus.mods.lootr.init.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CatSitOnBlockGoal.class)
public class MixinCatSitOnBlockGoal {
  @Redirect(method = "isValidTarget",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
  protected boolean isIn(BlockState state, Block block) {
    return state.is(block) || state.is(ModBlocks.CHEST);
  }

  @Inject(method = "isValidTarget", at = @At(target = "Lnet/minecraft/world/level/block/entity/ChestBlockEntity;getOpenCount(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)I", value = "INVOKE"), cancellable = true)
  protected void playersUsing(LevelReader reader, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
    if (LootrChestBlockEntity.getOpenCount(reader, pos) < 1) {
      info.setReturnValue(true);
      info.cancel();
    }
  }
}
