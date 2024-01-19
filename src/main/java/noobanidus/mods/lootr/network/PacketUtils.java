package noobanidus.mods.lootr.network;

import com.google.common.collect.Maps;
import com.ibm.icu.impl.Pair;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Map;
import java.util.Optional;
import java.util.function.IntFunction;

/* Shamelessly crib from Mekanism until it works
 * Original source: https://github.com/mekanism/Mekanism/blob/1.20.4/src/main/java/mekanism/common/network/PacketUtils.java
 */
public class PacketUtils {

  private PacketUtils() {
  }

  public static Optional<ServerPlayer> asServerPlayer(IPayloadContext context) {
    return context.player()
        .filter(ServerPlayer.class::isInstance)
        .map(ServerPlayer.class::cast);
  }

  /**
   * Send this message to the specified player.
   *
   * @param message - the message to send
   * @param player  - the player to send it to
   */
  public static <MSG extends CustomPacketPayload> void sendTo(MSG message, ServerPlayer player) {
    PacketDistributor.PLAYER.with(player).send(message);
  }

  /**
   * Send this message to everyone connected to the server.
   *
   * @param message - message to send
   */
  public static <MSG extends CustomPacketPayload> void sendToAll(MSG message) {
    PacketDistributor.ALL.noArg().send(message);
  }

  /**
   * Send this message to everyone connected to the server if the server has loaded.
   *
   * @param message - message to send
   * @apiNote This is useful for reload listeners
   */
  public static <MSG extends CustomPacketPayload> void sendToAllIfLoaded(MSG message) {
    if (ServerLifecycleHooks.getCurrentServer() != null) {
      //If the server has loaded, send to all players
      sendToAll(message);
    }
  }

  /**
   * Send this message to everyone within the supplied dimension.
   *
   * @param message   - the message to send
   * @param dimension - the dimension to target
   */
  public static <MSG extends CustomPacketPayload> void sendToDimension(MSG message, ResourceKey<Level> dimension) {
    PacketDistributor.DIMENSION.with(dimension).send(message);
  }

  /**
   * Send this message to the server.
   *
   * @param message - the message to send
   */
  public static <MSG extends CustomPacketPayload> void sendToServer(MSG message) {
    PacketDistributor.SERVER.noArg().send(message);
  }

  public static <MSG extends CustomPacketPayload> void sendToAllTracking(MSG message, Entity entity) {
    PacketDistributor.TRACKING_ENTITY.with(entity).send(message);
  }

  public static <MSG extends CustomPacketPayload> void sendToAllTrackingAndSelf(MSG message, Entity entity) {
    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entity).send(message);
  }

  public static <MSG extends CustomPacketPayload> void sendToAllTracking(MSG message, BlockEntity tile) {
    sendToAllTracking(message, tile.getLevel(), tile.getBlockPos());
  }

  public static <MSG extends CustomPacketPayload> void sendToAllTracking(MSG message, Level world, BlockPos pos) {
    if (world instanceof ServerLevel level) {
      //If we have a ServerWorld just directly figure out the ChunkPos to not require looking up the chunk
      // This provides a decent performance boost over using the packet distributor
      level.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).forEach(p -> sendTo(message, p));
    } else {
      //Otherwise, fallback to entities tracking the chunk if some mod did something odd and our world is not a ServerWorld
      PacketDistributor.TRACKING_CHUNK.with(world.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()))).send(message);
    }
  }

  private static boolean isChunkTracked(ServerPlayer player, int chunkX, int chunkZ) {
    return player.getChunkTrackingView().contains(chunkX, chunkZ) && !player.connection.chunkSender.isPending(ChunkPos.asLong(chunkX, chunkZ));
  }
}
