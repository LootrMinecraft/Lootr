package noobanidus.mods.lootr.setup;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.client.BarrelModel;
import noobanidus.mods.lootr.client.LootrChestBlockRenderer;
import noobanidus.mods.lootr.client.LootrChestCartRenderer;
import noobanidus.mods.lootr.client.LootrShulkerBlockRenderer;
import noobanidus.mods.lootr.init.ModBlockEntities;
import noobanidus.mods.lootr.init.ModEntities;

public class ClientSetup {
  public static void init(FMLClientSetupEvent event) {
  }

  @SuppressWarnings("deprecation")
  public static void stitch(TextureStitchEvent.Pre event) {
    // TODO ?
    if (event.getMap().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
      event.addSprite(LootrChestBlockRenderer.MATERIAL.texture());
      event.addSprite(LootrChestBlockRenderer.MATERIAL2.texture());
      event.addSprite(LootrShulkerBlockRenderer.MATERIAL.texture());
      event.addSprite(LootrShulkerBlockRenderer.MATERIAL2.texture());
    }
  }

  public static void modelRegister(ModelRegistryEvent event) {
    ModelLoaderRegistry.registerLoader(new ResourceLocation(Lootr.MODID, "barrel"), BarrelModel.Loader.INSTANCE);
  }

  public static void registerRenderers (EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(ModBlockEntities.SPECIAL_LOOT_CHEST, LootrChestBlockRenderer::new);
    event.registerBlockEntityRenderer(ModBlockEntities.SPECIAL_TRAPPED_LOOT_CHEST, LootrChestBlockRenderer::new);
    event.registerBlockEntityRenderer(ModBlockEntities.SPECIAL_LOOT_INVENTORY, LootrChestBlockRenderer::new);
    event.registerBlockEntityRenderer(ModBlockEntities.SPECIAL_LOOT_SHULKER, LootrShulkerBlockRenderer::new);
    event.registerEntityRenderer(ModEntities.LOOTR_MINECART_ENTITY, (e) -> new LootrChestCartRenderer<>(e, ModelLayers.CHEST_MINECART));
  }
}
