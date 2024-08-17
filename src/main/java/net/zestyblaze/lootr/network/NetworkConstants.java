package net.zestyblaze.lootr.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.config.ConfigManager;

public class NetworkConstants {
  public static final ResourceLocation CLOSE_CART_CHANNEL = new ResourceLocation(LootrAPI.MODID, "close_cart_packet");
  public static final ResourceLocation OPEN_CART_CHANNEL = new ResourceLocation(LootrAPI.MODID, "open_cart_packet");
  public static final ResourceLocation SYNC_DISABLE_BREAK = new ResourceLocation(LootrAPI.MODID, "sync_disable_break");

  public static void sendSyncDisableBreak (ServerPlayer player) {
    send(SYNC_DISABLE_BREAK, ConfigManager.get().breaking.disable_break, player);
  }

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

  protected static void send(ResourceLocation channel, boolean value, ServerPlayer player) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    buf.writeBoolean(value);
    ServerPlayNetworking.send(player, channel, buf);
  }
}
