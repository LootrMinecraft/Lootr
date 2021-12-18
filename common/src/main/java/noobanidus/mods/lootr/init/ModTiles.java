package noobanidus.mods.lootr.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.tiles.*;

public class ModTiles {
  public static BlockEntityType<SpecialLootChestTile> SPECIAL_LOOT_CHEST = BlockEntityType.Builder.of(SpecialLootChestTile::new, ModBlocks.CHEST).build(null);
  public static BlockEntityType<SpecialTrappedLootChestTile> SPECIAL_TRAPPED_LOOT_CHEST = BlockEntityType.Builder.of(SpecialTrappedLootChestTile::new, ModBlocks.TRAPPED_CHEST).build(null);
  public static BlockEntityType<SpecialLootBarrelTile> SPECIAL_LOOT_BARREL = BlockEntityType.Builder.of(SpecialLootBarrelTile::new, ModBlocks.BARREL).build(null);
  public static BlockEntityType<SpecialLootInventoryTile> SPECIAL_LOOT_INVENTORY = BlockEntityType.Builder.of(SpecialLootInventoryTile::new, ModBlocks.INVENTORY).build(null);
  public static BlockEntityType<SpecialLootShulkerTile> SPECIAL_LOOT_SHULKER = BlockEntityType.Builder.of(SpecialLootShulkerTile::new, ModBlocks.SHULKER).build(null);

  public static void registerTileEntityType(RegistryEvent.Register<BlockEntityType<?>> event) {
    SPECIAL_LOOT_CHEST.setRegistryName(Lootr.MODID, "special_loot_chest");
    event.getRegistry().register(SPECIAL_LOOT_CHEST);
    SPECIAL_TRAPPED_LOOT_CHEST.setRegistryName(Lootr.MODID, "special_trapped_loot_chest");
    event.getRegistry().register(SPECIAL_TRAPPED_LOOT_CHEST);
    SPECIAL_LOOT_BARREL.setRegistryName(Lootr.MODID, "special_loot_barrel");
    event.getRegistry().register(SPECIAL_LOOT_BARREL);
    SPECIAL_LOOT_INVENTORY.setRegistryName(Lootr.MODID, "special_loot_inventory");
    event.getRegistry().register(SPECIAL_LOOT_INVENTORY);
    SPECIAL_LOOT_SHULKER.setRegistryName(Lootr.MODID, "special_loot_shulker");
    event.getRegistry().register(SPECIAL_LOOT_SHULKER);
  }
}
