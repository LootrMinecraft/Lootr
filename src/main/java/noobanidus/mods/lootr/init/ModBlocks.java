package noobanidus.mods.lootr.init;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.TrappedChestBlock;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.blocks.LootrBarrelBlock;
import noobanidus.mods.lootr.blocks.LootrChestBlock;
import noobanidus.mods.lootr.blocks.LootrTrappedChestBlock;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;
import noobanidus.mods.lootr.tiles.SpecialTrappedLootChestTile;

public class ModBlocks {
  public static LootrBarrelBlock BARREL = new LootrBarrelBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD));

  public static LootrChestBlock CHEST = new LootrChestBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD));

  public static LootrTrappedChestBlock TRAPPED_CHEST = new LootrTrappedChestBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD));

  static {
    BARREL.setRegistryName(Lootr.MODID, "lootr_barrel");
    CHEST.setRegistryName(Lootr.MODID, "lootr_chest");
    TRAPPED_CHEST.setRegistryName(Lootr.MODID, "lootr_trapped_chest");
  }

  public static void registerBlocks(RegistryEvent.Register<Block> event) {
    event.getRegistry().registerAll(BARREL, CHEST, TRAPPED_CHEST);
  }
}
