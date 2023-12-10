package noobanidus.mods.lootr.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.item.LootrChestBlockItem;
import noobanidus.mods.lootr.item.LootrShulkerBlockItem;

public class ModItems {
  private static final DeferredRegister<Item> REGISTER = DeferredRegister.create(BuiltInRegistries.ITEM, LootrAPI.MODID);

  public static final DeferredHolder<Item, BlockItem> CHEST = REGISTER.register("lootr_chest", () -> new LootrChestBlockItem(ModBlocks.CHEST.get(), new BlockItem.Properties()));
  public static final DeferredHolder<Item, BlockItem> TRAPPED_CHEST = REGISTER.register("lootr_trapped_chest", () -> new LootrChestBlockItem(ModBlocks.TRAPPED_CHEST.get(), new BlockItem.Properties()));
  public static final DeferredHolder<Item, BlockItem> BARREL = REGISTER.register("lootr_barrel", () -> new BlockItem(ModBlocks.BARREL.get(), new BlockItem.Properties()));
  public static final DeferredHolder<Item, BlockItem> INVENTORY = REGISTER.register("lootr_inventory", () -> new LootrChestBlockItem(ModBlocks.INVENTORY.get(), new BlockItem.Properties()));
  public static final DeferredHolder<Item, BlockItem> SHULKER = REGISTER.register("lootr_shulker", () -> new LootrShulkerBlockItem(ModBlocks.SHULKER.get(), new BlockItem.Properties()));
  public static final DeferredHolder<Item, BlockItem> TROPHY = REGISTER.register("trophy", () -> new BlockItem(ModBlocks.TROPHY.get(), new Item.Properties().rarity(Rarity.EPIC)));

  public static void register (IEventBus bus) {
    REGISTER.register(bus);
  }
}
