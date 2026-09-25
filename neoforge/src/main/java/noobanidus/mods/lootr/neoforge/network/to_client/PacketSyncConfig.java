package noobanidus.mods.lootr.neoforge.network.to_client;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.config.SyncedConfig;
import noobanidus.mods.lootr.common.api.interfaces.network.ILootrPacket;
import noobanidus.mods.lootr.neoforge.network.ILootrNeoForgePacket;

public record PacketSyncConfig(SyncedConfig config) implements ILootrNeoForgePacket {
  public static final Type<PacketSyncConfig> TYPE = new Type<>(LootrAPI.rl("sync_config"));
  public static final StreamCodec<ByteBuf, PacketSyncConfig> STREAM_CODEC = SyncedConfig.STREAM_CODEC.map(PacketSyncConfig::new, PacketSyncConfig::config);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  @Override
  public void handle(IPayloadContext context) {
    ClientHandlers.handleConfigSync(config);
  }
}
