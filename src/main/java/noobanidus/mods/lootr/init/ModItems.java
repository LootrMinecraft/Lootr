package noobanidus.mods.lootr.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.items.LootrChestBlockItem;

@Mod.EventBusSubscriber(modid=Lootr.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {
  public static BlockItem CHEST = new LootrChestBlockItem(ModBlocks.CHEST, new BlockItem.Properties());
  public static BlockItem TRAPPED_CHEST = new LootrChestBlockItem(ModBlocks.TRAPPED_CHEST, new BlockItem.Properties());
  public static BlockItem BARREL = new BlockItem(ModBlocks.BARREL, new BlockItem.Properties());
  public static BlockItem INVENTORY = new LootrChestBlockItem(ModBlocks.INVENTORY, new BlockItem.Properties());

  public static BlockItem TROPHY = new BlockItem(ModBlocks.TROPHY, new Item.Properties().rarity(Rarity.EPIC).tab(Lootr.TAB));

  static {
    CHEST.setRegistryName(Lootr.MODID, "lootr_chest");
    TRAPPED_CHEST.setRegistryName(Lootr.MODID, "lootr_trapped_chest");
    BARREL.setRegistryName(Lootr.MODID, "lootr_barrel");
    INVENTORY.setRegistryName(Lootr.MODID, "lootr_inventory");
    TROPHY.setRegistryName(Lootr.MODID, "trophy");
  }

  @SubscribeEvent
  public static void registerItems(RegistryEvent.Register<Item> event) {
    event.getRegistry().registerAll(CHEST, TRAPPED_CHEST, BARREL, INVENTORY, TROPHY);
  }
}
