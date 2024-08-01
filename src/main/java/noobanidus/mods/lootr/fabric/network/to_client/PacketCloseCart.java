package noobanidus.mods.lootr.fabric.network.to_client;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.network.ILootrPacket;

public record PacketCloseCart(int entityId) implements ILootrPacket {
  public static final CustomPacketPayload.Type<PacketCloseCart> TYPE = new CustomPacketPayload.Type<>(LootrAPI.rl("close_cart"));
  public static final StreamCodec<ByteBuf, PacketCloseCart> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, PacketCloseCart::entityId, PacketCloseCart::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
