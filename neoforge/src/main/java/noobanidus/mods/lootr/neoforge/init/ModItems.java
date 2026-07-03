package noobanidus.mods.lootr.neoforge.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.api.LootrRegistry;

public class ModItems {
  private static final DeferredRegister<Item> REGISTER = DeferredRegister.create(BuiltInRegistries.ITEM, LootrAPI.MODID);

  public static final DeferredHolder<Item, BlockItem> CHEST = REGISTER.register(LootrConstants.Identifiers.CHEST.getPath(), () -> new BlockItem(LootrRegistry.getChestBlock(), new BlockItem.Properties().setId(LootrConstants.LootrItemIds.CHEST)
      .useBlockDescriptionPrefix()));
  public static final DeferredHolder<Item, BlockItem> COPPER_CHEST = REGISTER.register(LootrConstants.Identifiers.COPPER_CHEST.getPath(), () -> new BlockItem(LootrRegistry.getCopperChestBlock(), new BlockItem.Properties().setId(LootrConstants.LootrItemIds.COPPER_CHEST)
      .useBlockDescriptionPrefix()));
  public static final DeferredHolder<Item, BlockItem> WEATHERED_COPPER_CHEST = REGISTER.register(LootrConstants.Identifiers.WEATHERED_COPPER_CHEST.getPath(), () -> new BlockItem(LootrRegistry.getCopperChestBlock(), new BlockItem.Properties().setId(LootrConstants.LootrItemIds.WEATHERED_COPPER_CHEST)
      .useBlockDescriptionPrefix()));
  public static final DeferredHolder<Item, BlockItem> OXIDIZED_COPPER_CHEST = REGISTER.register(LootrConstants.Identifiers.OXIDIZED_COPPER_CHEST.getPath(), () -> new BlockItem(LootrRegistry.getCopperChestBlock(), new BlockItem.Properties().setId(LootrConstants.LootrItemIds.OXIDIZED_COPPER_CHEST)
      .useBlockDescriptionPrefix()));
  public static final DeferredHolder<Item, BlockItem> EXPOSED_COPPER_CHEST = REGISTER.register(LootrConstants.Identifiers.EXPOSED_COPPER_CHEST.getPath(), () -> new BlockItem(LootrRegistry.getCopperChestBlock(), new BlockItem.Properties().setId(LootrConstants.LootrItemIds.EXPOSED_COPPER_CHEST)
      .useBlockDescriptionPrefix()));
  public static final DeferredHolder<Item, BlockItem> TRAPPED_CHEST = REGISTER.register(LootrConstants.Identifiers.TRAPPED_CHEST.getPath(), () -> new BlockItem(LootrRegistry.getTrappedChestBlock(), new BlockItem.Properties().setId(LootrConstants.LootrItemIds.TRAPPED_CHEST)
      .useBlockDescriptionPrefix()));
  public static final DeferredHolder<Item, BlockItem> BARREL = REGISTER.register(LootrConstants.Identifiers.BARREL.getPath(), () -> new BlockItem(LootrRegistry.getBarrelBlock(), new BlockItem.Properties().setId(LootrConstants.LootrItemIds.BARREL)
      .useBlockDescriptionPrefix()));
  public static final DeferredHolder<Item, BlockItem> SHULKER = REGISTER.register(LootrConstants.Identifiers.SHULKER_BOX.getPath(), () -> new BlockItem(LootrRegistry.getShulkerBoxBlock(), new BlockItem.Properties().setId(LootrConstants.LootrItemIds.SHULKER_BOX)
      .useBlockDescriptionPrefix()));
  public static final DeferredHolder<Item, BlockItem> SUSPICIOUS_SAND = REGISTER.register(LootrConstants.Identifiers.SUSPICIOUS_SAND.getPath(), () -> new BlockItem(LootrRegistry.getSuspiciousSandBlock(), new BlockItem.Properties().setId(LootrConstants.LootrItemIds.SUSPICIOUS_SAND)
      .useBlockDescriptionPrefix()));
  public static final DeferredHolder<Item, BlockItem> SUSPICIOUS_GRAVEL = REGISTER.register(LootrConstants.Identifiers.SUSPICIOUS_GRAVEL.getPath(), () -> new BlockItem(LootrRegistry.getSuspiciousGravelBlock(), new BlockItem.Properties().setId(LootrConstants.LootrItemIds.SUSPICIOUS_GRAVEL)
      .useBlockDescriptionPrefix()));
  public static final DeferredHolder<Item, BlockItem> DECORATED_POT = REGISTER.register(LootrConstants.Identifiers.DECORATED_POT.getPath(), () -> new BlockItem(LootrRegistry.getDecoratedPotBlock(), new BlockItem.Properties().setId(LootrConstants.LootrItemIds.DECORATED_POT)
      .useBlockDescriptionPrefix()));
  public static final DeferredHolder<Item, BlockItem> TROPHY = REGISTER.register(LootrConstants.Identifiers.TROPHY.getPath(), () -> new BlockItem(LootrRegistry.getTrophyBlock(), new Item.Properties().rarity(Rarity.EPIC)
      .setId(LootrConstants.LootrItemIds.TROPHY).useBlockDescriptionPrefix()));

  public static void register(IEventBus bus) {
    REGISTER.register(bus);
  }
}
