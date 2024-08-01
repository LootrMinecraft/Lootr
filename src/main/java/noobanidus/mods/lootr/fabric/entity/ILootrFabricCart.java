package noobanidus.mods.lootr.fabric.entity;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.level.ChunkPos;
import noobanidus.mods.lootr.api.data.entity.ILootrCart;
import noobanidus.mods.lootr.common.mixins.MixinVehicleEntity;
import noobanidus.mods.lootr.fabric.network.to_client.PacketCloseCart;
import noobanidus.mods.lootr.fabric.network.to_client.PacketOpenCart;
import noobanidus.mods.lootr.fabric.network.to_client.PacketOpenContainer;

public interface ILootrFabricCart extends ILootrCart {
  @Override
  default void performOpen (ServerPlayer player) {
    ServerPlayNetworking.send(player, new PacketOpenCart(asEntity().getId()));
  }

  @Override
  default void performOpen () {
    if (getInfoLevel() instanceof ServerLevel serverLevel) {
      Packet<?> packet = ServerPlayNetworking.createS2CPacket(new PacketOpenCart(asEntity().getId()));
      serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(asEntity().blockPosition()), false).forEach(player -> player.connection.send(packet));
    }
  }

  @Override
  default void performClose (ServerPlayer player) {
    ServerPlayNetworking.send(player, new PacketCloseCart(asEntity().getId()));
  }

  @Override
  default void performClose () {
    if (getInfoLevel() instanceof ServerLevel serverLevel) {
      Packet<?> packet = ServerPlayNetworking.createS2CPacket(new PacketCloseCart(asEntity().getId()));
      serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(asEntity().blockPosition()), false).forEach(player -> player.connection.send(packet));
    }
  }
}
