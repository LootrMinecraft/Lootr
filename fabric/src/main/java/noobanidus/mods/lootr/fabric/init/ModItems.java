package noobanidus.mods.lootr.fabric.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;

public class ModItems {
  public static final BlockItem CHEST = new BlockItem(ModBlocks.CHEST, new Item.Properties().setId(LootrRegistry.CHEST_ITEM_KEY).useBlockDescriptionPrefix());
  public static final BlockItem BARREL = new BlockItem(ModBlocks.BARREL, new Item.Properties().setId(LootrRegistry.BARREL_ITEM_KEY).useBlockDescriptionPrefix());
  public static final BlockItem TRAPPED_CHEST = new BlockItem(ModBlocks.TRAPPED_CHEST, new Item.Properties().setId(LootrRegistry.TRAPPED_CHEST_ITEM_KEY).useBlockDescriptionPrefix());
  public static final BlockItem SHULKER = new BlockItem(ModBlocks.SHULKER, new Item.Properties().setId(LootrRegistry.SHULKER_ITEM_KEY).useBlockDescriptionPrefix());
  public static final BlockItem INVENTORY = new BlockItem(ModBlocks.INVENTORY, new Item.Properties().setId(LootrRegistry.INVENTORY_ITEM_KEY).useBlockDescriptionPrefix());

  public static final BlockItem TROPHY = new BlockItem(ModBlocks.TROPHY, new Item.Properties().rarity(Rarity.EPIC).setId(LootrRegistry.TROPHY_ITEM_KEY).useBlockDescriptionPrefix());

  public static void registerItems() {
    Registry.register(BuiltInRegistries.ITEM, LootrRegistry.CHEST, CHEST);
    Registry.register(BuiltInRegistries.ITEM, LootrRegistry.BARREL, BARREL);
    Registry.register(BuiltInRegistries.ITEM, LootrRegistry.TRAPPED_CHEST, TRAPPED_CHEST);
    Registry.register(BuiltInRegistries.ITEM, LootrRegistry.SHULKER, SHULKER);
    Registry.register(BuiltInRegistries.ITEM, LootrRegistry.INVENTORY, INVENTORY);
    Registry.register(BuiltInRegistries.ITEM, LootrRegistry.TROPHY, TROPHY);
  }
}
