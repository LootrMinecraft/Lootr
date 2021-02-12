package noobanidus.mods.lootr.init;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.tiles.SpecialLootBarrelTile;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;
import noobanidus.mods.lootr.tiles.SpecialLootInventoryTile;
import noobanidus.mods.lootr.tiles.SpecialTrappedLootChestTile;

public class ModTiles {
  public static TileEntityType<SpecialLootChestTile> SPECIAL_LOOT_CHEST = TileEntityType.Builder.create(SpecialLootChestTile::new, ModBlocks.CHEST).build(null);
  public static TileEntityType<SpecialTrappedLootChestTile> SPECIAL_TRAPPED_LOOT_CHEST = TileEntityType.Builder.create(SpecialTrappedLootChestTile::new, ModBlocks.TRAPPED_CHEST).build(null);
  public static TileEntityType<SpecialLootBarrelTile> SPECIAL_LOOT_BARREL = TileEntityType.Builder.create(SpecialLootBarrelTile::new, ModBlocks.BARREL).build(null);
  public static TileEntityType<SpecialLootInventoryTile> SPECIAL_LOOT_INVENTORY = TileEntityType.Builder.create(SpecialLootInventoryTile::new, ModBlocks.INVENTORY).build(null);

  public static void registerTileEntityType(RegistryEvent.Register<TileEntityType<?>> event) {
    SPECIAL_LOOT_CHEST.setRegistryName(Lootr.MODID, "special_loot_chest");
    event.getRegistry().register(SPECIAL_LOOT_CHEST);
    SPECIAL_TRAPPED_LOOT_CHEST.setRegistryName(Lootr.MODID, "special_trapped_loot_chest");
    event.getRegistry().register(SPECIAL_TRAPPED_LOOT_CHEST);
    SPECIAL_LOOT_BARREL.setRegistryName(Lootr.MODID, "special_loot_barrel");
    event.getRegistry().register(SPECIAL_LOOT_BARREL);
    SPECIAL_LOOT_INVENTORY.setRegistryName(Lootr.MODID, "special_loot_inventory");
    event.getRegistry().register(SPECIAL_LOOT_INVENTORY);
  }
}
