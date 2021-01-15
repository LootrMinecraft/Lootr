package noobanidus.mods.lootr.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noobanidus.mods.lootr.data.NewChestData;
import noobanidus.mods.lootr.tiles.ILootTile;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@SuppressWarnings("unused")
public class ChestUtil {
  public static Random random = new Random();
  public static Set<Class<?>> tileClasses = new HashSet<>();

  public static boolean handleLootChest(World world, BlockPos pos, PlayerEntity player) {
    if (world.isRemote()) {
      return false;
    }
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof ILootTile) {
      INamedContainerProvider provider = NewChestData.getInventory(world, pos, (ServerPlayerEntity) player, ((ILootTile) te)::fillWithLoot);
      player.openContainer(provider);
      return true;
    } else {
      return false;
    }
  }
}
