package noobanidus.mods.lootr.network;

import net.neoforged.bus.api.IEventBus;
import noobanidus.mods.lootr.network.toClient.PacketCloseCart;
import noobanidus.mods.lootr.network.toClient.PacketCloseContainer;
import noobanidus.mods.lootr.network.toClient.PacketOpenCart;
import noobanidus.mods.lootr.network.toClient.PacketOpenContainer;

/* Shamelessly cribbed from Mekanism.
Original source: https://github.com/mekanism/Mekanism/blob/1.20.4/src/main/java/mekanism/common/network/PacketHandler.java
 */

public class PacketHandler extends BasePacketHandler {
  public PacketHandler(IEventBus modEventBus) {
    super(modEventBus);
  }

  @Override
  protected void registerClientToServer(PacketRegistrar registrar) {
    // None
  }

  @Override
  protected void registerServerToClient(PacketRegistrar registrar) {
    registrar.play(PacketOpenCart.TYPE, PacketOpenCart.STREAM_CODEC);
    registrar.play(PacketOpenContainer.TYPE, PacketOpenContainer.STREAM_CODEC);
    registrar.play(PacketCloseCart.TYPE, PacketCloseCart.STREAM_CODEC);
    registrar.play(PacketCloseContainer.TYPE, PacketCloseContainer.STREAM_CODEC);
  }
}