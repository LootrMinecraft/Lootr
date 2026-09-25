package noobanidus.mods.lootr.fabric.network.to_client;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.interfaces.network.ILootrPacket;

import java.util.List;

public record PacketAreaEntitySync(List<Integer> opened, List<Integer> closed) implements ILootrPacket {
  public static final CustomPacketPayload.Type<PacketAreaEntitySync> TYPE = new CustomPacketPayload.Type<>(LootrAPI.rl("area_entity_symc"));
  public static final StreamCodec<ByteBuf, PacketAreaEntitySync> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()), PacketAreaEntitySync::opened, ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()), PacketAreaEntitySync::closed, PacketAreaEntitySync::new);

  @Override
  public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
