package noobanidus.mods.lootr.fabric.network.to_client;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.network.ILootrPacket;

public record PacketRefreshSection() implements ILootrPacket {
  public static final PacketRefreshSection INSTANCE = new PacketRefreshSection();

  public static final Type<PacketRefreshSection> TYPE = new Type<>(LootrAPI.rl("refresh_section"));
  public static final StreamCodec<ByteBuf, PacketRefreshSection> STREAM_CODEC = StreamCodec.unit(INSTANCE);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
