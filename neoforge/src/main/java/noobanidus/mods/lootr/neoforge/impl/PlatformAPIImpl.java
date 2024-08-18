package noobanidus.mods.lootr.neoforge.impl;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;
import noobanidus.mods.lootr.common.api.IPlatformAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrCart;
import noobanidus.mods.lootr.neoforge.network.toClient.PacketCloseCart;
import noobanidus.mods.lootr.neoforge.network.toClient.PacketCloseContainer;
import noobanidus.mods.lootr.neoforge.network.toClient.PacketOpenCart;
import noobanidus.mods.lootr.neoforge.network.toClient.PacketOpenContainer;

public class PlatformAPIImpl implements IPlatformAPI {
  @Override
  public void performCartOpen(ILootrCart cart, ServerPlayer player) {
    PacketDistributor.sendToPlayer(player, new PacketOpenCart(cart.asEntity().getId()));
  }

  @Override
  public void performCartOpen(ILootrCart cart) {
    PacketDistributor.sendToPlayersTrackingEntity(cart.asEntity(), new PacketOpenCart(cart.asEntity().getId()));
  }

  @Override
  public void performCartClose(ILootrCart cart, ServerPlayer player) {
    PacketDistributor.sendToPlayer(player, new PacketCloseCart(cart.asEntity().getId()));
  }

  @Override
  public void performCartClose(ILootrCart cart) {
    PacketDistributor.sendToPlayersTrackingEntity(cart.asEntity(), new PacketCloseCart(cart.asEntity().getId()));
  }

  @Override
  public void performBlockOpen(ILootrBlockEntity blockEntity, ServerPlayer player) {
    PacketDistributor.sendToPlayer(player, new PacketOpenContainer(blockEntity.asBlockEntity().getBlockPos()));
  }

  @Override
  public void performBlockOpen(ILootrBlockEntity blockEntity) {
    PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) blockEntity.getInfoLevel(), new ChunkPos(blockEntity.asBlockEntity().getBlockPos()), new PacketOpenContainer(blockEntity.asBlockEntity().getBlockPos()));
  }

  @Override
  public void performBlockClose(ILootrBlockEntity blockEntity, ServerPlayer player) {
    PacketDistributor.sendToPlayer(player, new PacketCloseContainer(blockEntity.asBlockEntity().getBlockPos()));
  }

  @Override
  public void performBlockClose(ILootrBlockEntity blockEntity) {
    PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) blockEntity.getInfoLevel(), new ChunkPos(blockEntity.asBlockEntity().getBlockPos()), new PacketCloseContainer(blockEntity.asBlockEntity().getBlockPos()));
  }
}
