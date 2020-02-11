package noobanidus.mods.lootr.util;

import net.minecraft.block.*;
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
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.tiles.ILootTile;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class ChestUtil {
  public static void handleLootChestReplaced(World world, BlockPos pos, BlockState oldState, BlockState newState) {
    if (oldState.getBlock() == newState.getBlock()) {
      return;
    }
    if (oldState.getBlock() == Blocks.BARREL && newState.getBlock() == ModBlocks.BARREL) {
      return;
    }
    BooleanData.deleteLootChest(world, pos);
  }

  public static boolean handleLootChest(World world, BlockPos pos, PlayerEntity player) {
    if (world.isRemote()) {
      return false;
    }
    INamedContainerProvider provider;
    if (isLootChest(world, pos)) {
      provider = ChestUtil.getLootContainer(world, pos, (ServerPlayerEntity) player);
    } else {
      BlockState state = world.getBlockState(pos);
      if (state.getBlock() == Blocks.CHEST) {
        provider = ((ChestBlock) Blocks.CHEST).getContainer(state, world, pos);
      } else if (state.getBlock() == Blocks.TRAPPED_CHEST) {
        provider = ((TrappedChestBlock) Blocks.TRAPPED_CHEST).getContainer(state, world, pos);
      } else if (state.getBlock() == Blocks.BARREL || state.getBlock() == ModBlocks.BARREL) {
        provider = ((ContainerBlock) Blocks.BARREL).getContainer(state, world, pos);
      } else if (state.getBlock() instanceof ChestBlock) {
        provider = ((ChestBlock) state.getBlock()).getContainer(state, world, pos);
      } else {
        provider = null;
      }
    }

    if (provider != null) {
      player.openContainer(provider);
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
      if (te instanceof ILootTile) {
        return ((ILootTile) te).isSpecialLootChest();
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
