package noobanidus.mods.lootr.neoforge.network.toClient;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import noobanidus.mods.lootr.neoforge.Lootr;
import noobanidus.mods.lootr.neoforge.network.ILootrNeoForgePacket;
import noobanidus.mods.lootr.neoforge.network.client.ClientHandlers;

public record PacketRefreshSection () implements ILootrNeoForgePacket {
  public static final PacketRefreshSection INSTANCE = new PacketRefreshSection();

  public static final CustomPacketPayload.Type<PacketRefreshSection> TYPE = new CustomPacketPayload.Type<>(Lootr.rl("refresh_section"));
  public static final StreamCodec<FriendlyByteBuf, PacketRefreshSection> STREAM_CODEC = StreamCodec.unit(INSTANCE);

  @Override
  public void handle(IPayloadContext context) {
    ClientHandlers.handleRefresh();
  }

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
