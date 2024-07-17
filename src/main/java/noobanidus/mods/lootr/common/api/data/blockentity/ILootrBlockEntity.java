package noobanidus.mods.lootr.common.api.data.blockentity;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;

public interface ILootrBlockEntity extends ILootrInfoProvider {
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
}
