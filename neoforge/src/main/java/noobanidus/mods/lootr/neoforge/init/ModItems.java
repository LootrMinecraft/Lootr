package noobanidus.mods.lootr.neoforge.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;

public class ModItems {
  private static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(LootrAPI.MODID);

  public static final DeferredItem<BlockItem> CHEST = REGISTER.registerItem(LootrRegistry.CHEST.getPath(), (p) -> new BlockItem(LootrRegistry.getChestBlock(), p), new Item.Properties().setId(LootrRegistry.CHEST_ITEM_KEY));
  public static final DeferredItem<BlockItem> TRAPPED_CHEST = REGISTER.registerItem(LootrRegistry.TRAPPED_CHEST.getPath(), (p) -> new BlockItem(LootrRegistry.getTrappedChestBlock(), p), new Item.Properties().setId(LootrRegistry.TRAPPED_CHEST_ITEM_KEY));
  public static final DeferredItem<BlockItem> BARREL = REGISTER.registerItem(LootrRegistry.BARREL.getPath(), (p) -> new BlockItem(LootrRegistry.getBarrelBlock(), p), new Item.Properties().setId(LootrRegistry.BARREL_ITEM_KEY));
  public static final DeferredItem<BlockItem> INVENTORY = REGISTER.registerItem(LootrRegistry.INVENTORY.getPath(), (p) -> new BlockItem(LootrRegistry.getInventoryBlock(), p), new Item.Properties().setId(LootrRegistry.INVENTORY_ITEM_KEY));
  public static final DeferredItem<BlockItem> SHULKER = REGISTER.registerItem(LootrRegistry.SHULKER.getPath(), (p) -> new BlockItem(LootrRegistry.getShulkerBlock(), p), new Item.Properties().setId(LootrRegistry.SHULKER_ITEM_KEY));
  public static final DeferredItem<BlockItem> TROPHY = REGISTER.registerItem(LootrRegistry.TROPHY.getPath(), (p) -> new BlockItem(LootrRegistry.getTrophyBlock(), p), new Item.Properties().rarity(Rarity.EPIC).setId(LootrRegistry.TROPHY_ITEM_KEY));

  public static void register(IEventBus bus) {
    REGISTER.register(bus);
  }
}
