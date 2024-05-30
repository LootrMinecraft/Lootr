package noobanidus.mods.lootr.api.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.PacketDistributor;
import noobanidus.mods.lootr.api.IHasOpeners;

import java.util.Set;
import java.util.UUID;

public interface ILootBlockEntity extends IHasOpeners {
  void unpackLootTable(Player player, Container inventory, ResourceLocation table, long seed);

  ResourceLocation getTable();

  BlockPos getPosition ();

  long getSeed();

  UUID getTileId();

  void updatePacketViaState();

  default void updatePacketViaForce (BlockEntity entity) {
    if (entity.getLevel() instanceof ServerLevel level) {
      Packet<?> packet = entity.getUpdatePacket();
      if (packet != null) {
        level.getChunkSource().chunkMap.getPlayers(new ChunkPos(entity.getBlockPos()), false).forEach(player -> player.connection.send(packet));
      }
    }
  }

  void setOpened(boolean opened);
}
