package net.zestyblaze.lootr.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.client.block.LootrChestBlockRenderer;
import net.zestyblaze.lootr.client.item.LootrChestItemRenderer;
import net.zestyblaze.lootr.config.LootrModConfig;
import net.zestyblaze.lootr.registry.LootrBlockEntityInit;
import net.zestyblaze.lootr.registry.LootrBlockInit;
import net.zestyblaze.lootr.registry.LootrNetworkingInit;

@Environment(EnvType.CLIENT)
public class LootrClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LootrNetworkingInit.registerClientNetwork();

        BlockEntityRendererRegistry.register(LootrBlockEntityInit.SPECIAL_LOOT_CHEST, LootrChestBlockRenderer::new);
        BuiltinItemRendererRegistry.INSTANCE.register(LootrBlockInit.CHEST, LootrChestItemRenderer.getInstance());

        if(LootrModConfig.get().debug.debugMode) {
            LootrAPI.LOG.info("Lootr: Registry - Client Fully Loaded!");
        }
    }
}
