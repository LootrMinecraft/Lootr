package noobanidus.mods.lootr.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import noobanidus.mods.lootr.api.LootrAPI;

public class NetworkConstants {
    public static final ResourceLocation CLOSE_CART_CHANNEL = new ResourceLocation(LootrAPI.MODID, "close_cart_packet");
    public static final ResourceLocation OPEN_CART_CHANNEL = new ResourceLocation(LootrAPI.MODID, "open_cart_packet");

    public static void sendCloseCart(int entityId, ServerPlayer player) {
        send(CLOSE_CART_CHANNEL, entityId, player);
    }

    public static void sendOpenCart(int entityId, ServerPlayer player) {
        send(OPEN_CART_CHANNEL, entityId, player);
    }

    protected static void send(ResourceLocation channel, int entityId, ServerPlayer player) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeVarInt(entityId);
        player.connection.send(new ClientboundCustomPayloadPacket(channel, buf));
    }
}