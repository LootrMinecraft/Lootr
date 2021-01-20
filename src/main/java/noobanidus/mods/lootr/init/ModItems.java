package noobanidus.mods.lootr.init;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.client.SpecialLootChestItemRenderer;

public class ModItems {
  public static BlockItem CHEST = new BlockItem(ModBlocks.CHEST, new BlockItem.Properties().setISTER(() -> SpecialLootChestItemRenderer::new));

  static {
    CHEST.setRegistryName(Lootr.MODID, "lootr_chest");
  }

  public static void registerItems(RegistryEvent.Register<Item> event) {
    event.getRegistry().registerAll(CHEST);
  }
}
