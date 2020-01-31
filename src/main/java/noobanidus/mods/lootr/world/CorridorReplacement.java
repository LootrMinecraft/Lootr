package noobanidus.mods.lootr.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.MineshaftPieces;
import noobanidus.mods.lootr.tiles.LockableTileReplacement;

import java.util.Random;

public class CorridorReplacement {
  public static boolean generateChest(MineshaftPieces.Corridor corridor, IWorld worldIn, MutableBoundingBox structurebb, Random randomIn, int x, int y, int z, ResourceLocation loot) {
    BlockPos blockpos = new BlockPos(corridor.getXWithOffset(x, z), corridor.getYWithOffset(y), corridor.getZWithOffset(x, z));
    if (structurebb.isVecInside(blockpos) && worldIn.getBlockState(blockpos).isAir(worldIn, blockpos) && !worldIn.getBlockState(blockpos.down()).isAir(worldIn, blockpos.down())) {
      BlockState blockstate = Blocks.CHEST.getDefaultState();
      corridor.setBlockState(worldIn, blockstate, x, y, z, structurebb);
      LockableTileReplacement.setLootTable(worldIn, randomIn, blockpos, loot);
      return true;
    } else {
      return false;
    }
  }
}
