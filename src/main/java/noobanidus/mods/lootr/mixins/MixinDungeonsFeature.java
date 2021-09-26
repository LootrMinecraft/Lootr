package noobanidus.mods.lootr.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.DungeonsFeature;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DungeonsFeature.class)
public class MixinDungeonsFeature {
  @Redirect(
      method = "place",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/feature/structure/StructurePiece;reorient(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/block/BlockState;")
  )
  private BlockState correctFacing(IBlockReader worldIn, BlockPos posIn, BlockState blockStateIn) {
    RegistryKey<World> key = ((ISeedReader) worldIn).getLevel().dimension();
    if (ConfigManager.isDimensionBlocked(key)) {
      return StructurePiece.reorient(worldIn, posIn, blockStateIn);
    }
    return StructurePiece.reorient(worldIn, posIn, ModBlocks.CHEST.defaultBlockState().setValue(ChestBlock.WATERLOGGED, blockStateIn.getValue(ChestBlock.WATERLOGGED)));
  }
}
