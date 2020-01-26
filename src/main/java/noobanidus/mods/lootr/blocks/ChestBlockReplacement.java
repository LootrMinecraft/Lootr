package noobanidus.mods.lootr.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.BlockPos;
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
}
