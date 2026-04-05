package noobanidus.mods.lootr.common.api.data.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.LockMessageSuppression;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.PlatformAPI;
import noobanidus.mods.lootr.common.api.data.ILootrContainerInstance;
import noobanidus.mods.lootr.common.api.conversion.BlockConversionMap;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.ApiStatus;

public interface ILootrBlockEntity extends ILootrContainerInstance {
  static <T extends BlockEntity> void ticker (Level level, BlockPos pos, BlockState state, T blockEntity) {
    if (LootrAPI.resolveBlockEntity(blockEntity) instanceof ILootrBlockEntity t && (level.isClientSide() || t.hasLootTable())) {
      t.defaultTick(level, pos, state);
    }
  }

  /**
   * @return It is actually possible for addon-associated block entities to not have a loot table associated with them, meaning they function as a "normal" container. This will never actually happen with Lootr containers, however it should always be checked.
   */
  default boolean hasLootTable () {
    return getDataLootTable() != null;
  }

  default void defaultTick (Level level, BlockPos pos, BlockState state) {
    if (!level.isClientSide()) {
      LootrAPI.handleProviderTick(this);
    } else {
      LootrAPI.handleProviderClientTick(this);
    }
  }

  default BlockEntity asBlockEntity () {
    if (this instanceof BlockEntity entity) {
      return entity;
    }

    throw new NullPointerException("ILootrBlockEntity implementation is not a BlockEntity and doesn't provide asBlockEntity()!");
  }

  default void updatePacketViaForce () {
    updatePacketViaForce(asBlockEntity());
  }

  default void updatePacketViaForce(BlockEntity entity) {
    if (entity.getLevel() instanceof ServerLevel level) {
      Packet<?> packet = entity.getUpdatePacket();
      if (packet != null) {
        level.getChunkSource().chunkMap.getPlayers(ChunkPos.containing(entity.getBlockPos()), false).forEach(player -> player.connection.send(packet));
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
    Level level = getDataLevel();
    if (level == null || level.isClientSide()) {
      return;
    }
    // TODO: Put this somewhere else
    BlockState stateAt = level.getBlockState(getDataPos());
    boolean replaceWhenDecayed = LootrAPI.shouldReplaceWhenDecayed();
    level.destroyBlock(getDataPos(), !replaceWhenDecayed);
    if (replaceWhenDecayed) {
      //noinspection deprecation
      Block replacementBlock = getDataType().getReplacementBlock();
      if (replacementBlock != null) {
        BlockState replacementState = replacementBlock.defaultBlockState();
        for (Property<?> prop : replacementState.getProperties()) {
          if (stateAt.hasProperty(prop)) {
            replacementState = BlockConversionMap.safeReplace(replacementState, stateAt, prop);
          }
        }
        level.setBlock(getDataPos(), replacementState, Block.UPDATE_CLIENTS);
      }
    }
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

  @ApiStatus.Internal
  default void setLootTableInternal (ResourceKey<LootTable> lootTable, long seed) {
    if (this instanceof RandomizableContainer container) {
      container.setLootTable(lootTable, seed);
    } else {
      throw new NotImplementedException("setLootTableInternal called on ILootrBlockEntity that is not a RandomizableContainer without overriding!");
    }
  }

  @Override
  default boolean canPlayerOpen(ServerPlayer player) {
    if (this instanceof BaseContainerBlockEntity bce) {
      LockMessageSuppression.setSuppressableLock(true);
      var result = bce.canOpen(player);
      LockMessageSuppression.setSuppressableLock(false);
      return result;
    }

    return true;
  }

  @Override
  default void informPlayerCannotOpen(ServerPlayer player) {
    if (this instanceof BaseContainerBlockEntity bce) {
      LockMessageSuppression.setSuppressableLock(false);
      bce.canOpen(player);
    }
  }
}
