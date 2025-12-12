package noobanidus.mods.lootr.neoforge.setup;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
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
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.*;
import noobanidus.mods.lootr.common.client.block.LootrBrushableBlockRenderer;
import noobanidus.mods.lootr.common.client.entity.LootrChestCartRenderer;
import noobanidus.mods.lootr.common.client.item.LootrChestItemRenderer;
import noobanidus.mods.lootr.common.client.item.LootrShulkerItemRenderer;
import noobanidus.mods.lootr.common.client.item.LootrTrappedChestItemRenderer;
import noobanidus.mods.lootr.common.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.neoforge.client.block.*;

@EventBusSubscriber(modid = LootrAPI.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {

  @SubscribeEvent
  public static void modelRegister(ModelEvent.RegisterGeometryLoaders event) {
    event.register(LootrAPI.rl("custom_barrel"), CustomBarrelModel.Loader.INSTANCE);
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
    event.registerItem(chest, LootrRegistry.getChestItem());
    event.registerItem(chest, LootrRegistry.getInventoryItem());
  }
}
