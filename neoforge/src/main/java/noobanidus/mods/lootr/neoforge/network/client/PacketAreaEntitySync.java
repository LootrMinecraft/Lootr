package noobanidus.mods.lootr.neoforge.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.interfaces.network.ILootrPacket;
import noobanidus.mods.lootr.neoforge.network.ILootrNeoForgePacket;

import java.util.List;

public record PacketAreaEntitySync(List<Integer> opened, List<Integer> closed) implements ILootrNeoForgePacket {
  public static final Type<PacketAreaEntitySync> TYPE = new Type<>(LootrAPI.rl("area_entity_symc"));
  public static final StreamCodec<ByteBuf, PacketAreaEntitySync> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()), PacketAreaEntitySync::opened, ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()), PacketAreaEntitySync::closed, PacketAreaEntitySync::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  @Override
  public void handle(IPayloadContext context) {
    ClientHandlers.handleAreaSync(this);
  }
}
