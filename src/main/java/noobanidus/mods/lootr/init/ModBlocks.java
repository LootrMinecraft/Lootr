package noobanidus.mods.lootr.init;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.blocks.*;
import noobanidus.mods.lootr.client.SpecialLootShulkerTileRenderer;
import noobanidus.mods.lootr.tiles.SpecialLootShulkerTile;

public class ModBlocks {
  public static LootrBarrelBlock BARREL = new LootrBarrelBlock(Block.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD));

  public static LootrChestBlock CHEST = new LootrChestBlock(Block.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD));

  public static LootrTrappedChestBlock TRAPPED_CHEST = new LootrTrappedChestBlock(Block.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD));

  public static LootrInventoryBlock INVENTORY = new LootrInventoryBlock(Block.Properties.of(Material.WOOD).strength(2.5f).sound(SoundType.WOOD));

  private static AbstractBlock.IPositionPredicate posPredicate = (p_235444_0_, p_235444_1_, p_235444_2_) -> {
    TileEntity tileentity = p_235444_1_.getBlockEntity(p_235444_2_);
    if (!(tileentity instanceof SpecialLootShulkerTile)) {
      return true;
    } else {
      SpecialLootShulkerTile shulkerboxtileentity = (SpecialLootShulkerTile) tileentity;
      return shulkerboxtileentity.isClosed();
    }
  };

  public static LootrShulkerBlock SHULKER = new LootrShulkerBlock(Block.Properties.of(Material.SHULKER_SHELL).strength(2.5f).dynamicShape().noOcclusion().isSuffocating(posPredicate).isViewBlocking(posPredicate));

  public static Block TROPHY = new TrophyBlock(Block.Properties.of(Material.METAL).strength(15f).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(0).noOcclusion().lightLevel((o) -> 15));

  static {
    BARREL.setRegistryName(Lootr.MODID, "lootr_barrel");
    CHEST.setRegistryName(Lootr.MODID, "lootr_chest");
    TRAPPED_CHEST.setRegistryName(Lootr.MODID, "lootr_trapped_chest");
    INVENTORY.setRegistryName(Lootr.MODID, "lootr_inventory");
    SHULKER.setRegistryName(Lootr.MODID, "lootr_shulker");
    TROPHY.setRegistryName(Lootr.MODID, "trophy");
  }

  public static void registerBlocks(RegistryEvent.Register<Block> event) {
    event.getRegistry().registerAll(BARREL, CHEST, TRAPPED_CHEST, INVENTORY, SHULKER, TROPHY);
  }
}
