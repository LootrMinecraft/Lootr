package noobanidus.mods.lootr.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.item.CrownItem;
import noobanidus.mods.lootr.item.LootrChestBlockItem;
import noobanidus.mods.lootr.item.LootrShulkerBlockItem;

@Mod.EventBusSubscriber(modid= LootrAPI.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {
  public static BlockItem CHEST = new LootrChestBlockItem(ModBlocks.CHEST, new BlockItem.Properties());
  public static BlockItem TRAPPED_CHEST = new LootrChestBlockItem(ModBlocks.TRAPPED_CHEST, new BlockItem.Properties());
  public static BlockItem BARREL = new BlockItem(ModBlocks.BARREL, new BlockItem.Properties());
  public static BlockItem INVENTORY = new LootrChestBlockItem(ModBlocks.INVENTORY, new BlockItem.Properties());
  public static BlockItem SHULKER = new LootrShulkerBlockItem(ModBlocks.SHULKER, new BlockItem.Properties());

  public static BlockItem TROPHY = new BlockItem(ModBlocks.TROPHY, new Item.Properties().rarity(Rarity.EPIC).tab(Lootr.TAB));

  public static CrownItem CROWN = new CrownItem(new Item.Properties().rarity(Rarity.EPIC).tab(Lootr.TAB));

  static {
    CHEST.setRegistryName(LootrAPI.MODID, "lootr_chest");
    TRAPPED_CHEST.setRegistryName(LootrAPI.MODID, "lootr_trapped_chest");
    BARREL.setRegistryName(LootrAPI.MODID, "lootr_barrel");
    INVENTORY.setRegistryName(LootrAPI.MODID, "lootr_inventory");
    SHULKER.setRegistryName(LootrAPI.MODID, "lootr_shulker");
    TROPHY.setRegistryName(LootrAPI.MODID, "trophy");
    CROWN.setRegistryName(LootrAPI.MODID, "crown");
  }

  @SubscribeEvent
  public static void registerItems(RegistryEvent.Register<Item> event) {
    event.getRegistry().registerAll(CHEST, TRAPPED_CHEST, BARREL, INVENTORY, SHULKER, TROPHY/*, CROWN*/);
  }
}
