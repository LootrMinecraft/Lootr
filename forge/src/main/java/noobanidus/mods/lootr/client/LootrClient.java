package noobanidus.mods.lootr.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.client.block.BarrelModel;
import noobanidus.mods.lootr.client.block.LootrChestBlockRenderer;
import noobanidus.mods.lootr.client.block.LootrShulkerBlockRenderer;
import noobanidus.mods.lootr.registry.LootrBlockEntityInit;

@Mod.EventBusSubscriber(modid = LootrAPI.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LootrClient {
    @SubscribeEvent
    public static void modelRegister(ModelEvent.RegisterGeometryLoaders event) {
        event.register("barrel", BarrelModel.Loader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(LootrBlockEntityInit.LOOT_CHEST_ENTITY_PROVIDER.get(), LootrChestBlockRenderer::new);
        event.registerBlockEntityRenderer(LootrBlockEntityInit.TRAPPED_LOOT_CHEST_ENTITY_PROVIDER.get(), LootrChestBlockRenderer::new);
        event.registerBlockEntityRenderer(LootrBlockEntityInit.LOOT_SHULKER_ENTITY_PROVIDER.get(), LootrShulkerBlockRenderer::new);
        event.registerBlockEntityRenderer(LootrBlockEntityInit.LOOT_INVENTORY_ENTITY_PROVIDER.get(), LootrChestBlockRenderer::new);
    }
}
