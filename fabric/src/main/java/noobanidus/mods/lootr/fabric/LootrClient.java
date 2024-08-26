package noobanidus.mods.lootr.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import noobanidus.mods.lootr.fabric.client.block.BarrelModelLoader;
import noobanidus.mods.lootr.fabric.client.block.LootrChestBlockRenderer;
import noobanidus.mods.lootr.fabric.client.block.LootrShulkerBlockRenderer;
import noobanidus.mods.lootr.fabric.client.entity.LootrChestCartRenderer;
import noobanidus.mods.lootr.fabric.client.item.LootrChestItemRenderer;
import noobanidus.mods.lootr.fabric.client.item.LootrShulkerItemRenderer;
import noobanidus.mods.lootr.fabric.init.ModBlockEntities;
import noobanidus.mods.lootr.fabric.init.ModBlocks;
import noobanidus.mods.lootr.fabric.init.ModEntities;
import noobanidus.mods.lootr.fabric.network.LootrNetworkingInit;

@Environment(EnvType.CLIENT)
public class LootrClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    LootrNetworkingInit.registerClientNetwork();

    BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BARREL, RenderType.cutoutMipped());

    BlockEntityRenderers.register(ModBlockEntities.LOOTR_CHEST, LootrChestBlockRenderer::new);
    BuiltinItemRendererRegistry.INSTANCE.register(ModBlocks.CHEST, LootrChestItemRenderer.getInstance());

    BlockEntityRenderers.register(ModBlockEntities.LOOTR_TRAPPED_CHEST, LootrChestBlockRenderer::new);
    BuiltinItemRendererRegistry.INSTANCE.register(ModBlocks.TRAPPED_CHEST, LootrChestItemRenderer.getInstance());

    BlockEntityRenderers.register(ModBlockEntities.LOOTR_INVENTORY, LootrChestBlockRenderer::new);
    BuiltinItemRendererRegistry.INSTANCE.register(ModBlocks.INVENTORY, LootrChestItemRenderer.getInstance());

    BlockEntityRenderers.register(ModBlockEntities.LOOTR_SHULKER, LootrShulkerBlockRenderer::new);
    BuiltinItemRendererRegistry.INSTANCE.register(ModBlocks.SHULKER, LootrShulkerItemRenderer.getInstance());

    ModelLoadingPlugin.register(BarrelModelLoader.INSTANCE);

    EntityRendererRegistry.register(ModEntities.LOOTR_MINECART_ENTITY, (context) -> new LootrChestCartRenderer<>(context, ModelLayers.CHEST_MINECART));
  }
}
