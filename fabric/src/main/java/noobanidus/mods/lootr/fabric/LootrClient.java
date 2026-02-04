package noobanidus.mods.lootr.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.SpecialBlockRendererRegistry;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.client.block.LootrBrushableBlockRenderer;
import noobanidus.mods.lootr.common.client.block.LootrChestBlockRenderer;
import noobanidus.mods.lootr.common.client.block.LootrDecoratedPotRenderer;
import noobanidus.mods.lootr.common.client.block.LootrShulkerBoxRenderer;
import noobanidus.mods.lootr.common.client.entity.LootrChestCartRenderer;
import noobanidus.mods.lootr.common.client.entity.LootrItemFrameRenderer;
import noobanidus.mods.lootr.common.client.particle.UnopenedParticle;
import noobanidus.mods.lootr.common.client.select.SelectConfigType;
import noobanidus.mods.lootr.common.client.special.LootrChestSpecialRenderer;
import noobanidus.mods.lootr.common.client.special.LootrDecoratedPotSpecialRenderer;
import noobanidus.mods.lootr.common.client.special.LootrShulkerSpecialRenderer;
import noobanidus.mods.lootr.fabric.client.block.UnbakedBrushableModel;
import noobanidus.mods.lootr.fabric.client.block.UnbakedCustomModel;
import noobanidus.mods.lootr.fabric.init.ModBlockEntities;
import noobanidus.mods.lootr.fabric.init.ModBlocks;
import noobanidus.mods.lootr.fabric.init.ModEntities;
import noobanidus.mods.lootr.fabric.init.ModParticles;
import noobanidus.mods.lootr.fabric.network.LootrClientNetworkingInit;

@Environment(EnvType.CLIENT)
public class LootrClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    LootrClientNetworkingInit.register();

    BlockRenderLayerMap.putBlock(ModBlocks.CHEST, ChunkSectionLayer.CUTOUT);
    BlockRenderLayerMap.putBlock(ModBlocks.TRAPPED_CHEST, ChunkSectionLayer.CUTOUT);
    BlockRenderLayerMap.putBlock(ModBlocks.INVENTORY, ChunkSectionLayer.CUTOUT);
    BlockRenderLayerMap.putBlock(ModBlocks.SHULKER, ChunkSectionLayer.CUTOUT);
    BlockRenderLayerMap.putBlock(ModBlocks.BARREL, ChunkSectionLayer.CUTOUT);
    BlockRenderLayerMap.putBlock(ModBlocks.TROPHY, ChunkSectionLayer.CUTOUT);

    BlockEntityRenderers.register(ModBlockEntities.LOOTR_CHEST, LootrChestBlockRenderer::new);
    BlockEntityRenderers.register(ModBlockEntities.LOOTR_TRAPPED_CHEST, LootrChestBlockRenderer::new);
    BlockEntityRenderers.register(ModBlockEntities.LOOTR_INVENTORY, LootrChestBlockRenderer::new);
    BlockEntityRenderers.register(ModBlockEntities.LOOTR_SHULKER, LootrShulkerBoxRenderer::new);
    BlockEntityRenderers.register(ModBlockEntities.LOOTR_BRUSHABLE_BLOCK, LootrBrushableBlockRenderer::new);
    BlockEntityRenderers.register(ModBlockEntities.LOOTR_DECORATED_POT, LootrDecoratedPotRenderer::new);

    SpecialModelRenderers.ID_MAPPER.put(LootrAPI.rl("chest"), LootrChestSpecialRenderer.Unbaked.MAP_CODEC);
    SpecialModelRenderers.ID_MAPPER.put(LootrAPI.rl("shulker_box"), LootrShulkerSpecialRenderer.Unbaked.MAP_CODEC);
    SpecialModelRenderers.ID_MAPPER.put(LootrAPI.rl("decorated_pot"), LootrDecoratedPotSpecialRenderer.Unbaked.MAP_CODEC);

    SpecialBlockRendererRegistry.register(ModBlocks.TRAPPED_CHEST, LootrChestSpecialRenderer.Unbaked.trappedChest());
    SpecialBlockRendererRegistry.register(ModBlocks.CHEST, LootrChestSpecialRenderer.Unbaked.chest());
    SpecialBlockRendererRegistry.register(ModBlocks.INVENTORY, LootrChestSpecialRenderer.Unbaked.chest());
    SpecialBlockRendererRegistry.register(ModBlocks.SHULKER, LootrShulkerSpecialRenderer.Unbaked.shulker());
    SpecialBlockRendererRegistry.register(ModBlocks.DECORATED_POT, LootrDecoratedPotSpecialRenderer.Unbaked.decoratedPot());

    CustomUnbakedBlockStateModel.register(LootrAPI.rl("custom"), UnbakedCustomModel.CODEC);
    CustomUnbakedBlockStateModel.register(LootrAPI.rl("brushable"), UnbakedBrushableModel.CODEC);

    EntityRenderers.register(ModEntities.LOOTR_MINECART_ENTITY, (context) -> new LootrChestCartRenderer<>(context, ModelLayers.CHEST_MINECART));
    EntityRenderers.register(ModEntities.ITEM_FRAME, LootrItemFrameRenderer::new);

    EntityModelLayerRegistry.registerModelLayer(LootrDecoratedPotRenderer.OPEN_POT_LAYER, LootrDecoratedPotRenderer::createBodyLayer);

    ParticleFactoryRegistry.getInstance().register(ModParticles.UNOPENED_PARTCLE, UnopenedParticle.Provider::new);

    SelectItemModelProperties.ID_MAPPER.put(LootrAPI.rl("config_type"), SelectConfigType.TYPE);
  }
}
