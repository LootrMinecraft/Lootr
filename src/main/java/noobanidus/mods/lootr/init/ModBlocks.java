package noobanidus.mods.lootr.init;

import com.google.common.collect.Sets;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.block.*;
import noobanidus.mods.lootr.block.tile.LootrShulkerTileEntity;

import java.util.Set;

@Mod.EventBusSubscriber(modid = Lootr.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks {
  public static LootrBarrelBlock BARREL = new LootrBarrelBlock(Block.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD));

  public static LootrChestBlock CHEST = new LootrChestBlock(Block.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD));

  public static LootrTrappedChestBlock TRAPPED_CHEST = new LootrTrappedChestBlock(Block.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD));

  public static LootrInventoryBlock INVENTORY = new LootrInventoryBlock(Block.Properties.of(Material.WOOD).strength(2.5f).sound(SoundType.WOOD));

  private static final AbstractBlock.IPositionPredicate posPredicate = (state, level, pos) -> {
    TileEntity tileentity = level.getBlockEntity(pos);
    if (!(tileentity instanceof LootrShulkerTileEntity)) {
      return true;
    } else {
      LootrShulkerTileEntity shulkerboxtileentity = (LootrShulkerTileEntity) tileentity;
      return shulkerboxtileentity.isClosed();
    }
  };

  public static LootrShulkerBlock SHULKER = new LootrShulkerBlock(Block.Properties.of(Material.SHULKER_SHELL).strength(2.5f).dynamicShape().noOcclusion().isSuffocating(posPredicate).isViewBlocking(posPredicate));
  public static Set<Block> LOOT_CONTAINERS = Sets.newHashSet(CHEST, BARREL, TRAPPED_CHEST, SHULKER, INVENTORY);

  public static Block TROPHY = new TrophyBlock(Block.Properties.of(Material.METAL).strength(15f).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(0).noOcclusion().lightLevel((o) -> 15));

  static {
    BARREL.setRegistryName(Lootr.MODID, "lootr_barrel");
    CHEST.setRegistryName(Lootr.MODID, "lootr_chest");
    TRAPPED_CHEST.setRegistryName(Lootr.MODID, "lootr_trapped_chest");
    INVENTORY.setRegistryName(Lootr.MODID, "lootr_inventory");
    SHULKER.setRegistryName(Lootr.MODID, "lootr_shulker");
    TROPHY.setRegistryName(Lootr.MODID, "trophy");
  }

  @SubscribeEvent
  public static void registerBlocks(RegistryEvent.Register<Block> event) {
    LOOT_CONTAINERS.forEach(o -> event.getRegistry().register(o));
  }
}
