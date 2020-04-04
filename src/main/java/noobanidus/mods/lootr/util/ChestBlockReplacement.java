package noobanidus.mods.lootr.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.properties.ChestType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import noobanidus.mods.lootr.util.ChestUtil;

import javax.annotation.Nullable;

public class ChestBlockReplacement {
  @Nullable
  public static IInventory getInventory(BlockState state, World world, BlockPos pos, boolean allowBlocked) {
    if (ChestUtil.isLootChest(world, pos)) {
      return null;
    }

    return ChestBlock.getChestInventory(state, world, pos, allowBlocked, ChestBlock.field_220109_i);
  }

  @Nullable
  public static INamedContainerProvider getContainer(BlockState state, World world, BlockPos pos) {
    if (ChestUtil.isLootChest(world, pos)) {
      return null;
    }

    return ChestBlock.getChestInventory(state, world, pos, false, ChestBlock.field_220110_j);
  }

  public static BlockState updatePostPlacement(ChestBlock block, BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
    if (stateIn.get(ChestBlock.WATERLOGGED)) {
      worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
    }

    if (ChestUtil.isLootChest(worldIn, facingPos) || ChestUtil.isLootChest(worldIn, currentPos)) {
      return stateIn.with(ChestBlock.TYPE, ChestType.SINGLE);
    }

    if (facingState.getBlock() == block && facing.getAxis().isHorizontal()) {
      ChestType chesttype = facingState.get(ChestBlock.TYPE);
      if (stateIn.get(ChestBlock.TYPE) == ChestType.SINGLE && chesttype != ChestType.SINGLE && stateIn.get(ChestBlock.FACING) == facingState.get(ChestBlock.FACING) && ChestBlock.getDirectionToAttached(facingState) == facing.getOpposite()) {
        return stateIn.with(ChestBlock.TYPE, chesttype.opposite());
      }
    } else if (ChestBlock.getDirectionToAttached(stateIn) == facing) {
      return stateIn.with(ChestBlock.TYPE, ChestType.SINGLE);
    }

    return stateIn; // the "super" for block is supposed to be called
  }

  @Nullable
  public static Direction getDirectionToAttach(ChestBlock block, BlockItemUseContext context, Direction direction) {
    BlockPos offsetPos = context.getPos().offset(direction);
    World world = context.getWorld();
    if (ChestUtil.isLootChest(world, context.getPos()) || ChestUtil.isLootChest(world, offsetPos)) {
      return null;
    }

    BlockState blockstate = world.getBlockState(offsetPos);
    return blockstate.getBlock() == block && blockstate.get(ChestBlock.TYPE) == ChestType.SINGLE ? blockstate.get(ChestBlock.FACING) : null;
  }
}
