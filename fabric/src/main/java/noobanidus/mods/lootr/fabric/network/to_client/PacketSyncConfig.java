package noobanidus.mods.lootr.fabric.network.to_client;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.config.SyncedConfig;
import noobanidus.mods.lootr.common.api.network.ILootrPacket;

public record PacketSyncConfig(SyncedConfig config) implements ILootrPacket {
  public static final CustomPacketPayload.Type<PacketSyncConfig> TYPE = new CustomPacketPayload.Type<>(LootrAPI.rl("sync_config"));
  public static final StreamCodec<ByteBuf, PacketSyncConfig> STREAM_CODEC = SyncedConfig.STREAM_CODEC.map(PacketSyncConfig::new, PacketSyncConfig::config);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
