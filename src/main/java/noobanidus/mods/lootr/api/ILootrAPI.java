package noobanidus.mods.lootr.api;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.api.client.ClientTextureType;
import noobanidus.mods.lootr.api.info.ILootrInfoProvider;
import noobanidus.mods.lootr.api.inventory.ILootrInventory;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ILootrAPI {
  MinecraftServer getServer();

  default int getCurrentTicks() {
    MinecraftServer server = getServer();
    if (server == null) {
      return -1;
    }
    return server.getTickCount();
  }

  boolean isFakePlayer(Player player);

  default boolean clearPlayerLoot(ServerPlayer entity) {
    return clearPlayerLoot(entity.getUUID());
  }

  boolean clearPlayerLoot(UUID id);

  @Nullable
  ILootrInventory getInventory(ILootrInfoProvider provider, ServerPlayer player, LootFiller filler);

  @Nullable
  ILootrInventory getInventory(ILootrInfoProvider provider, ServerPlayer player, LootFiller filler, MenuBuilder builder);

  long getLootSeed(long seed);

  boolean shouldDiscard();

  float getExplosionResistance(Block block, float defaultResistance);

  float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos position, float defaultProgress);

  int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos, int defaultSignal);

  boolean shouldNotify(int remaining);

  ClientTextureType getTextureType();

  default boolean isOldTextures() {
    return getTextureType() == ClientTextureType.OLD;
  }

  default boolean isVanillaTextures() {
    return getTextureType() == ClientTextureType.VANILLA;
  }

  default boolean isDefaultTextures() {
    return getTextureType() == ClientTextureType.DEFAULT;
  }

  boolean isDisabled();

  boolean isLootTableBlacklisted(ResourceKey<LootTable> table);

  boolean isDimensionBlocked(ResourceKey<Level> dimension);

  boolean isDimensionDecaying(ResourceKey<Level> dimension);

  boolean isDimensionRefreshing(ResourceKey<Level> dimension);

  boolean isDecaying(ILootrInfoProvider provider);

  boolean isRefreshing(ILootrInfoProvider provider);

  boolean reportUnresolvedTables();

  boolean isCustomTrapped();

  boolean isWorldBorderSafe(Level level, BlockPos pos);

  boolean isWorldBorderSafe(Level level, ChunkPos pos);

  boolean hasExpired(long time);

  boolean shouldConvertMineshafts();

  boolean shouldConvertElytras();

  int getDecayValue();

  int getRefreshValue();

  Style getInvalidStyle();

  Style getDecayStyle();

  Style getRefreshStyle();

  Style getChatStyle();

  Component getInvalidTableComponent(ResourceKey<LootTable> lootTable);

  boolean canDestroyOrBreak(Player player);

  boolean isBreakDisabled();

  @Nullable
  BlockState replacementBlockState(BlockState original);

  // TODO: Think on this.
  default boolean hasCapacity(String capacity) {
    return switch (capacity) {
      case LootrCapacities.STRUCTURE_SAVING -> true;
      case LootrCapacities.SHOULD_DISCARD -> true;
      case LootrCapacities.CAPACITIES -> true;
      case LootrCapacities.EXPLOSION_RESISTANCE -> true;
      case LootrCapacities.DESTRUCTION_PROGRESS -> true;
      default -> false;
    };
  }
}
