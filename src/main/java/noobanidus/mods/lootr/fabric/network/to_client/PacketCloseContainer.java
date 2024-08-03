package noobanidus.mods.lootr.fabric.network.to_client;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.network.ILootrPacket;

public record PacketCloseContainer(BlockPos blockPos) implements ILootrPacket {
  public static final Type<PacketCloseContainer> TYPE = new Type<>(LootrAPI.rl("close_container"));
  public static final StreamCodec<ByteBuf, PacketCloseContainer> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, PacketCloseContainer::blockPos, PacketCloseContainer::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
