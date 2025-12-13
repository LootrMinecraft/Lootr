package noobanidus.mods.lootr.neoforge.client.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.client.block.LootrDecoratedPotRenderer;

@EventBusSubscriber(modid= LootrAPI.MODID, value= Dist.CLIENT, bus= EventBusSubscriber.Bus.MOD)
public class HandleEvents {
  @SubscribeEvent
  public static void registerLayersEvent (EntityRenderersEvent.RegisterLayerDefinitions event) {
    event.registerLayerDefinition(LootrDecoratedPotRenderer.OPEN_POT_LAYER, LootrDecoratedPotRenderer::createBodyLayer);
  }
}
