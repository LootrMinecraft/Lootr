package noobanidus.mods.lootr.common.api.data.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.PlatformAPI;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;

public interface ILootrBlockEntity extends ILootrInfoProvider {
  static <T extends BlockEntity> void ticker (Level level, BlockPos pos, BlockState state, T blockEntity) {
    if (blockEntity instanceof ILootrBlockEntity t) {
      t.defaultTick(level, pos, state);
    }
  }

  default void defaultTick (Level level, BlockPos pos, BlockState state) {
    if (!level.isClientSide()) {
      LootrAPI.handleProviderTick(this);
    }
  }

  default BlockEntity asBlockEntity () {
    return ((BlockEntity) this);
  }

  @Override
  default LootrInfoType getInfoType() {
    return LootrInfoType.CONTAINER_BLOCK_ENTITY;
  }

  default void updatePacketViaForce () {
    if (this instanceof BlockEntity blockEntity) {
      updatePacketViaForce(blockEntity);
    } else {
      throw new IllegalStateException("updatePacketViaForce called on non-BlockEntity ILootrBlockEntity");
    }
  }

  default void updatePacketViaForce(BlockEntity entity) {
    if (entity.getLevel() instanceof ServerLevel level) {
      Packet<?> packet = entity.getUpdatePacket();
      if (packet != null) {
        level.getChunkSource().chunkMap.getPlayers(new ChunkPos(entity.getBlockPos()), false).forEach(player -> player.connection.send(packet));
      }
    }
  }

  @Override
  default void performOpen(ServerPlayer player) {
    PlatformAPI.performBlockOpen(this, player);
  }

  @Override
  default void performOpen() {
    PlatformAPI.performBlockOpen(this);
  }

  @Override
  default void performClose(ServerPlayer player) {
    PlatformAPI.performBlockClose(this, player);
  }

  @Override
  default void performClose() {
    PlatformAPI.performBlockClose(this);
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
