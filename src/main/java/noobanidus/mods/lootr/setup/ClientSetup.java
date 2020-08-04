package noobanidus.mods.lootr.setup;

import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import noobanidus.mods.lootr.client.SpecialLootChestTileRenderer;
import noobanidus.mods.lootr.init.ModTiles;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;

public class ClientSetup {
  public static void init(FMLClientSetupEvent event) {
    ClientRegistry.bindTileEntityRenderer(ModTiles.SPECIAL_LOOT_CHEST, SpecialLootChestTileRenderer::new);
    ClientRegistry.bindTileEntityRenderer(ModTiles.SPECIAL_TRAPPED_LOOT_CHEST, SpecialLootChestTileRenderer::new);
  }

  @SuppressWarnings("deprecation")
  public static void stitch (TextureStitchEvent.Pre event) {
    if (event.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE)) {
      event.addSprite(SpecialLootChestTileRenderer.MATERIAL.getTextureLocation());
    }
  }
}
