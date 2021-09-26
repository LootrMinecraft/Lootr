package noobanidus.mods.lootr.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CatSitOnBlockGoal.class)
public class MixinCatSitOnBlockGoal {
  @Redirect(method = "Lnet/minecraft/entity/ai/goal/CatSitOnBlockGoal;shouldMoveTo(Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/BlockPos;)Z",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isIn(Lnet/minecraft/block/Block;)Z"))
  protected boolean isIn(BlockState state, Block block) {
    return state.is(block) || state.is(ModBlocks.CHEST);
  }

  @Inject(method = "Lnet/minecraft/entity/ai/goal/CatSitOnBlockGoal;shouldMoveTo(Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/BlockPos;)Z", at = @At(target = "Lnet/minecraft/tileentity/ChestTileEntity;getPlayersUsing(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;)I", value = "INVOKE"), cancellable = true)
  protected void playersUsing(IWorldReader reader, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
    if (SpecialLootChestTile.getPlayersUsing(reader, pos) < 1) {
      info.setReturnValue(true);
      info.cancel();
    }
  }
}
