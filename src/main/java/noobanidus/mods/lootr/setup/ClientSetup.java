package noobanidus.mods.lootr.setup;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.client.block.BarrelModel;
import noobanidus.mods.lootr.client.block.LootrChestBlockRenderer;
import noobanidus.mods.lootr.client.entity.LootrChestCartRenderer;
import noobanidus.mods.lootr.client.block.LootrShulkerBlockRenderer;
import noobanidus.mods.lootr.init.ModBlockEntities;
import noobanidus.mods.lootr.init.ModEntities;

@Mod.EventBusSubscriber(modid=Lootr.MODID, value= Dist.CLIENT, bus= Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
  @SubscribeEvent
  public static void stitch(TextureStitchEvent.Pre event) {
    if (event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
      event.addSprite(LootrChestBlockRenderer.MATERIAL.texture());
      event.addSprite(LootrChestBlockRenderer.MATERIAL2.texture());
      event.addSprite(LootrShulkerBlockRenderer.MATERIAL.texture());
      event.addSprite(LootrShulkerBlockRenderer.MATERIAL2.texture());
    }
  }

  @SubscribeEvent
  public static void modelRegister(ModelRegistryEvent event) {
    ModelLoaderRegistry.registerLoader(new ResourceLocation(Lootr.MODID, "barrel"), BarrelModel.Loader.INSTANCE);
  }

  @SubscribeEvent
  public static void registerRenderers (EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(ModBlockEntities.SPECIAL_LOOT_CHEST, LootrChestBlockRenderer::new);
    event.registerBlockEntityRenderer(ModBlockEntities.SPECIAL_TRAPPED_LOOT_CHEST, LootrChestBlockRenderer::new);
    event.registerBlockEntityRenderer(ModBlockEntities.SPECIAL_LOOT_INVENTORY, LootrChestBlockRenderer::new);
    event.registerBlockEntityRenderer(ModBlockEntities.SPECIAL_LOOT_SHULKER, LootrShulkerBlockRenderer::new);
    event.registerEntityRenderer(ModEntities.LOOTR_MINECART_ENTITY, (e) -> new LootrChestCartRenderer<>(e, ModelLayers.CHEST_MINECART));
  }
}
