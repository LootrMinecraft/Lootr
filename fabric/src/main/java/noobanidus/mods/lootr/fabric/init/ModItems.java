package noobanidus.mods.lootr.fabric.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import noobanidus.mods.lootr.common.api.LootrConstants;

public class ModItems {
  public static final BlockItem CHEST = new BlockItem(ModBlocks.CHEST, new Item.Properties().setId(LootrConstants.CHEST_ITEM_RESOURCE_KEY)
      .useBlockDescriptionPrefix());
  public static final BlockItem BARREL = new BlockItem(ModBlocks.BARREL, new Item.Properties().setId(LootrConstants.BARREL_ITEM_RESOURCE_KEY)
      .useBlockDescriptionPrefix());
  public static final BlockItem TRAPPED_CHEST = new BlockItem(ModBlocks.TRAPPED_CHEST, new Item.Properties().setId(LootrConstants.TRAPPED_CHEST_ITEM_RESOURCE_KEY)
      .useBlockDescriptionPrefix());
  public static final BlockItem SHULKER_BOX = new BlockItem(ModBlocks.SHULKER_BOX, new Item.Properties().setId(LootrConstants.SHULKER_BOX_ITEM_RESOURCE_KEY)
      .useBlockDescriptionPrefix());

  public static final BlockItem SUSPICIOUS_SAND = new BlockItem(ModBlocks.SUSPICIOUS_SAND, new Item.Properties().setId(LootrConstants.SUSPICIOUS_SAND_ITEM_RESOURCE_KEY)
      .useBlockDescriptionPrefix());
  public static final BlockItem SUSPICIOUS_GRAVEL = new BlockItem(ModBlocks.SUSPICIOUS_GRAVEL, new Item.Properties().setId(LootrConstants.SUSPICIOUS_GRAVEL_ITEM_RESOURCE_KEY)
      .useBlockDescriptionPrefix());

  public static final BlockItem DECORATED_POT = new BlockItem(ModBlocks.DECORATED_POT, new Item.Properties().setId(LootrConstants.DECORATED_POT_ITEM_RESOURCE_KEY)
      .useBlockDescriptionPrefix());

  public static final BlockItem TROPHY = new BlockItem(ModBlocks.TROPHY, new Item.Properties().rarity(Rarity.EPIC)
      .setId(LootrConstants.TROPHY_ITEM_RESOURCE_KEY).useBlockDescriptionPrefix());

  public static void registerItems() {
    Registry.register(BuiltInRegistries.ITEM, LootrConstants.CHEST, CHEST);
    Registry.register(BuiltInRegistries.ITEM, LootrConstants.BARREL, BARREL);
    Registry.register(BuiltInRegistries.ITEM, LootrConstants.TRAPPED_CHEST, TRAPPED_CHEST);
    Registry.register(BuiltInRegistries.ITEM, LootrConstants.SHULKER_BOX, SHULKER_BOX);
    Registry.register(BuiltInRegistries.ITEM, LootrConstants.SUSPICIOUS_SAND, SUSPICIOUS_SAND);
    Registry.register(BuiltInRegistries.ITEM, LootrConstants.SUSPICIOUS_GRAVEL, SUSPICIOUS_GRAVEL);
    Registry.register(BuiltInRegistries.ITEM, LootrConstants.DECORATED_POT, DECORATED_POT);
    Registry.register(BuiltInRegistries.ITEM, LootrConstants.TROPHY, TROPHY);
  }
}
