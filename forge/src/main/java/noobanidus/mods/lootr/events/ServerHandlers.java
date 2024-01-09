package noobanidus.mods.lootr.events;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.util.forge.PlatformUtilsImpl;

@Mod.EventBusSubscriber(modid = LootrAPI.MODID)
public class ServerHandlers {
    @SubscribeEvent
    public void onServerStartEvent(ServerAboutToStartEvent event) {
        PlatformUtilsImpl.SERVER_START_HANDLERS.forEach(handler -> handler.accept(event.getServer()));
    }

    @SubscribeEvent
    public void onServerStopEvent(ServerStoppingEvent event) {
        PlatformUtilsImpl.SERVER_STOP_HANDLERS.forEach(handler -> handler.accept(event.getServer()));
    }

    @SubscribeEvent
    public void onPostServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        PlatformUtilsImpl.SERVER_END_TICK_HANDLERS.forEach(handler -> handler.accept(event.getServer()));
    }
}
