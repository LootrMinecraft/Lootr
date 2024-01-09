package noobanidus.mods.lootr.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import noobanidus.mods.lootr.client.block.BarrelModel;
import noobanidus.mods.lootr.client.block.LootrChestBlockRenderer;
import noobanidus.mods.lootr.client.block.LootrShulkerBlockRenderer;
import noobanidus.mods.lootr.client.entity.LootrChestCartRenderer;
import noobanidus.mods.lootr.client.item.LootrChestItemRenderer;
import noobanidus.mods.lootr.client.item.LootrShulkerItemRenderer;
import noobanidus.mods.lootr.registry.LootrBlockEntityInit;
import noobanidus.mods.lootr.registry.LootrBlockInit;
import noobanidus.mods.lootr.registry.LootrEntityInit;
import noobanidus.mods.lootr.registry.LootrNetworkingInit;

public class LootrClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        initModels();
    }

    public static void initModels() {
        LootrNetworkingInit.registerClientNetwork();

        BlockEntityRenderers.register(LootrBlockEntityInit.LOOT_CHEST_ENTITY_PROVIDER.get(), LootrChestBlockRenderer::new);
        BuiltinItemRendererRegistry.INSTANCE.register(LootrBlockInit.CHEST.get(), LootrChestItemRenderer.getInstance());

        BlockEntityRenderers.register(LootrBlockEntityInit.TRAPPED_LOOT_CHEST_ENTITY_PROVIDER.get(), LootrChestBlockRenderer::new);
        BuiltinItemRendererRegistry.INSTANCE.register(LootrBlockInit.TRAPPED_CHEST.get(), LootrChestItemRenderer.getInstance());

        BlockEntityRenderers.register(LootrBlockEntityInit.LOOT_SHULKER_ENTITY_PROVIDER.get(), LootrShulkerBlockRenderer::new);
        BuiltinItemRendererRegistry.INSTANCE.register(LootrBlockInit.SHULKER.get(), LootrShulkerItemRenderer.getInstance());

        BlockEntityRenderers.register(LootrBlockEntityInit.LOOT_INVENTORY_ENTITY_PROVIDER.get(), LootrChestBlockRenderer::new);
        BuiltinItemRendererRegistry.INSTANCE.register(LootrBlockInit.INVENTORY.get(), LootrChestItemRenderer.getInstance());

        ModelLoadingRegistry.INSTANCE.registerResourceProvider(o -> new BarrelModel.BarrelModelLoader());

        EntityRendererRegistry.register(LootrEntityInit.LOOTR_MINECART_ENTITY_PROVIDER.get(), (context) -> new LootrChestCartRenderer<>(context, ModelLayers.CHEST_MINECART));
    }
}
