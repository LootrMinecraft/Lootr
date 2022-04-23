package noobanidus.mods.lootr.init;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noobanidus.mods.lootr.Lootr;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class ModItems {
  public static ItemBlock CHEST = new ItemLootrChestBlock(ModBlocks.CHEST);
  public static ItemBlock TRAPPED_CHEST = new ItemLootrChestBlock(ModBlocks.TRAPPED_CHEST);
  public static ItemBlock SHULKER = new ItemLootrShulkerBlock(ModBlocks.SHULKER);

  public static ItemBlock TROPHY = new ItemBlock(ModBlocks.TROPHY);

  static {
    CHEST.setRegistryName(Lootr.MODID, "lootr_chest");
    TRAPPED_CHEST.setRegistryName(Lootr.MODID, "lootr_trapped_chest");
    SHULKER.setRegistryName(Lootr.MODID, "lootr_shulker");
    TROPHY.setRegistryName(Lootr.MODID, "trophy");
  }

  @SubscribeEvent
  public static void registerItems(RegistryEvent.Register<Item> event) {
    event.getRegistry().registerAll(CHEST, TRAPPED_CHEST, SHULKER, TROPHY);
  }
}
