package net.zestyblaze.lootr.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.client.block.LootrChestBlockRenderer;
import net.zestyblaze.lootr.config.LootrModConfig;
import net.zestyblaze.lootr.registry.LootrBlockEntityInit;
import net.zestyblaze.lootr.registry.LootrNetworkingInit;

@Environment(EnvType.CLIENT)
public class LootrClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LootrNetworkingInit.registerClientNetwork();

        BlockEntityRendererRegistry.register(LootrBlockEntityInit.SPECIAL_LOOT_CHEST, LootrChestBlockRenderer::new);

        if(LootrModConfig.get().debug.debugMode) {
            LootrAPI.LOG.info("Lootr: Registry - Client Fully Loaded!");
        }
    }
}
