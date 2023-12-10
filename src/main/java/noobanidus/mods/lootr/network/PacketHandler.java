package noobanidus.mods.lootr.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.PlayNetworkDirection;
import net.neoforged.neoforge.network.simple.MessageFunctions;
import net.neoforged.neoforge.network.simple.SimpleChannel;
import noobanidus.mods.lootr.api.LootrAPI;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PacketHandler {

  private static final String PROTOCOL_VERSION = Integer.toString(2);
  private static short index = 0;

  public static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
      .named(new ResourceLocation(LootrAPI.MODID, "main_network_channel"))
      .clientAcceptedVersions(PROTOCOL_VERSION::equals)
      .serverAcceptedVersions(PROTOCOL_VERSION::equals)
      .networkProtocolVersion(() -> PROTOCOL_VERSION)
      .simpleChannel();

  public static void registerMessages() {
    registerMessage(OpenCart.class, OpenCart::encode, OpenCart::new, OpenCart::handle);
    registerMessage(CloseCart.class, CloseCart::encode, CloseCart::new, CloseCart::handle);
    registerMessage(UpdateModelData.class, UpdateModelData::encode, UpdateModelData::new, UpdateModelData::handle);
  }

  public static void sendToInternal(Object msg, ServerPlayer player) {
    if (!(player instanceof FakePlayer)) {
      HANDLER.sendTo(msg, player.connection.connection, PlayNetworkDirection.PLAY_TO_CLIENT);
    }
  }

  public static void sendToServerInternal(Object msg) {
    HANDLER.sendToServer(msg);
  }

  public static <MSG> void sendInternal(PacketDistributor.PacketTarget target, MSG message) {
    HANDLER.send(target, message);
  }

  public static <MSG> void registerMessage(Class<MSG> messageType, MessageFunctions.MessageEncoder<MSG> encoder, MessageFunctions.MessageDecoder<MSG> decoder, MessageFunctions.MessageConsumer<MSG> messageConsumer) {
    HANDLER.registerMessage(index, messageType, encoder, decoder, messageConsumer);
    index++;
    if (index > 0xFF)
      throw new RuntimeException("Too many messages!");
  }
}