package noobanidus.mods.lootr.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.item.LootrChestBlockItem;
import noobanidus.mods.lootr.item.LootrShulkerBlockItem;

@Mod.EventBusSubscriber(modid = LootrAPI.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {
  private static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, LootrAPI.MODID);

  public static final RegistryObject<BlockItem> CHEST = REGISTER.register("lootr_chest", () -> new LootrChestBlockItem(ModBlocks.CHEST.get(), new BlockItem.Properties()));
  public static final RegistryObject<BlockItem> TRAPPED_CHEST = REGISTER.register("lootr_trapped_chest", () -> new LootrChestBlockItem(ModBlocks.TRAPPED_CHEST.get(), new BlockItem.Properties()));
  public static final RegistryObject<BlockItem> BARREL = REGISTER.register("lootr_barrel", () -> new BlockItem(ModBlocks.BARREL.get(), new BlockItem.Properties()));
  public static final RegistryObject<BlockItem> INVENTORY = REGISTER.register("lootr_inventory", () -> new LootrChestBlockItem(ModBlocks.INVENTORY.get(), new BlockItem.Properties()));
  public static final RegistryObject<BlockItem> SHULKER = REGISTER.register("lootr_shulker", () -> new LootrShulkerBlockItem(ModBlocks.SHULKER.get(), new BlockItem.Properties()));
  public static final RegistryObject<BlockItem> TROPHY = REGISTER.register("trophy", () -> new BlockItem(ModBlocks.TROPHY.get(), new Item.Properties().rarity(Rarity.EPIC).tab(Lootr.TAB)));

  public static void register (IEventBus bus) {
    REGISTER.register(bus);
  }
}
