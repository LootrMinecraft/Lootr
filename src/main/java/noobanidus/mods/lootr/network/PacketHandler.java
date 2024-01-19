package noobanidus.mods.lootr.network;

import net.neoforged.bus.api.IEventBus;
import noobanidus.mods.lootr.network.to_client.PacketCloseCart;
import noobanidus.mods.lootr.network.to_client.PacketCloseContainer;
import noobanidus.mods.lootr.network.to_client.PacketOpenCart;
import noobanidus.mods.lootr.network.to_client.PacketOpenContainer;

/* Shamelessly cribbed from Mekanism.
Original source: https://github.com/mekanism/Mekanism/blob/1.20.4/src/main/java/mekanism/common/network/PacketHandler.java
 */

public class PacketHandler extends BasePacketHandler {
  public PacketHandler(IEventBus modEventBus, String modid, String version) {
    super(modEventBus, modid, version);
  }

  @Override
  protected void registerClientToServer(PacketRegistrar registrar) {
    // None
  }

  @Override
  protected void registerServerToClient(PacketRegistrar registrar) {
    registrar.play(PacketOpenCart.ID, PacketOpenCart::new);
    registrar.play(PacketOpenContainer.ID, PacketOpenContainer::new);
    registrar.play(PacketCloseCart.ID, PacketCloseCart::new);
    registrar.play(PacketCloseContainer.ID, PacketCloseContainer::new);
  }
}