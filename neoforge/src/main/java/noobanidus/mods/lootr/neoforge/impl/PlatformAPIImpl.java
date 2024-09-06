package noobanidus.mods.lootr.neoforge.impl;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.LockCode;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import noobanidus.mods.lootr.common.api.DataToCopy;
import noobanidus.mods.lootr.common.api.IPlatformAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrCart;
import noobanidus.mods.lootr.common.mixins.MixinBaseContainerBlockEntity;
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

  @Override
  public DataToCopy copySpecificData(BlockEntity oldBlockEntity) {
    LockCode code = LockCode.NO_LOCK;
    if (oldBlockEntity instanceof BaseContainerBlockEntity baseContainer) {
      code = ((MixinBaseContainerBlockEntity) baseContainer).getLockKey();
    }
    return new DataToCopy(oldBlockEntity.getPersistentData(), code);
  }

  @Override
  public void restoreSpecificData(DataToCopy data, BlockEntity newBlockEntity) {
    if (data != DataToCopy.EMPTY && newBlockEntity != null) {
      newBlockEntity.getPersistentData().merge(data.data());
    }
    if (newBlockEntity instanceof BaseContainerBlockEntity baseContainer) {
      ((MixinBaseContainerBlockEntity) baseContainer).setLockKey(data.lockCode());
    }
  }

  @Override
  public void copyEntityData(AbstractMinecartContainer entity1, AbstractMinecartContainer entity2) {
    IPlatformAPI.super.copyEntityData(entity1, entity2);
    entity2.getPersistentData().merge(entity1.getPersistentData());
  }
}
