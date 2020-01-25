package noobanidus.mods.lootr.util;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import noobanidus.mods.lootr.data.BooleanData;
import noobanidus.mods.lootr.data.ChestData;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;

import javax.annotation.Nullable;

public class ChestUtil {
  public static boolean isLootChest(IWorld world, BlockPos pos) {
    if (!world.isRemote()) {
      return BooleanData.isLootChest(world, pos);
    } else {
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof SpecialLootChestTile) {
        return ((SpecialLootChestTile) te).isSpecialLootChest();
      }

      return false;
    }
  }

  @Nullable
  public static INamedContainerProvider getLootContainer(IWorld world, BlockPos pos, ServerPlayerEntity player) {
    return ChestData.getInventory(world, pos, player);
  }
}
