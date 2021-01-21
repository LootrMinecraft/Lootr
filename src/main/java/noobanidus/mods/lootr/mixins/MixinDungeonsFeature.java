package noobanidus.mods.lootr.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.gen.feature.DungeonsFeature;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import noobanidus.mods.lootr.init.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DungeonsFeature.class)
public class MixinDungeonsFeature {
  @Redirect(
      method = "Lnet/minecraft/world/gen/feature/DungeonsFeature;generate(Lnet/minecraft/world/ISeedReader;Lnet/minecraft/world/gen/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/NoFeatureConfig;)Z",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/feature/structure/StructurePiece;correctFacing(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/block/BlockState;")
  )
  private BlockState correctFacing(IBlockReader worldIn, BlockPos posIn, BlockState blockStateIn) {
    return StructurePiece.correctFacing(worldIn, posIn, ModBlocks.CHEST.getDefaultState().with(ChestBlock.WATERLOGGED, blockStateIn.get(ChestBlock.WATERLOGGED)));
  }
}
