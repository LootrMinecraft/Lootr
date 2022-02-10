package noobanidus.mods.lootr.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import noobanidus.libs.shoulders.client.bootstrap.Bootstrap;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.client.block.BarrelModel;
import noobanidus.mods.lootr.client.block.SpecialLootChestTileRenderer;
import noobanidus.mods.lootr.client.block.SpecialLootShulkerTileRenderer;
import noobanidus.mods.lootr.client.entity.LootrMinecartRenderer;
import noobanidus.mods.lootr.init.ModEntities;
import noobanidus.mods.lootr.init.ModTiles;

@Mod.EventBusSubscriber(modid = Lootr.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
  @SubscribeEvent
  public static void init(FMLClientSetupEvent event) {
    ClientRegistry.bindTileEntityRenderer(ModTiles.LOOT_CHEST, SpecialLootChestTileRenderer::new);
    ClientRegistry.bindTileEntityRenderer(ModTiles.TRAPPED_LOOT_CHEST, SpecialLootChestTileRenderer::new);
    ClientRegistry.bindTileEntityRenderer(ModTiles.LOOT_INVENTORY, SpecialLootChestTileRenderer::new);
    ClientRegistry.bindTileEntityRenderer(ModTiles.LOOK_SHULKER, SpecialLootShulkerTileRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(ModEntities.LOOTR_MINECART_ENTITY, LootrMinecartRenderer::new);
    event.enqueueWork(() -> {
      Bootstrap.init(Minecraft.getInstance());
    });
  }

  @SubscribeEvent
  public static void stitch(TextureStitchEvent.Pre event) {
    if (event.getMap().location().equals(AtlasTexture.LOCATION_BLOCKS)) {
      event.addSprite(SpecialLootChestTileRenderer.MATERIAL.texture());
      event.addSprite(SpecialLootChestTileRenderer.MATERIAL2.texture());
      event.addSprite(SpecialLootShulkerTileRenderer.MATERIAL.texture());
      event.addSprite(SpecialLootShulkerTileRenderer.MATERIAL2.texture());
    }
  }

  @SubscribeEvent
  public static void modelRegister(ModelRegistryEvent event) {
    ModelLoaderRegistry.registerLoader(new ResourceLocation(Lootr.MODID, "barrel"), BarrelModel.Loader.INSTANCE);
  }
}
