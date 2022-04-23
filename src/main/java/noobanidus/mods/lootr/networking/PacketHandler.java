package noobanidus.mods.lootr.networking;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.client.ClientPacketHandlers;

public class PacketHandler {

  private static final String PROTOCOL_VERSION = Integer.toString(2);
  private static short index = 0;

  public static final SimpleNetworkWrapper HANDLER = NetworkRegistry.INSTANCE.newSimpleChannel(Lootr.MODID);

  public static void registerMessages() {
    registerMessage(OpenCart.class, ClientPacketHandlers::handleOpenCart);
    registerMessage(CloseCart.class, ClientPacketHandlers::handleCloseCart);
  }

  public static void sendToInternal(IMessage msg, EntityPlayerMP player) {
    if (!(player instanceof FakePlayer))
      HANDLER.sendTo(msg, player);
  }

  public static void sendToServerInternal(IMessage msg) {
    HANDLER.sendToServer(msg);
  }

  public static <MSG extends IMessage> void sendToAllTracking(Entity tracking, MSG message) {
    HANDLER.sendToAllTracking(message, tracking);
  }

  public static <MSG extends IMessage> void registerMessage(Class<MSG> messageType, IMessageHandler<MSG, IMessage> messageConsumer) {
    HANDLER.registerMessage(messageConsumer, messageType, index, Side.CLIENT);
    HANDLER.registerMessage(messageConsumer, messageType, index, Side.SERVER);
    index++;
    if (index > 0xFF)
      throw new RuntimeException("Too many messages!");
  }
}