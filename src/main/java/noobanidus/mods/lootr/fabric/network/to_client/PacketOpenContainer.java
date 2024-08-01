package noobanidus.mods.lootr.fabric.network.to_client;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import noobanidus.mods.lootr.api.LootrAPI;

public record PacketOpenContainer(BlockPos blockPos) implements CustomPacketPayload {
  public static final Type<PacketOpenContainer> TYPE = new Type<>(LootrAPI.rl("close_cart"));
  public static final StreamCodec<ByteBuf, PacketOpenContainer> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, PacketOpenContainer::blockPos, PacketOpenContainer::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
