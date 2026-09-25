package noobanidus.mods.lootr.neoforge.network.to_server;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.interfaces.network.ILootrPacket;
import noobanidus.mods.lootr.neoforge.network.ILootrNeoForgePacket;
import org.jspecify.annotations.NonNull;

public record PacketRequestUpdate(GlobalPos position) implements ILootrNeoForgePacket {
  public static final Type<PacketRequestUpdate> TYPE = new Type<>(LootrAPI.rl("request_update"));
  public static final StreamCodec<ByteBuf, PacketRequestUpdate> STREAM_CODEC = StreamCodec.composite(GlobalPos.STREAM_CODEC, PacketRequestUpdate::position, PacketRequestUpdate::new);

  @Override
  public @NonNull Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  @Override
  public void handle(IPayloadContext context) {
    ServerHandlers.handleRequestUpdate(context, position);
  }
}
