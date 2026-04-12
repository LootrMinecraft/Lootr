package noobanidus.mods.lootr.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
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
import noobanidus.mods.lootr.common.client.particle.RefreshParticle;
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

    BlockEntityRenderers.register(ModBlockEntities.CHEST, LootrChestBlockRenderer::new);
    BlockEntityRenderers.register(ModBlockEntities.TRAPPED_CHEST, LootrChestBlockRenderer::new);
    BlockEntityRenderers.register(ModBlockEntities.SHULKER_BOX, LootrShulkerBoxRenderer::new);
    BlockEntityRenderers.register(ModBlockEntities.BRUSHABLE_BLOCK, LootrBrushableBlockRenderer::new);
    BlockEntityRenderers.register(ModBlockEntities.DECORATED_POT, LootrDecoratedPotRenderer::new);

    SpecialModelRenderers.ID_MAPPER.put(LootrAPI.rl("chest"), LootrChestSpecialRenderer.Unbaked.MAP_CODEC);
    SpecialModelRenderers.ID_MAPPER.put(LootrAPI.rl("shulker_box"), LootrShulkerSpecialRenderer.Unbaked.MAP_CODEC);
    SpecialModelRenderers.ID_MAPPER.put(LootrAPI.rl("decorated_pot"), LootrDecoratedPotSpecialRenderer.Unbaked.MAP_CODEC);

    // TODO: Check these
    CustomUnbakedBlockStateModel.register(UnbakedCustomModel.IDENTIFIER, UnbakedCustomModel.CODEC);
    CustomUnbakedBlockStateModel.register(UnbakedBrushableModel.IDENTIFIER, UnbakedBrushableModel.CODEC);

    EntityRenderers.register(ModEntities.MINECART_WITH_CHEST, (context) -> new LootrChestCartRenderer<>(context, ModelLayers.CHEST_MINECART));
    EntityRenderers.register(ModEntities.ITEM_FRAME, LootrItemFrameRenderer::new);

    ModelLayerRegistry.registerModelLayer(LootrDecoratedPotRenderer.OPEN_POT_LAYER, LootrDecoratedPotRenderer::createBodyLayer);

    ParticleProviderRegistry.getInstance().register(ModParticles.UNOPENED_PARTCLE, UnopenedParticle.Provider::new);
    ParticleProviderRegistry.getInstance().register(ModParticles.REFRESH_PARTICLE, RefreshParticle.Provider::new);

    SelectItemModelProperties.ID_MAPPER.put(SelectConfigType.IDENTIFIER, SelectConfigType.TYPE);
  }
}
