package noobanidus.mods.lootr.block.entity;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import noobanidus.mods.lootr.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.network.to_client.PacketCloseContainer;
import noobanidus.mods.lootr.network.to_client.PacketOpenContainer;

public interface ILootrFabricBlockEntity extends ILootrBlockEntity {
  @Override
  default void performOpen(ServerPlayer player) {
    ServerPlayNetworking.send(player, new PacketOpenContainer(asBlockEntity().getBlockPos()));
  }

  @Override
  default void performOpen() {
    if (getInfoLevel() instanceof ServerLevel serverLevel) {
      Packet<?> packet = ServerPlayNetworking.createS2CPacket(new PacketOpenContainer(asBlockEntity().getBlockPos()));
      serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(asBlockEntity().getBlockPos()), false).forEach(player -> player.connection.send(packet));
    }
  }

  @Override
  default void performClose(ServerPlayer player) {
    ServerPlayNetworking.send(player, new PacketCloseContainer(asBlockEntity().getBlockPos()));
  }

  @Override
  default void performClose() {
    if (getInfoLevel() instanceof ServerLevel serverLevel) {
      Packet<?> packet = ServerPlayNetworking.createS2CPacket(new PacketCloseContainer(asBlockEntity().getBlockPos()));
      serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(asBlockEntity().getBlockPos()), false).forEach(player -> player.connection.send(packet));
    }
  }

  @Override
  default void performDecay() {
    Level level = getInfoLevel();
    if (level == null || level.isClientSide()) {
      return;
    }
    level.destroyBlock(getInfoPos(), true);
  }

  @Override
  default void performUpdate(ServerPlayer player) {
    performUpdate();
  }

  @Override
  default void performUpdate() {
    markChanged();
    updatePacketViaForce();
  }
}
