package net.zestyblaze.lootr.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.geom.ModelLayers;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.client.block.BarrelModel;
import net.zestyblaze.lootr.client.block.LootrChestBlockRenderer;
import net.zestyblaze.lootr.client.block.LootrShulkerBlockRenderer;
import net.zestyblaze.lootr.client.entity.LootrChestCartRenderer;
import net.zestyblaze.lootr.client.item.LootrChestItemRenderer;
import net.zestyblaze.lootr.client.item.LootrShulkerItemRenderer;
import net.zestyblaze.lootr.config.LootrModConfig;
import net.zestyblaze.lootr.registry.LootrBlockEntityInit;
import net.zestyblaze.lootr.registry.LootrBlockInit;
import net.zestyblaze.lootr.registry.LootrEntityInit;
import net.zestyblaze.lootr.registry.LootrNetworkingInit;

@Environment(EnvType.CLIENT)
public class LootrClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LootrNetworkingInit.registerClientNetwork();

        BlockEntityRendererRegistry.register(LootrBlockEntityInit.SPECIAL_LOOT_CHEST, LootrChestBlockRenderer::new);
        BuiltinItemRendererRegistry.INSTANCE.register(LootrBlockInit.CHEST, LootrChestItemRenderer.getInstance());

        BlockEntityRendererRegistry.register(LootrBlockEntityInit.SPECIAL_TRAPPED_LOOT_CHEST, LootrChestBlockRenderer::new);
        BuiltinItemRendererRegistry.INSTANCE.register(LootrBlockInit.TRAPPED_CHEST, LootrChestItemRenderer.getInstance());

        BlockEntityRendererRegistry.register(LootrBlockEntityInit.SPECIAL_LOOT_INVENTORY, LootrChestBlockRenderer::new);
        BuiltinItemRendererRegistry.INSTANCE.register(LootrBlockInit.INVENTORY, LootrChestItemRenderer.getInstance());

        BlockEntityRendererRegistry.register(LootrBlockEntityInit.SPECIAL_LOOT_SHULKER, LootrShulkerBlockRenderer::new);
        BuiltinItemRendererRegistry.INSTANCE.register(LootrBlockInit.SHULKER, LootrShulkerItemRenderer.getInstance());

        ModelLoadingRegistry.INSTANCE.registerResourceProvider(o -> new BarrelModel.BarrelModelLoader());

        EntityRendererRegistry.register(LootrEntityInit.LOOTR_MINECART_ENTITY, (context) -> new  LootrChestCartRenderer<>(context, ModelLayers.CHEST_MINECART));

        if(LootrModConfig.get().debug.debugMode) {
            LootrAPI.LOG.info("Lootr: Registry - Client Fully Loaded!");
        }
    }
}
