package noobanidus.mods.lootr.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.SpecialBlockRendererRegistry;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.client.block.LootrChestBlockRenderer;
import noobanidus.mods.lootr.common.client.block.LootrShulkerBlockRenderer;
import noobanidus.mods.lootr.common.client.entity.LootrChestCartRenderer;
import noobanidus.mods.lootr.common.client.special.LootrChestSpecialRenderer;
import noobanidus.mods.lootr.common.client.special.LootrShulkerSpecialRenderer;
import noobanidus.mods.lootr.fabric.client.block.BarrelModelLoader;
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
    SpecialBlockRendererRegistry.register(ModBlocks.CHEST, LootrChestSpecialRenderer.Unbaked.chest());
    SpecialModelRenderers.ID_MAPPER.put(LootrAPI.rl("chest"), LootrChestSpecialRenderer.Unbaked.MAP_CODEC);

    BlockEntityRenderers.register(ModBlockEntities.LOOTR_TRAPPED_CHEST, LootrChestBlockRenderer::new);
    SpecialBlockRendererRegistry.register(ModBlocks.TRAPPED_CHEST, LootrChestSpecialRenderer.Unbaked.trappedChest());

    BlockEntityRenderers.register(ModBlockEntities.LOOTR_INVENTORY, LootrChestBlockRenderer::new);
    SpecialBlockRendererRegistry.register(ModBlocks.INVENTORY, LootrChestSpecialRenderer.Unbaked.chest());

    BlockEntityRenderers.register(ModBlockEntities.LOOTR_SHULKER, LootrShulkerBlockRenderer::new);
    SpecialBlockRendererRegistry.register(ModBlocks.SHULKER, LootrShulkerSpecialRenderer.Unbaked.shulker());
    SpecialModelRenderers.ID_MAPPER.put(LootrAPI.rl("shulker"), LootrShulkerSpecialRenderer.Unbaked.MAP_CODEC);

    ModelLoadingPlugin.register(BarrelModelLoader.getInstance());

    EntityRendererRegistry.register(ModEntities.LOOTR_MINECART_ENTITY, (context) -> new LootrChestCartRenderer<>(context, ModelLayers.CHEST_MINECART));
  }
}
