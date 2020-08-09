package noobanidus.mods.lootr.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RailBlock;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.structure.MineshaftPieces;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.Template;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.tiles.ILootTile;
import noobanidus.mods.lootr.util.TickManager;

import javax.annotation.Nullable;
import java.util.Random;

public class CorridorReplacement {
  public static boolean generateChest(StructurePiece piece, IWorld worldIn, MutableBoundingBox boundsIn, Random rand, BlockPos posIn, ResourceLocation resourceLocationIn, @Nullable BlockState state) {
    //Lootr.LOG.debug("Trying to generate a chest at " + posIn + " using the loot table: " + resourceLocationIn);
    if (boundsIn.isVecInside(posIn) && worldIn.getBlockState(posIn).getBlock() != Blocks.CHEST) {
      if (state == null) {
        state = StructurePiece.correctFacing(worldIn, posIn, ModBlocks.CHEST.getDefaultState());
      }

      worldIn.setBlockState(posIn, state, 2);
      TileEntity te = worldIn.getTileEntity(posIn);
      if (te instanceof ILootTile) {
        ((ILootTile) te).setSeed(rand.nextLong());
        ((ILootTile) te).setTable(resourceLocationIn);
        synchronized (TickManager.lootMap) {
          TickManager.lootMap.put(GlobalPos.of(worldIn.getDimension().getType(), posIn), resourceLocationIn);
        }
      }

      return true;
    } else {
      return false;
    }
  }

  public static boolean checkLootAndRead(LockableLootTileEntity tile, CompoundNBT tag) {
    if (tile instanceof ILootTile) {
      return false;
    }

    if (tag.contains("LootTable", 8)) {
      ResourceLocation table = new ResourceLocation(tag.getString("LootTable"));
      long seed = tag.getLong("LootTableSeed");
      DimensionType dim = null;
      if (tile.getWorld() != null) {
        dim = tile.getWorld().getDimension().getType();
      }
      TickManager.addTicker(tile, tile.getPos(), dim, table, seed);
      //Lootr.LOG.debug("(checkLootAndRead) Added a ticker for: " + tile + " at " + tile.getPos() + " in " + (dim == null ? "null" : dim) + " with table " + table);
    }

    return false;
  }
}
