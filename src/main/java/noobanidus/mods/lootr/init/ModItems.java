package noobanidus.mods.lootr.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.item.LootrChestBlockItem;
import noobanidus.mods.lootr.item.LootrShulkerBlockItem;

@Mod.EventBusSubscriber(modid = LootrAPI.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {
  public static BlockItem CHEST;
  public static BlockItem TRAPPED_CHEST;
  public static BlockItem BARREL;
  public static BlockItem INVENTORY;
  public static BlockItem SHULKER;
  public static BlockItem TROPHY;

  public static void construct() {
    CHEST = new LootrChestBlockItem(ModBlocks.CHEST, new BlockItem.Properties());
    TRAPPED_CHEST = new LootrChestBlockItem(ModBlocks.TRAPPED_CHEST, new BlockItem.Properties());
    BARREL = new BlockItem(ModBlocks.BARREL, new BlockItem.Properties());
    INVENTORY = new LootrChestBlockItem(ModBlocks.INVENTORY, new BlockItem.Properties());
    SHULKER = new LootrShulkerBlockItem(ModBlocks.SHULKER, new BlockItem.Properties());
    TROPHY = new BlockItem(ModBlocks.TROPHY, new Item.Properties().rarity(Rarity.EPIC).tab(Lootr.TAB));

    CHEST.setRegistryName(LootrAPI.MODID, "lootr_chest");
    TRAPPED_CHEST.setRegistryName(LootrAPI.MODID, "lootr_trapped_chest");
    BARREL.setRegistryName(LootrAPI.MODID, "lootr_barrel");
    INVENTORY.setRegistryName(LootrAPI.MODID, "lootr_inventory");
    SHULKER.setRegistryName(LootrAPI.MODID, "lootr_shulker");
    TROPHY.setRegistryName(LootrAPI.MODID, "trophy");
  }

  @SubscribeEvent
  public static void registerItems(RegistryEvent.Register<Item> event) {
    construct();
    event.getRegistry().registerAll(CHEST, TRAPPED_CHEST, BARREL, INVENTORY, SHULKER, TROPHY/*, CROWN*/);
  }
}
