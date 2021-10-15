package noobanidus.mods.lootr.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.blocks.*;

@Mod.EventBusSubscriber(modid=Lootr.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks {
  public static LootrBarrelBlock BARREL = new LootrBarrelBlock(BlockBehaviour.Properties.copy(Blocks.CHEST).strength(2.5f));

  public static LootrChestBlock CHEST = new LootrChestBlock(BlockBehaviour.Properties.copy(Blocks.BARREL).strength(2.5f));

  public static LootrTrappedChestBlock TRAPPED_CHEST = new LootrTrappedChestBlock(BlockBehaviour.Properties.copy(Blocks.TRAPPED_CHEST).strength(2.5f));

  public static LootrInventoryBlock INVENTORY = new LootrInventoryBlock(Block.Properties.of(Material.WOOD).strength(2.5f).sound(SoundType.WOOD));

  // TODO: Tag this
  public static Block TROPHY = new TrophyBlock(Block.Properties.of(Material.METAL).strength(15f).sound(SoundType.METAL).noOcclusion().lightLevel((o) -> 15));

  static {
    BARREL.setRegistryName(Lootr.MODID, "lootr_barrel");
    CHEST.setRegistryName(Lootr.MODID, "lootr_chest");
    TRAPPED_CHEST.setRegistryName(Lootr.MODID, "lootr_trapped_chest");
    INVENTORY.setRegistryName(Lootr.MODID, "lootr_inventory");
    TROPHY.setRegistryName(Lootr.MODID, "trophy");
  }

  @SubscribeEvent
  public static void registerBlocks(RegistryEvent.Register<Block> event) {
    event.getRegistry().registerAll(BARREL, CHEST, TRAPPED_CHEST, INVENTORY, TROPHY);
  }
}
