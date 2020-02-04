package noobanidus.mods.lootr.init;

import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.tiles.SpecialLootBarrelTile;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;
import noobanidus.mods.lootr.tiles.SpecialTrappedLootChestTile;

public class ModTiles {
  public static TileEntityType<SpecialLootChestTile> SPECIAL_LOOT_CHEST = TileEntityType.Builder.create(SpecialLootChestTile::new, Blocks.CHEST).build(null);
  public static TileEntityType<SpecialTrappedLootChestTile> SPECIAL_TRAPPED_LOOT_CHEST = TileEntityType.Builder.create(SpecialTrappedLootChestTile::new, Blocks.TRAPPED_CHEST).build(null);
  public static TileEntityType<SpecialLootBarrelTile> SPECIAL_LOOT_BARREL = TileEntityType.Builder.create(SpecialLootBarrelTile::new, Blocks.BARREL, ModBlocks.BARREL).build(null);

  public static void registerTileEntityType(RegistryEvent.Register<TileEntityType<?>> event) {
    SPECIAL_LOOT_CHEST.setRegistryName(Lootr.MODID, "special_loot_chest");
    event.getRegistry().register(SPECIAL_LOOT_CHEST);
    SPECIAL_TRAPPED_LOOT_CHEST.setRegistryName(Lootr.MODID, "special_trapped_loot_chest");
    event.getRegistry().register(SPECIAL_TRAPPED_LOOT_CHEST);
    SPECIAL_LOOT_BARREL.setRegistryName(Lootr.MODID, "special_loot_barrel");
    event.getRegistry().register(SPECIAL_LOOT_BARREL);
  }
}
