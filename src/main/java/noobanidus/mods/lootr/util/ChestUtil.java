package noobanidus.mods.lootr.util;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import noobanidus.mods.lootr.data.NewChestData;
import noobanidus.mods.lootr.init.ModBlocks;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class ChestUtil {
  public static IInventory getInventory(BlockState state, World world, BlockPos pos, boolean allowBlocked) {
    return ChestBlock.getChestInventory(state, world, pos, allowBlocked, ChestBlock.field_220109_i);
  }

  public static INamedContainerProvider getContainer(BlockState state, World world, BlockPos pos) {
    return ChestBlock.getChestInventory(state, world, pos, false, ChestBlock.field_220110_j);
  }

  @Nullable
  public static INamedContainerProvider getLootContainer(IWorld world, BlockPos pos, ServerPlayerEntity player) {
    return NewChestData.getInventory(world, pos, player);
  }

   public static boolean handleLootChest(World world, BlockPos pos, PlayerEntity player) {
     if (world.isRemote()) {
       return false;
     }
     INamedContainerProvider provider = ChestUtil.getLootContainer(world, pos, (ServerPlayerEntity) player);
     player.openContainer(provider);
     return true;
   }

  public enum ReturnType {
    TRUE, FALSE, SKIP
  }
}
