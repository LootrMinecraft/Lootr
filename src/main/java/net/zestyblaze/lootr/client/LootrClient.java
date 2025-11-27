package net.zestyblaze.lootr.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.zestyblaze.lootr.client.block.BarrelModel;
import net.zestyblaze.lootr.client.block.LootrChestBlockRenderer;
import net.zestyblaze.lootr.client.block.LootrShulkerBlockRenderer;
import net.zestyblaze.lootr.client.entity.LootrChestCartRenderer;
import net.zestyblaze.lootr.client.item.LootrChestItemRenderer;
import net.zestyblaze.lootr.client.item.LootrShulkerItemRenderer;
import net.zestyblaze.lootr.init.ModBlockEntities;
import net.zestyblaze.lootr.init.ModBlocks;
import net.zestyblaze.lootr.init.ModEntities;
import net.zestyblaze.lootr.network.LootrNetworkingInit;

@Environment(EnvType.CLIENT)
public class LootrClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    LootrNetworkingInit.registerClientNetwork();

    BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BARREL, RenderType.cutoutMipped());

    BlockEntityRenderers.register(ModBlockEntities.SPECIAL_LOOT_CHEST, LootrChestBlockRenderer::new);
    BuiltinItemRendererRegistry.INSTANCE.register(ModBlocks.CHEST, LootrChestItemRenderer.getInstance().getDynamicItemRenderer());

    BlockEntityRenderers.register(ModBlockEntities.SPECIAL_TRAPPED_LOOT_CHEST, LootrChestBlockRenderer::new);
    BuiltinItemRendererRegistry.INSTANCE.register(ModBlocks.TRAPPED_CHEST, LootrChestItemRenderer.getInstance().getDynamicItemRenderer());

    BlockEntityRenderers.register(ModBlockEntities.SPECIAL_LOOT_INVENTORY, LootrChestBlockRenderer::new);
    BuiltinItemRendererRegistry.INSTANCE.register(ModBlocks.INVENTORY, LootrChestItemRenderer.getInstance().getDynamicItemRenderer());

    BlockEntityRenderers.register(ModBlockEntities.SPECIAL_LOOT_SHULKER, LootrShulkerBlockRenderer::new);
    BuiltinItemRendererRegistry.INSTANCE.register(ModBlocks.SHULKER, LootrShulkerItemRenderer.getInstance().getDynamicItemRenderer());

    ModelLoadingRegistry.INSTANCE.registerResourceProvider(o -> new BarrelModel.BarrelModelLoader());

    EntityRendererRegistry.register(ModEntities.LOOTR_MINECART_ENTITY, (context) -> new LootrChestCartRenderer<>(context, ModelLayers.CHEST_MINECART));
  }
}
