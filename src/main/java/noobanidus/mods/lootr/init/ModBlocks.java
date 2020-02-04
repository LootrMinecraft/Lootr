package noobanidus.mods.lootr.init;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.blocks.LootrBarrelBlock;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;
import noobanidus.mods.lootr.tiles.SpecialTrappedLootChestTile;

public class ModBlocks {
  public static LootrBarrelBlock BARREL = new LootrBarrelBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD));

  public static void registerBlocks(RegistryEvent.Register<Block> event) {
    BARREL.setRegistryName(Lootr.MODID, "lootr_barrel");
    event.getRegistry().register(BARREL);
  }
}
