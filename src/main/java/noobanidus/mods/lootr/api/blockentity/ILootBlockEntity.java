package noobanidus.mods.lootr.api.blockentity;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.api.IHasOpeners;
import noobanidus.mods.lootr.api.ILootInfoProvider;

public interface ILootBlockEntity extends IHasOpeners, ILootInfoProvider {
    default void updatePacketViaForce(BlockEntity entity) {
        if (entity.getLevel() instanceof ServerLevel level) {
            Packet<?> packet = entity.getUpdatePacket();
            if (packet != null) {
                level.getChunkSource().chunkMap.getPlayers(new ChunkPos(entity.getBlockPos()), false).forEach(player -> player.connection.send(packet));
            }
        }
    }

    boolean isClientOpened();

    void setClientOpened(boolean opened);
}