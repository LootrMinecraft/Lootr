package noobanidus.mods.lootr.neoforge.network.toClient;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.client.ClientHooks;
import noobanidus.mods.lootr.neoforge.network.ILootrNeoForgePacket;
import org.jspecify.annotations.NonNull;

public record PacketPerformBreakEffect(int entityId, BlockPos pos) implements ILootrNeoForgePacket {
  public static final Type<PacketPerformBreakEffect> TYPE = new Type<>(LootrAPI.rl("perform_break_effect"));
  public static final StreamCodec<ByteBuf, PacketPerformBreakEffect> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, PacketPerformBreakEffect::entityId, BlockPos.STREAM_CODEC, PacketPerformBreakEffect::pos, PacketPerformBreakEffect::new);

  @Override
  public @NonNull Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  @Override
  public void handle(IPayloadContext context) {
    ClientHooks.performBreakEffect(entityId, pos);
  }
}
