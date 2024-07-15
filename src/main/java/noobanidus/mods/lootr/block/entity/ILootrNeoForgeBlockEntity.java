package noobanidus.mods.lootr.block.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import noobanidus.mods.lootr.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.network.toClient.PacketCloseContainer;
import noobanidus.mods.lootr.network.toClient.PacketOpenContainer;

public interface ILootrNeoForgeBlockEntity extends ILootrBlockEntity {
  @Override
  default void performOpen(ServerPlayer player) {
    PacketDistributor.sendToPlayer(player, new PacketOpenContainer(asBlockEntity().getBlockPos()));
  }

  @Override
  default void performClose(ServerPlayer player) {
    PacketDistributor.sendToPlayer(player, new PacketCloseContainer(asBlockEntity().getBlockPos()));
  }

  @Override
  default void performUpdate(ServerPlayer player) {
    markChanged();
    updatePacketViaForce();
  }

  @Override
  default void performDecay(ServerPlayer player) {
    Level level = getInfoLevel();
    if (level == null || level.isClientSide()) {
      return;
    }
    level.destroyBlock(getInfoPos(), true);
  }
}
