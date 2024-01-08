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

        BlockEntityRenderers.register(LootrBlockEntityInit.SPECIAL_LOOT_CHEST, LootrChestBlockRenderer::new);
        BuiltinItemRendererRegistry.INSTANCE.register(LootrBlockInit.CHEST, LootrChestItemRenderer.getInstance());

        BlockEntityRenderers.register(LootrBlockEntityInit.SPECIAL_TRAPPED_LOOT_CHEST, LootrChestBlockRenderer::new);
        BuiltinItemRendererRegistry.INSTANCE.register(LootrBlockInit.TRAPPED_CHEST, LootrChestItemRenderer.getInstance());

        BlockEntityRenderers.register(LootrBlockEntityInit.SPECIAL_LOOT_INVENTORY, LootrChestBlockRenderer::new);
        BuiltinItemRendererRegistry.INSTANCE.register(LootrBlockInit.INVENTORY, LootrChestItemRenderer.getInstance());

        BlockEntityRenderers.register(LootrBlockEntityInit.SPECIAL_LOOT_SHULKER, LootrShulkerBlockRenderer::new);
        BuiltinItemRendererRegistry.INSTANCE.register(LootrBlockInit.SHULKER, LootrShulkerItemRenderer.getInstance());

        ModelLoadingRegistry.INSTANCE.registerResourceProvider(o -> new BarrelModel.BarrelModelLoader());

        EntityRendererRegistry.register(LootrEntityInit.LOOTR_MINECART_ENTITY, (context) -> new LootrChestCartRenderer<>(context, ModelLayers.CHEST_MINECART));
    }
}
