package noobanidus.mods.lootr.setup;

import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.client.BarrelModel;
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
    if (event.getMap().location().equals(AtlasTexture.LOCATION_BLOCKS)) {
      event.addSprite(SpecialLootChestTileRenderer.MATERIAL.texture());
      event.addSprite(SpecialLootChestTileRenderer.MATERIAL2.texture());
    }
  }

  public static void modelRegister(ModelRegistryEvent event) {
    ModelLoaderRegistry.registerLoader(new ResourceLocation(Lootr.MODID, "barrel"), BarrelModel.Loader.INSTANCE);
  }
}
