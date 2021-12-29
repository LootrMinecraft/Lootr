package noobanidus.mods.lootr.init;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.client.item.SpecialLootChestItemRenderer;
import noobanidus.mods.lootr.client.item.SpecialLootShulkerItemRenderer;

@Mod.EventBusSubscriber(modid=Lootr.MODID)
public class ModItems {
  public static BlockItem CHEST = new BlockItem(ModBlocks.CHEST, new BlockItem.Properties().setISTER(() -> SpecialLootChestItemRenderer::new));
  public static BlockItem TRAPPED_CHEST = new BlockItem(ModBlocks.TRAPPED_CHEST, new BlockItem.Properties().setISTER(() -> SpecialLootChestItemRenderer::new));
  public static BlockItem BARREL = new BlockItem(ModBlocks.BARREL, new BlockItem.Properties());
  public static BlockItem SHULKER = new BlockItem(ModBlocks.SHULKER, new Item.Properties().setISTER(() -> SpecialLootShulkerItemRenderer::new));
  public static BlockItem INVENTORY = new BlockItem(ModBlocks.INVENTORY, new BlockItem.Properties().setISTER(() -> SpecialLootChestItemRenderer::new));

  public static BlockItem TROPHY = new BlockItem(ModBlocks.TROPHY, new Item.Properties().rarity(Rarity.EPIC).tab(Lootr.TAB));

  static {
    CHEST.setRegistryName(Lootr.MODID, "lootr_chest");
    TRAPPED_CHEST.setRegistryName(Lootr.MODID, "lootr_trapped_chest");
    BARREL.setRegistryName(Lootr.MODID, "lootr_barrel");
    INVENTORY.setRegistryName(Lootr.MODID, "lootr_inventory");
    SHULKER.setRegistryName(Lootr.MODID, "lootr_shulker");
    TROPHY.setRegistryName(Lootr.MODID, "trophy");
  }

  @SubscribeEvent
  public static void registerItems(RegistryEvent.Register<Item> event) {
    event.getRegistry().registerAll(CHEST, TRAPPED_CHEST, BARREL, INVENTORY, SHULKER, TROPHY);
  }
}
