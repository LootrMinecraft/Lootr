package noobanidus.mods.lootr.neoforge.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.annotation.MigrateName;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;

public class ModItems {
  private static final DeferredRegister<Item> REGISTER = DeferredRegister.create(BuiltInRegistries.ITEM, LootrAPI.MODID);

  @MigrateName(value="chest", in="26")
  public static final DeferredHolder<Item, BlockItem> CHEST = REGISTER.register(LootrConstants.LOOTR_CHEST.getPath(), () -> new BlockItem(LootrRegistry.getChestBlock(), new BlockItem.Properties()));
  @MigrateName(value="trapped_chest", in="26")
  public static final DeferredHolder<Item, BlockItem> TRAPPED_CHEST = REGISTER.register(LootrConstants.LOOTR_TRAPPED_CHEST.getPath(), () -> new BlockItem(LootrRegistry.getTrappedChestBlock(), new BlockItem.Properties()));
  @MigrateName(value="barrel", in="26")
  public static final DeferredHolder<Item, BlockItem> BARREL = REGISTER.register(LootrConstants.LOOTR_BARREL.getPath(), () -> new BlockItem(LootrRegistry.getBarrelBlock(), new BlockItem.Properties()));
  @MigrateName(value="inventory", in="26")
  public static final DeferredHolder<Item, BlockItem> INVENTORY = REGISTER.register(LootrConstants.LOOTR_INVENTORY.getPath(), () -> new BlockItem(LootrRegistry.getInventoryBlock(), new BlockItem.Properties()));
  @MigrateName(value="shulker_box", in="26")
  public static final DeferredHolder<Item, BlockItem> SHULKER = REGISTER.register(LootrConstants.LOOTR_SHULKER.getPath(), () -> new BlockItem(LootrRegistry.getShulkerBlock(), new BlockItem.Properties()));
  public static final DeferredHolder<Item, BlockItem> SUSPICIOUS_SAND = REGISTER.register(LootrConstants.SUSPICIOUS_SAND.getPath(), () -> new BlockItem(LootrRegistry.getSuspiciousSandBlock(), new BlockItem.Properties()));
  public static final DeferredHolder<Item, BlockItem> SUSPICIOUS_GRAVEL = REGISTER.register(LootrConstants.SUSPICIOUS_GRAVEL.getPath(), () -> new BlockItem(LootrRegistry.getSuspiciousGravelBlock(), new BlockItem.Properties()));
  public static final DeferredHolder<Item, BlockItem> DECORATED_POT = REGISTER.register(LootrConstants.DECORATED_POT.getPath(), () -> new BlockItem(LootrRegistry.getDecoratedPotBlock(), new BlockItem.Properties()));
  public static final DeferredHolder<Item, BlockItem> TROPHY = REGISTER.register(LootrConstants.TROPHY.getPath(), () -> new BlockItem(LootrRegistry.getTrophyBlock(), new Item.Properties().rarity(Rarity.EPIC)));

  public static void register(IEventBus bus) {
    REGISTER.register(bus);
  }
}
