package noobanidus.mods.lootr.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.MineshaftPieces;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTables;
import noobanidus.mods.lootr.util.ChestUtil;
import org.objectweb.asm.tree.FieldInsnNode;

import javax.annotation.Nullable;
import java.util.Random;

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
}
