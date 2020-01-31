package noobanidus.mods.lootr.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RailBlock;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.MineshaftPieces;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.tiles.LockableTileReplacement;

import java.util.Random;

public class CorridorReplacement {
  public static boolean generateChest(MineshaftPieces.Corridor corridor, IWorld worldIn, MutableBoundingBox structurebb, Random randomIn, int x, int y, int z, ResourceLocation loot) {
    BlockPos blockpos = new BlockPos(corridor.getXWithOffset(x, z), corridor.getYWithOffset(y), corridor.getZWithOffset(x, z));
    if (structurebb.isVecInside(blockpos) && worldIn.getBlockState(blockpos).isAir(worldIn, blockpos) && !worldIn.getBlockState(blockpos.down()).isAir(worldIn, blockpos.down())) {
      if (ConfigManager.CONVERT_MINESHAFTS.get()) {
        BlockState blockstate = Blocks.CHEST.getDefaultState();
        corridor.setBlockState(worldIn, blockstate, x, y, z, structurebb);
        LockableTileReplacement.setLootTable(worldIn, randomIn, blockpos, loot);
      } else {
        BlockState blockstate = Blocks.RAIL.getDefaultState().with(RailBlock.SHAPE, randomIn.nextBoolean() ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST);
        corridor.setBlockState(worldIn, blockstate, x, y, z, structurebb);
        ChestMinecartEntity chestminecartentity = new ChestMinecartEntity(worldIn.getWorld(), (double) ((float) blockpos.getX() + 0.5F), (double) ((float) blockpos.getY() + 0.5F), (double) ((float) blockpos.getZ() + 0.5F));
        chestminecartentity.setLootTable(loot, randomIn.nextLong());
        worldIn.addEntity(chestminecartentity);
      }
      return true;
    } else {
      return false;
    }
  }
}
