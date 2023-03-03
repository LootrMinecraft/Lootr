package noobanidus.mods.lootr.init;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.LootrAPI;

public class ModItems {
  public static final BlockItem CHEST = new BlockItem(ModBlocks.CHEST, new Item.Properties());
  public static final BlockItem BARREL = new BlockItem(ModBlocks.BARREL, new Item.Properties());
  public static final BlockItem TRAPPED_CHEST = new BlockItem(ModBlocks.TRAPPED_CHEST, new Item.Properties());
  public static final BlockItem SHULKER = new BlockItem(ModBlocks.SHULKER, new Item.Properties());
  public static final BlockItem INVENTORY = new BlockItem(ModBlocks.INVENTORY, new Item.Properties());

  public static final BlockItem TROPHY = new BlockItem(ModBlocks.TROPHY, new FabricItemSettings().rarity(Rarity.EPIC).group(Lootr.TAB));

  public static void registerItems() {
    Registry.register(Registry.ITEM, new ResourceLocation(LootrAPI.MODID, "lootr_chest"), CHEST);
    Registry.register(Registry.ITEM, new ResourceLocation(LootrAPI.MODID, "lootr_barrel"), BARREL);
    Registry.register(Registry.ITEM, new ResourceLocation(LootrAPI.MODID, "lootr_trapped_chest"), TRAPPED_CHEST);
    Registry.register(Registry.ITEM, new ResourceLocation(LootrAPI.MODID, "lootr_shulker"), SHULKER);
    Registry.register(Registry.ITEM, new ResourceLocation(LootrAPI.MODID, "lootr_inventory"), INVENTORY);
    Registry.register(Registry.ITEM, new ResourceLocation(LootrAPI.MODID, "trophy"), TROPHY);
  }
}
