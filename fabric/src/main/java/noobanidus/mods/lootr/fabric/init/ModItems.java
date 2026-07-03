package noobanidus.mods.lootr.fabric.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import noobanidus.mods.lootr.common.api.LootrConstants;

public class ModItems {
  public static final BlockItem CHEST = new BlockItem(ModBlocks.CHEST, new Item.Properties().setId(LootrConstants.LootrItemIds.CHEST)
      .useBlockDescriptionPrefix());
  public static final BlockItem COPPER_CHEST = new BlockItem(ModBlocks.COPPER_CHEST, new Item.Properties().setId(LootrConstants.LootrItemIds.COPPER_CHEST)
      .useBlockDescriptionPrefix());
  public static final BlockItem WEATHERED_COPPER_CHEST = new BlockItem(ModBlocks.WEATHERED_COPPER_CHEST, new Item.Properties().setId(LootrConstants.LootrItemIds.WEATHERED_COPPER_CHEST)
      .useBlockDescriptionPrefix());
  public static final BlockItem EXPOSED_COPPER_CHEST = new BlockItem(ModBlocks.EXPOSED_COPPER_CHEST, new Item.Properties().setId(LootrConstants.LootrItemIds.EXPOSED_COPPER_CHEST)
      .useBlockDescriptionPrefix());
  public static final BlockItem OXIDIZED_COPPER_CHEST = new BlockItem(ModBlocks.OXIDIZED_COPPER_CHEST, new Item.Properties().setId(LootrConstants.LootrItemIds.OXIDIZED_COPPER_CHEST)
      .useBlockDescriptionPrefix());
  public static final BlockItem BARREL = new BlockItem(ModBlocks.BARREL, new Item.Properties().setId(LootrConstants.LootrItemIds.BARREL)
      .useBlockDescriptionPrefix());
  public static final BlockItem TRAPPED_CHEST = new BlockItem(ModBlocks.TRAPPED_CHEST, new Item.Properties().setId(LootrConstants.LootrItemIds.TRAPPED_CHEST)
      .useBlockDescriptionPrefix());
  public static final BlockItem SHULKER_BOX = new BlockItem(ModBlocks.SHULKER_BOX, new Item.Properties().setId(LootrConstants.LootrItemIds.SHULKER_BOX)
      .useBlockDescriptionPrefix());

  public static final BlockItem SUSPICIOUS_SAND = new BlockItem(ModBlocks.SUSPICIOUS_SAND, new Item.Properties().setId(LootrConstants.LootrItemIds.SUSPICIOUS_SAND)
      .useBlockDescriptionPrefix());
  public static final BlockItem SUSPICIOUS_GRAVEL = new BlockItem(ModBlocks.SUSPICIOUS_GRAVEL, new Item.Properties().setId(LootrConstants.LootrItemIds.SUSPICIOUS_GRAVEL)
      .useBlockDescriptionPrefix());

  public static final BlockItem DECORATED_POT = new BlockItem(ModBlocks.DECORATED_POT, new Item.Properties().setId(LootrConstants.LootrItemIds.DECORATED_POT)
      .useBlockDescriptionPrefix());

  public static final BlockItem TROPHY = new BlockItem(ModBlocks.TROPHY, new Item.Properties().rarity(Rarity.EPIC)
      .setId(LootrConstants.LootrItemIds.TROPHY).useBlockDescriptionPrefix());

  public static void registerItems() {
    Registry.register(BuiltInRegistries.ITEM, LootrConstants.Identifiers.CHEST, CHEST);
    Registry.register(BuiltInRegistries.ITEM, LootrConstants.Identifiers.BARREL, BARREL);
    Registry.register(BuiltInRegistries.ITEM, LootrConstants.Identifiers.TRAPPED_CHEST, TRAPPED_CHEST);
    Registry.register(BuiltInRegistries.ITEM, LootrConstants.Identifiers.SHULKER_BOX, SHULKER_BOX);
    Registry.register(BuiltInRegistries.ITEM, LootrConstants.Identifiers.SUSPICIOUS_SAND, SUSPICIOUS_SAND);
    Registry.register(BuiltInRegistries.ITEM, LootrConstants.Identifiers.SUSPICIOUS_GRAVEL, SUSPICIOUS_GRAVEL);
    Registry.register(BuiltInRegistries.ITEM, LootrConstants.Identifiers.DECORATED_POT, DECORATED_POT);
    Registry.register(BuiltInRegistries.ITEM, LootrConstants.Identifiers.TROPHY, TROPHY);
  }
}
