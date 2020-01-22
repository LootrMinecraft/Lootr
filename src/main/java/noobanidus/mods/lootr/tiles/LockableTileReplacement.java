package noobanidus.mods.lootr.tiles;

import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import noobanidus.mods.lootr.data.BooleanData;

import java.util.Random;

public class LockableTileReplacement {
  public static void setLootTable(IBlockReader reader, Random rand, BlockPos pos, ResourceLocation lootTableIn) {
    TileEntity tileentity = reader.getTileEntity(pos);
    if (tileentity instanceof LockableLootTileEntity) {
      ((LockableLootTileEntity) tileentity).setLootTable(lootTableIn, rand.nextLong());
      if (reader instanceof IWorld && tileentity instanceof SpecialLootChestTile) {
        IWorld world = (IWorld) reader;
        BooleanData.markLootChest(world, pos);
      }
    }
  }
}
