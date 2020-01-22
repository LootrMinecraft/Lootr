package noobanidus.mods.lootr.init;

import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;

public class ModTiles {
  public static TileEntityType<SpecialLootChestTile> SPECIAL_LOOT_CHEST = TileEntityType.Builder.create(SpecialLootChestTile::new, Blocks.CHEST, Blocks.TRAPPED_CHEST).build(null);

  public static void registerTypeEntityType (RegistryEvent.Register<TileEntityType<?>> event) {
    SPECIAL_LOOT_CHEST.setRegistryName(Lootr.MODID, "special_loot_chest");
    event.getRegistry().register(SPECIAL_LOOT_CHEST);
  }
}
