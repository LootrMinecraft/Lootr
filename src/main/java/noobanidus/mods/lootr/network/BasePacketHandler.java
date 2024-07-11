package noobanidus.mods.lootr.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import noobanidus.mods.lootr.api.LootrAPI;

/* Shamelessly crib from Mekanism until it works
 * Original source: https://github.com/mekanism/Mekanism/blob/1.21.x/src/main/java/mekanism/common/network/BasePacketHandler.java
 * */

public abstract class BasePacketHandler {

  protected BasePacketHandler(IEventBus modEventBus) {
    modEventBus.addListener(RegisterPayloadHandlersEvent.class, event -> {
      PayloadRegistrar registrar = event.registrar(LootrAPI.NETWORK_VERSION);
      registerClientToServer(new PacketRegistrar(registrar, true));
      registerServerToClient(new PacketRegistrar(registrar, false));
    });
  }

  protected abstract void registerClientToServer(PacketRegistrar registrar);

  protected abstract void registerServerToClient(PacketRegistrar registrar);

  protected record PacketRegistrar(PayloadRegistrar registrar, boolean toServer) {

    public <MSG extends ILootrPacket> void play(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> reader) {
      if (toServer) {
        registrar.playToServer(type, reader, ILootrPacket::handle);
      } else {
        registrar.playToClient(type, reader, ILootrPacket::handle);
      }
    }
  }
}