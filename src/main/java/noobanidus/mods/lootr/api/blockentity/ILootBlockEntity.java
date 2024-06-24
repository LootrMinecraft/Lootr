package noobanidus.mods.lootr.api.blockentity;

import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.api.IHasOpeners;
import noobanidus.mods.lootr.api.ILootInfoProvider;
import org.jetbrains.annotations.Nullable;

public interface ILootBlockEntity extends IHasOpeners, ILootInfoProvider {
  void unpackLootTable(Player player, Container inventory, @Nullable ResourceKey<LootTable> overrideTable, long overrideSeed);

  default void updatePacketViaForce (BlockEntity entity) {
    if (entity.getLevel() instanceof ServerLevel level) {
      Packet<?> packet = entity.getUpdatePacket();
      if (packet != null) {
        level.getChunkSource().chunkMap.getPlayers(new ChunkPos(entity.getBlockPos()), false).forEach(player -> player.connection.send(packet));
      }
    }
  }

  boolean isClientOpened ();

  void setClientOpened(boolean opened);
}
