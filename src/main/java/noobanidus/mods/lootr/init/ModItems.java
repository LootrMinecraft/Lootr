package noobanidus.mods.lootr.init;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraftforge.event.RegistryEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.client.SpecialLootChestItemRenderer;

public class ModItems {
  public static BlockItem CHEST = new BlockItem(ModBlocks.CHEST, new BlockItem.Properties().setISTER(() -> SpecialLootChestItemRenderer::new));
  public static BlockItem TRAPPED_CHEST = new BlockItem(ModBlocks.TRAPPED_CHEST, new BlockItem.Properties().setISTER(() -> SpecialLootChestItemRenderer::new));
  public static BlockItem BARREL = new BlockItem(ModBlocks.BARREL, new BlockItem.Properties());
  public static BlockItem INVENTORY = new BlockItem(ModBlocks.INVENTORY, new BlockItem.Properties().setISTER(() -> SpecialLootChestItemRenderer::new));

  public static BlockItem TROPHY = new BlockItem(ModBlocks.TROPHY, new Item.Properties().rarity(Rarity.EPIC).group(Lootr.TAB));

  static {
    CHEST.setRegistryName(Lootr.MODID, "lootr_chest");
    TRAPPED_CHEST.setRegistryName(Lootr.MODID, "lootr_trapped_chest");
    BARREL.setRegistryName(Lootr.MODID, "lootr_barrel");
    INVENTORY.setRegistryName(Lootr.MODID, "lootr_inventory");
    TROPHY.setRegistryName(Lootr.MODID, "trophy");
  }

  public static void registerItems(RegistryEvent.Register<Item> event) {
    event.getRegistry().registerAll(CHEST, TRAPPED_CHEST, BARREL, INVENTORY, TROPHY);
  }
}
