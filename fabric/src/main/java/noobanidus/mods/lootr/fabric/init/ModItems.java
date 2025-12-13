package noobanidus.mods.lootr.fabric.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.registry.LootrProperties;

public class ModItems {
  public static final BlockItem CHEST = new BlockItem(ModBlocks.CHEST, new Item.Properties());
  public static final BlockItem BARREL = new BlockItem(ModBlocks.BARREL, new Item.Properties());
  public static final BlockItem TRAPPED_CHEST = new BlockItem(ModBlocks.TRAPPED_CHEST, new Item.Properties());
  public static final BlockItem SHULKER = new BlockItem(ModBlocks.SHULKER, new Item.Properties());
  public static final BlockItem INVENTORY = new BlockItem(ModBlocks.INVENTORY, new Item.Properties());

  public static final BlockItem SUSPICIOUS_SAND = new BlockItem(ModBlocks.SUSPICIOUS_SAND, new Item.Properties());
  public static final BlockItem SUSPICIOUS_GRAVEL = new BlockItem(ModBlocks.SUSPICIOUS_GRAVEL, new Item.Properties());

  public static final BlockItem DECORATED_POT = new BlockItem(ModBlocks.DECORATED_POT, new Item.Properties());

  public static final BlockItem TROPHY = new BlockItem(ModBlocks.TROPHY, new Item.Properties().rarity(Rarity.EPIC));

  public static void registerItems() {
    Registry.register(BuiltInRegistries.ITEM, LootrProperties.LOOTR_CHEST, CHEST);
    Registry.register(BuiltInRegistries.ITEM, LootrProperties.LOOTR_BARREL, BARREL);
    Registry.register(BuiltInRegistries.ITEM, LootrProperties.LOOTR_TRAPPED_CHEST, TRAPPED_CHEST);
    Registry.register(BuiltInRegistries.ITEM, LootrProperties.LOOTR_SHULKER, SHULKER);
    Registry.register(BuiltInRegistries.ITEM, LootrProperties.LOOTR_INVENTORY, INVENTORY);
    Registry.register(BuiltInRegistries.ITEM, LootrProperties.SUSPICIOUS_SAND, SUSPICIOUS_SAND);
    Registry.register(BuiltInRegistries.ITEM, LootrProperties.SUSPICIOUS_GRAVEL, SUSPICIOUS_GRAVEL);
    Registry.register(BuiltInRegistries.ITEM, LootrProperties.DECORATED_POT, DECORATED_POT);
    Registry.register(BuiltInRegistries.ITEM, LootrProperties.TROPHY, TROPHY);
  }
}
