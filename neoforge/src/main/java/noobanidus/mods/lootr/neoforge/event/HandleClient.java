package noobanidus.mods.lootr.neoforge.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;

@EventBusSubscriber(value = Dist.CLIENT, modid = LootrAPI.MODID)
public class HandleClient {
  @SubscribeEvent
  public static void onClientLogOut(ClientPlayerNetworkEvent.LoggingOut event) {
    LootrAPI.SYNCED_CONFIG = null;
  }
}
