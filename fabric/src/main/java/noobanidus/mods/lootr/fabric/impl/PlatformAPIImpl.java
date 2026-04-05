package noobanidus.mods.lootr.fabric.impl;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.LockCode;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.data.DataToCopy;
import noobanidus.mods.lootr.common.api.interfaces.IPlatformAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;
import noobanidus.mods.lootr.common.impl.DefaultPlatformAPIImpl;
import noobanidus.mods.lootr.common.mixin.accessor.AccessorMixinBaseContainerBlockEntity;
import noobanidus.mods.lootr.fabric.network.to_client.*;

public class PlatformAPIImpl extends DefaultPlatformAPIImpl implements IPlatformAPI {
  @Override
  public void performEntityOpen(ILootrEntity entity, ServerPlayer player) {
    ServerPlayNetworking.send(player, new PacketOpenCart(entity.asEntity().getId()));
  }

  @Override
  public void performEntityOpen(ILootrEntity entity) {
    if (entity.getDataLevel() instanceof ServerLevel serverLevel) {
      Packet<?> packet = ServerPlayNetworking.createClientboundPacket(new PacketOpenCart(entity.asEntity().getId()));
      serverLevel.getChunkSource().chunkMap.getPlayers(ChunkPos.containing(entity.asEntity().blockPosition()), false).forEach(player -> player.connection.send(packet));
    }
  }

  @Override
  public void performEntityClose(ILootrEntity entity, ServerPlayer player) {
    ServerPlayNetworking.send(player, new PacketCloseCart(entity.asEntity().getId()));
  }

  @Override
  public void performEntityClose(ILootrEntity entity) {
    if (entity.getDataLevel() instanceof ServerLevel serverLevel) {
      Packet<?> packet = ServerPlayNetworking.createClientboundPacket(new PacketCloseCart(entity.asEntity().getId()));
      serverLevel.getChunkSource().chunkMap.getPlayers(ChunkPos.containing(entity.asEntity().blockPosition()), false).forEach(player -> player.connection.send(packet));
    }
  }

  @Override
  public void performBlockOpen(ILootrBlockEntity blockEntity, ServerPlayer player) {
    ServerPlayNetworking.send(player, new PacketOpenContainer(blockEntity.asBlockEntity().getBlockPos()));
  }

  @Override
  public void performBlockOpen(ILootrBlockEntity blockEntity) {
    if (blockEntity.getDataLevel() instanceof ServerLevel serverLevel) {
      Packet<?> packet = ServerPlayNetworking.createClientboundPacket(new PacketOpenContainer(blockEntity.asBlockEntity().getBlockPos()));
      serverLevel.getChunkSource().chunkMap.getPlayers(ChunkPos.containing(blockEntity.asBlockEntity().getBlockPos()), false).forEach(player -> player.connection.send(packet));
    }
  }

  @Override
  public void performBlockClose(ILootrBlockEntity blockEntity, ServerPlayer player) {
    ServerPlayNetworking.send(player, new PacketCloseContainer(blockEntity.asBlockEntity().getBlockPos()));
  }

  @Override
  public void performBlockClose(ILootrBlockEntity blockEntity) {
    if (blockEntity.getDataLevel() instanceof ServerLevel serverLevel) {
      Packet<?> packet = ServerPlayNetworking.createClientboundPacket(new PacketCloseContainer(blockEntity.asBlockEntity().getBlockPos()));
      serverLevel.getChunkSource().chunkMap.getPlayers(ChunkPos.containing(blockEntity.asBlockEntity().getBlockPos()), false).forEach(player -> player.connection.send(packet));
    }
  }

  @Override
  public DataToCopy copySpecificData(BlockEntity oldBlockEntity) {
    LockCode key = LockCode.NO_LOCK;
    if (oldBlockEntity instanceof BaseContainerBlockEntity baseContainer) {
      key = ((AccessorMixinBaseContainerBlockEntity) baseContainer).getLockKey();
    }
    return new DataToCopy(null, key);
  }

  @Override
  public void restoreSpecificData(DataToCopy data, BlockEntity newBlockEntity) {
    if (newBlockEntity instanceof BaseContainerBlockEntity baseContainer) {
      ((AccessorMixinBaseContainerBlockEntity) baseContainer).setLockKey(data.lockCode());
    }
  }

  @Override
  public void refreshPlayerSection(ServerPlayer player) {
    ServerPlayNetworking.send(player, PacketRefreshSection.INSTANCE);
  }

  @Override
  public void performPotBreak(ILootrBlockEntity blockEntity, ServerPlayer player) {
    if (blockEntity.getDataLevel() instanceof ServerLevel serverLevel) {
      Packet<?> packet = ServerPlayNetworking.createClientboundPacket(new PacketPerformBreakEffect(player.getId(), blockEntity.asBlockEntity().getBlockPos()));
      serverLevel.getChunkSource().chunkMap.getPlayers(ChunkPos.containing(blockEntity.asBlockEntity().getBlockPos()), false).forEach(splayer -> splayer.connection.send(packet));
    }
  }
}
