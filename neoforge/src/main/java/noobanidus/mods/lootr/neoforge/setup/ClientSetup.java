package noobanidus.mods.lootr.neoforge.setup;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.registry.LootrProperties;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.*;
import noobanidus.mods.lootr.common.client.block.LootrBrushableBlockRenderer;
import noobanidus.mods.lootr.common.client.block.LootrDecoratedPotRenderer;
import noobanidus.mods.lootr.common.client.entity.LootrChestCartRenderer;
import noobanidus.mods.lootr.common.client.entity.LootrItemFrameRenderer;
import noobanidus.mods.lootr.common.client.item.LootrChestItemRenderer;
import noobanidus.mods.lootr.common.client.item.LootrDecoratedPotItemRenderer;
import noobanidus.mods.lootr.common.client.item.LootrShulkerItemRenderer;
import noobanidus.mods.lootr.common.client.item.LootrTrappedChestItemRenderer;
import noobanidus.mods.lootr.common.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.common.entity.LootrItemFrame;
import noobanidus.mods.lootr.neoforge.client.block.*;

@EventBusSubscriber(modid = LootrAPI.MODID, value = Dist.CLIENT)
public class ClientSetup {
  @SubscribeEvent
  public static void modelAdditional (ModelEvent.RegisterAdditional event) {
    event.register(LootrItemFrameRenderer.FRAME_LOCATION);
    event.register(LootrItemFrameRenderer.FRAME_OPEN_LOCATION);
  }

  @SubscribeEvent
  public static void modelRegister(ModelEvent.RegisterGeometryLoaders event) {
    // "custom_barrel" is deprecated
    event.register(LootrAPI.rl("custom_barrel"), CustomModel.Loader.BARREL_INSTANCE);
    event.register(LootrAPI.rl("custom_model"), CustomModel.Loader.INSTANCE);
    event.register(LootrAPI.rl("barrel"), BarrelModel.Loader.INSTANCE);
    event.register(LootrAPI.rl("brushable"), BrushableModel.Loader.INSTANCE);
  }

  @SuppressWarnings("unchecked")
  @SubscribeEvent
  public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer((BlockEntityType<LootrChestBlockEntity>) LootrRegistry.getChestBlockEntity(), LootrChestBlockRenderer::new);
    event.registerBlockEntityRenderer((BlockEntityType<LootrTrappedChestBlockEntity>) LootrRegistry.getTrappedChestBlockEntity(), LootrChestBlockRenderer::new);
    event.registerBlockEntityRenderer((BlockEntityType<LootrChestBlockEntity>) LootrRegistry.getChestBlockEntity(), LootrChestBlockRenderer::new);
    event.registerBlockEntityRenderer((BlockEntityType<LootrShulkerBlockEntity>) LootrRegistry.getShulkerBlockEntity(), LootrShulkerBlockRenderer::new);
    event.registerBlockEntityRenderer((BlockEntityType<LootrInventoryBlockEntity>) LootrRegistry.getInventoryBlockEntity(), LootrChestBlockRenderer::new);
    event.registerEntityRenderer((EntityType<LootrChestMinecartEntity>) LootrRegistry.getMinecart(), (e) -> new LootrChestCartRenderer<>(e, ModelLayers.CHEST_MINECART));
    event.registerBlockEntityRenderer((BlockEntityType<LootrBrushableBlockEntity>) LootrRegistry.getBrushableBlockEntity(), LootrBrushableBlockRenderer::new);
    event.registerBlockEntityRenderer((BlockEntityType<LootrDecoratedPotBlockEntity>) LootrRegistry.getDecoratedPotBlockEntity(), LootrDecoratedPotRenderer::new);
    event.registerEntityRenderer((EntityType<LootrItemFrame>) LootrRegistry.getItemFrame(), LootrItemFrameRenderer::new);
  }

  @SubscribeEvent
  public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
    IClientItemExtensions chest = new IClientItemExtensions() {
      @Override
      public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return LootrChestItemRenderer.getInstance();
      }
    };
    event.registerItem(new IClientItemExtensions() {
      @Override
      public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return LootrShulkerItemRenderer.getInstance();
      }
    }, LootrRegistry.getShulkerItem());
    event.registerItem(new IClientItemExtensions() {
      @Override
      public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return LootrTrappedChestItemRenderer.getInstance();
      }
    }, LootrRegistry.getTrappedChestItem());
    event.registerItem(new IClientItemExtensions() {
      @Override
      public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return LootrDecoratedPotItemRenderer.getInstance();
      }
    }, LootrRegistry.getDecoratedPotItem());
    event.registerItem(chest, LootrRegistry.getChestItem());
    event.registerItem(chest, LootrRegistry.getInventoryItem());
  }

  @SubscribeEvent
  public static void registerLayersEvent (EntityRenderersEvent.RegisterLayerDefinitions event) {
    event.registerLayerDefinition(LootrDecoratedPotRenderer.OPEN_POT_LAYER, LootrDecoratedPotRenderer::createBodyLayer);
  }
}
