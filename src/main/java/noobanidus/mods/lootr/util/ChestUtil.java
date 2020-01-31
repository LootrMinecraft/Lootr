package noobanidus.mods.lootr.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import noobanidus.mods.lootr.data.BooleanData;
import noobanidus.mods.lootr.data.NewChestData;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class ChestUtil {
  public static boolean handleLootChest(World world, BlockPos pos, PlayerEntity player) {
    if (world.isRemote()) {
      return false;
    }
    INamedContainerProvider inamedcontainerprovider = ChestUtil.getLootContainer(world, pos, (ServerPlayerEntity) player);
    if (inamedcontainerprovider != null) {
      player.openContainer(inamedcontainerprovider);
    }
    return true;
  }

  public static boolean isLootChest(IWorld world, BlockPos pos, Direction offset) {
    if (isLootChest(world, pos)) {
      return true;
    }

    return isLootChest(world, pos.offset(offset));
  }

  public static boolean isLootChest(BlockItemUseContext context, Direction direction) {
    if (isLootChest(context.getWorld(), context.getPos())) {
      return true;
    }

    return isLootChest(context.getWorld(), context.getPos().offset(direction));
  }

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
    return NewChestData.getInventory(world, pos, player);
  }

  public enum ReturnType {
    TRUE, FALSE, SKIP
  }
}
