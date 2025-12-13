package noobanidus.mods.lootr.fabric.network.to_server;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.network.ILootrPacket;

public record PacketRequestUpdate(GlobalPos position) implements ILootrPacket {
  public static final CustomPacketPayload.Type<PacketRequestUpdate> TYPE = new CustomPacketPayload.Type<>(LootrAPI.rl("request_update"));
  public static final StreamCodec<ByteBuf, PacketRequestUpdate> STREAM_CODEC = StreamCodec.composite(GlobalPos.STREAM_CODEC, PacketRequestUpdate::position, PacketRequestUpdate::new);

  @Override
  public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
