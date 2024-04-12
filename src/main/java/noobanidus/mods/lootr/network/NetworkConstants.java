package noobanidus.mods.lootr.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
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
    FriendlyByteBuf buf = PacketByteBufs.create();
    buf.writeVarInt(entityId);
    ServerPlayNetworking.send(player, channel, buf);
  }
}
