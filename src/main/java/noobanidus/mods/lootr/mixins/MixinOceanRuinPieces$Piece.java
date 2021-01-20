package noobanidus.mods.lootr.mixins;

import net.minecraft.block.Block;
import net.minecraft.world.gen.feature.structure.OceanRuinPieces;
import noobanidus.mods.lootr.init.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(OceanRuinPieces.Piece.class)
public class MixinOceanRuinPieces$Piece {
  @Redirect(
      method = "Lnet/minecraft/world/gen/feature/structure/OceanRuinPieces$Piece;handleDataMarker(Ljava/lang/String;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/IServerWorld;Ljava/util/Random;Lnet/minecraft/util/math/MutableBoundingBox;)V",
      at = @At(value = "FIELD",
          target = "Lnet/minecraft/block/Blocks;CHEST:Lnet/minecraft/block/Block;",
          opcode = 178)
  )
  private Block getChestBlock() {
    return ModBlocks.CHEST;
  }
}