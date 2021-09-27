package noobanidus.mods.lootr.mixins;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MonsterRoomFeature.class)
public class MixinDungeonsFeature {
  @Redirect(
      method = "place",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/feature/structure/StructurePiece;reorient(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/block/BlockState;")
  )
  private BlockState correctFacing(BlockGetter worldIn, BlockPos posIn, BlockState blockStateIn) {
    ResourceKey<Level> key = ((WorldGenLevel) worldIn).getLevel().dimension();
    if (ConfigManager.isDimensionBlocked(key)) {
      return StructurePiece.reorient(worldIn, posIn, blockStateIn);
    }
    return StructurePiece.reorient(worldIn, posIn, ModBlocks.CHEST.defaultBlockState().setValue(ChestBlock.WATERLOGGED, blockStateIn.getValue(ChestBlock.WATERLOGGED)));
  }
}
