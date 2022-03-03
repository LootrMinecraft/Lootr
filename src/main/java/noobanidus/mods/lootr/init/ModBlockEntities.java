package noobanidus.mods.lootr.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.block.entities.*;

@Mod.EventBusSubscriber(modid = LootrAPI.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlockEntities {
  public static BlockEntityType<LootrChestBlockEntity> SPECIAL_LOOT_CHEST;
  public static BlockEntityType<LootrTrappedChestBlockEntity> SPECIAL_TRAPPED_LOOT_CHEST;
  public static BlockEntityType<LootrBarrelBlockEntity> SPECIAL_LOOT_BARREL;
  public static BlockEntityType<LootrInventoryBlockEntity> SPECIAL_LOOT_INVENTORY;
  public static BlockEntityType<LootrShulkerBlockEntity> SPECIAL_LOOT_SHULKER;

  public static void construct() {
    SPECIAL_LOOT_CHEST = BlockEntityType.Builder.of(LootrChestBlockEntity::new, ModBlocks.CHEST).build(null);
    SPECIAL_TRAPPED_LOOT_CHEST = BlockEntityType.Builder.of(LootrTrappedChestBlockEntity::new, ModBlocks.TRAPPED_CHEST).build(null);
    SPECIAL_LOOT_BARREL = BlockEntityType.Builder.of(LootrBarrelBlockEntity::new, ModBlocks.BARREL).build(null);
    SPECIAL_LOOT_INVENTORY = BlockEntityType.Builder.of(LootrInventoryBlockEntity::new, ModBlocks.INVENTORY).build(null);
    SPECIAL_LOOT_SHULKER = BlockEntityType.Builder.of(LootrShulkerBlockEntity::new, ModBlocks.SHULKER).build(null);
    SPECIAL_LOOT_CHEST.setRegistryName(LootrAPI.MODID, "special_loot_chest");
    SPECIAL_TRAPPED_LOOT_CHEST.setRegistryName(LootrAPI.MODID, "special_trapped_loot_chest");
    SPECIAL_LOOT_BARREL.setRegistryName(LootrAPI.MODID, "special_loot_barrel");
    SPECIAL_LOOT_INVENTORY.setRegistryName(LootrAPI.MODID, "special_loot_inventory");
    SPECIAL_LOOT_SHULKER.setRegistryName(LootrAPI.MODID, "special_loot_shulker");
  }

  @SubscribeEvent
  public static void registerTileEntityType(RegistryEvent.Register<BlockEntityType<?>> event) {
    construct();
    event.getRegistry().registerAll(SPECIAL_LOOT_CHEST, SPECIAL_TRAPPED_LOOT_CHEST, SPECIAL_LOOT_BARREL, SPECIAL_LOOT_INVENTORY, SPECIAL_LOOT_SHULKER);
  }
}
