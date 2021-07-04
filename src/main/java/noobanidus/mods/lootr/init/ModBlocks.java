package noobanidus.mods.lootr.init;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.blocks.*;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;
import noobanidus.mods.lootr.tiles.SpecialTrappedLootChestTile;

public class ModBlocks {
  public static LootrBarrelBlock BARREL = new LootrBarrelBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD));

  public static LootrChestBlock CHEST = new LootrChestBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD));

  public static LootrTrappedChestBlock TRAPPED_CHEST = new LootrTrappedChestBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD));

  public static LootrInventoryBlock INVENTORY = new LootrInventoryBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(2.5f).sound(SoundType.WOOD));

  public static Block TROPHY = new TrophyBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(15f).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(0).notSolid().setLightLevel((o) -> 15));

  static {
    BARREL.setRegistryName(Lootr.MODID, "lootr_barrel");
    CHEST.setRegistryName(Lootr.MODID, "lootr_chest");
    TRAPPED_CHEST.setRegistryName(Lootr.MODID, "lootr_trapped_chest");
    INVENTORY.setRegistryName(Lootr.MODID, "lootr_inventory");
    TROPHY.setRegistryName(Lootr.MODID, "trophy");
  }

  public static void registerBlocks(RegistryEvent.Register<Block> event) {
    event.getRegistry().registerAll(BARREL, CHEST, TRAPPED_CHEST, INVENTORY, TROPHY);
  }
}
