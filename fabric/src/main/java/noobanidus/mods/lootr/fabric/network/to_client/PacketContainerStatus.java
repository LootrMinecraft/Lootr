package noobanidus.mods.lootr.fabric.network.to_client;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.client.ContainerStatus;
import noobanidus.mods.lootr.common.api.interfaces.network.ILootrPacket;

public record PacketContainerStatus(ContainerStatus status, ContainerStatus.Type statusType,
                                    int remaining) implements ILootrPacket {
  public static final Type<PacketContainerStatus> TYPE = new Type<>(LootrAPI.rl("container_status"));
  public static final StreamCodec<ByteBuf, PacketContainerStatus> STREAM_CODEC = StreamCodec.composite(ContainerStatus.STREAM_CODEC, PacketContainerStatus::status, ContainerStatus.Type.STREAM_CODEC, PacketContainerStatus::statusType, ByteBufCodecs.VAR_INT, PacketContainerStatus::remaining, PacketContainerStatus::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
