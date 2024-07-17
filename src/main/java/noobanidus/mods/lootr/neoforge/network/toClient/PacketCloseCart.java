package noobanidus.mods.lootr.neoforge.network.toClient;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.neoforge.network.ILootrNeoForgePacket;
import noobanidus.mods.lootr.neoforge.network.client.ClientHandlers;

public record PacketCloseCart(int entityId) implements ILootrNeoForgePacket {
  public static final CustomPacketPayload.Type<PacketCloseCart> TYPE = new CustomPacketPayload.Type<>(LootrAPI.rl("close_cart"));
  public static final StreamCodec<ByteBuf, PacketCloseCart> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, PacketCloseCart::entityId, PacketCloseCart::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  @Override
  public void handle(IPayloadContext context) {
    ClientHandlers.handleCloseCart(this.entityId);
  }
}
