package noobanidus.mods.lootr.setup;

import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import noobanidus.mods.lootr.client.LootrMinecartRenderer;
import noobanidus.mods.lootr.client.SpecialLootChestTileRenderer;
import noobanidus.mods.lootr.init.ModEntities;
import noobanidus.mods.lootr.init.ModTiles;

public class ClientSetup {
  public static void init(FMLClientSetupEvent event) {
    ClientRegistry.bindTileEntityRenderer(ModTiles.SPECIAL_LOOT_CHEST, SpecialLootChestTileRenderer::new);
    ClientRegistry.bindTileEntityRenderer(ModTiles.SPECIAL_TRAPPED_LOOT_CHEST, SpecialLootChestTileRenderer::new);
    ClientRegistry.bindTileEntityRenderer(ModTiles.SPECIAL_LOOT_INVENTORY, SpecialLootChestTileRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(ModEntities.LOOTR_MINECART_ENTITY, LootrMinecartRenderer::new);
  }

  @SuppressWarnings("deprecation")
  public static void stitch(TextureStitchEvent.Pre event) {
    if (event.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE)) {
      event.addSprite(SpecialLootChestTileRenderer.MATERIAL.getTextureLocation());
      event.addSprite(SpecialLootChestTileRenderer.MATERIAL2.getTextureLocation());
    }
  }
}
