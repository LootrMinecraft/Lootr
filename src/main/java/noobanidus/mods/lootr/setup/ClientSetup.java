package noobanidus.mods.lootr.setup;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import noobanidus.mods.lootr.client.SpecialLootBarrelTileRenderer;
import noobanidus.mods.lootr.client.SpecialLootChestTileRenderer;
import noobanidus.mods.lootr.tiles.SpecialLootBarrelTile;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;

public class ClientSetup {
  public static void init(FMLClientSetupEvent event) {
    ClientRegistry.bindTileEntitySpecialRenderer(SpecialLootChestTile.class, new SpecialLootChestTileRenderer<>());
    ClientRegistry.bindTileEntitySpecialRenderer(SpecialLootBarrelTile.class, new SpecialLootBarrelTileRenderer<>());
  }
}
