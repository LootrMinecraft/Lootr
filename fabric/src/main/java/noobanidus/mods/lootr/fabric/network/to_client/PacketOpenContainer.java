package noobanidus.mods.lootr.fabric.network.to_client;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.network.ILootrPacket;

public record PacketOpenContainer(BlockPos blockPos) implements ILootrPacket {
  public static final Type<PacketOpenContainer> TYPE = new Type<>(LootrAPI.rl("open_container"));
  public static final StreamCodec<ByteBuf, PacketOpenContainer> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, PacketOpenContainer::blockPos, PacketOpenContainer::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
