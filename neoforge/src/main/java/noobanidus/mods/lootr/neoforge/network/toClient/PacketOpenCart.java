package noobanidus.mods.lootr.neoforge.network.toClient;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.neoforge.network.client.ClientHandlers;
import noobanidus.mods.lootr.neoforge.network.ILootrNeoForgePacket;

public record PacketOpenCart(int entityId) implements ILootrNeoForgePacket {
  public static final CustomPacketPayload.Type<PacketOpenCart> TYPE = new CustomPacketPayload.Type<>(LootrAPI.rl("open_cart"));
  public static final StreamCodec<ByteBuf, PacketOpenCart> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, PacketOpenCart::entityId, PacketOpenCart::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  @Override
  public void handle(IPayloadContext context) {
    ClientHandlers.handleOpenCart(this.entityId);
  }
}
