package noobanidus.mods.lootr.neoforge.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.neoforge.network.toClient.*;

/* Shamelessly crib from Mekanism until it works
 * Original source: https://github.com/mekanism/Mekanism/blob/1.21.x/src/main/java/mekanism/common/network/BasePacketHandler.java
 * */

@EventBusSubscriber(modid=LootrAPI.MODID)
public class PacketHandler {
  @SubscribeEvent
  public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
    PayloadRegistrar registrar = event.registrar(LootrAPI.NETWORK_VERSION);
    registerClientToServer(new PacketRegistrar(registrar, true));
    registerServerToClient(new PacketRegistrar(registrar, false));
  }

  protected static void registerClientToServer(PacketRegistrar registrar) {
  }

  protected static void registerServerToClient(PacketRegistrar registrar) {
    registrar.play(PacketOpenCart.TYPE, PacketOpenCart.STREAM_CODEC);
    registrar.play(PacketOpenContainer.TYPE, PacketOpenContainer.STREAM_CODEC);
    registrar.play(PacketCloseCart.TYPE, PacketCloseCart.STREAM_CODEC);
    registrar.play(PacketCloseContainer.TYPE, PacketCloseContainer.STREAM_CODEC);
    registrar.play(PacketRefreshSection.TYPE, PacketRefreshSection.STREAM_CODEC);
    registrar.play(PacketPerformBreakEffect.TYPE, PacketPerformBreakEffect.STREAM_CODEC);
  }

  protected record PacketRegistrar(PayloadRegistrar registrar, boolean toServer) {
    public <MSG extends ILootrNeoForgePacket> void play(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> reader) {
      if (toServer) {
        registrar.playToServer(type, reader, ILootrNeoForgePacket::handle);
      } else {
        registrar.playToClient(type, reader, ILootrNeoForgePacket::handle);
      }
    }
  }
}