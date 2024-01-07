package noobanidus.mods.lootr.events;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.util.forge.PlatformUtilsImpl;

@Mod.EventBusSubscriber(modid = LootrAPI.MODID)
public class NetworkHandlers {
    @SubscribeEvent
    public void onCustomPayloadPacketReceive(NetworkEvent.ClientCustomPayloadEvent event) {
        FriendlyByteBuf payload = event.getPayload();
        int readerIndex = payload.readerIndex();
        int writerIndex = payload.writerIndex();
        ResourceLocation channel = payload.readResourceLocation();
        PlatformUtilsImpl.CLIENT_CUSTOM_NETWORK_HANDLERS.computeIfPresent(channel, (c, handlers) -> {
            handlers.forEach(handler -> handler.consume(Minecraft.getInstance(), null, payload, event.getSource().get().getPacketDispatcher()));
            payload.setIndex(readerIndex, writerIndex);
            return handlers;
        });
    }
}
