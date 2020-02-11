package noobanidus.mods.lootr.tiles;

import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.data.BooleanData;

import java.util.Random;

public class LockableTileReplacement {
  public static void setLootTable(IBlockReader reader, Random rand, BlockPos pos, ResourceLocation lootTableIn) {
    TileEntity tileentity = reader.getTileEntity(pos);
    if (tileentity instanceof LockableLootTileEntity) {
      ((LockableLootTileEntity) tileentity).setLootTable(lootTableIn, rand.nextLong());
      Lootr.LOG.debug("Set loot table for tile entity at " + pos.toString() + " with table " + lootTableIn.toString());
      if (reader instanceof IWorld && tileentity instanceof ILootTile) {
        IWorld world = (IWorld) reader;
        BooleanData.markLootChest(world, pos);
      }
    }
  }
}
