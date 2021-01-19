package noobanidus.mods.lootr.mixins;

import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTables;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.feature.structure.IglooPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(IglooPieces.Piece.class)
public class MixinIglooPieces$Piece {
  @Inject(method = "Lnet/minecraft/world/gen/feature/structure/IglooPieces$Piece;handleDataMarker(Ljava/lang/String;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/IServerWorld;Ljava/util/Random;Lnet/minecraft/util/math/MutableBoundingBox;)V",
      at = @At(value = "HEAD"),
      cancellable = true)
  protected void handleDataMarker(String function, BlockPos pos, IServerWorld worldIn, Random rand, MutableBoundingBox sbb, CallbackInfo info) {
    if ("chest".equals(function)) {
      worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
      LockableLootTileEntity.setLootTable(worldIn, rand, pos.down(), LootTables.CHESTS_IGLOO_CHEST);
      info.cancel();
    }
  }
}
