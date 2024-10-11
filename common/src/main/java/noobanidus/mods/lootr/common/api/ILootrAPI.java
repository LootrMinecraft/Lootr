package noobanidus.mods.lootr.common.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.client.ClientTextureType;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.common.api.data.ILootrSavedData;
import noobanidus.mods.lootr.common.api.data.LootFiller;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrCart;
import noobanidus.mods.lootr.common.api.data.inventory.ILootrInventory;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ILootrAPI {
  // Platform-independent implementations
  Set<UUID> getPlayerIds();

  MinecraftServer getServer();

  default int getCurrentTicks() {
    MinecraftServer server = getServer();
    if (server == null) {
      return -1;
    }
    return server.getTickCount();
  }

  boolean isFakePlayer(Player player);

  // Clears player loot for all containers
  default boolean clearPlayerLoot(ServerPlayer entity) {
    return clearPlayerLoot(entity.getUUID());
  }

  boolean clearPlayerLoot(UUID id);

  // Get specified inventory
  @Nullable
  ILootrInventory getInventory(ILootrInfoProvider provider, ServerPlayer player, LootFiller filler);

  // Get specified inventory using menubuilder
  @Nullable
  ILootrInventory getInventory(ILootrInfoProvider provider, ServerPlayer player, LootFiller filler, MenuBuilder builder);

  // Get saved data for specific provider
  @Nullable
  ILootrSavedData getData(ILootrInfoProvider provider);

  // Calculate seed according to configuration
  long getLootSeed(long seed);

  // Determine if saving block entity data in a structure
  boolean shouldDiscard();

  // Calculate resistance according to configuration
  float getExplosionResistance(Block block, float defaultResistance);

  // Determine configuration for blast resistance
  boolean isBlastResistant();

  // Determine configuration for blast resistance
  boolean isBlastImmune();

  // Determine destroy progress from configuration, used to completely prevent block breaking
  float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos position, float defaultProgress);

  // Determine analog signal from configuration
  int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos, int defaultSignal);

  // Determine if comparators should be powered when containers are opened
  boolean shouldPowerComparators();

  // Determine if notifications should be made by checking the remaining time
  boolean shouldNotify(int remaining);

  int getNotificationDelay();

  boolean isNotificationsEnabled();

  boolean isMessageStylesEnabled();

  ClientTextureType getTextureType();

  default boolean isNewTextures () {
    return getTextureType() == ClientTextureType.NEW;
  }

  default boolean isOldTextures() {
    return getTextureType() == ClientTextureType.OLD;
  }

  default boolean isVanillaTextures() {
    return getTextureType() == ClientTextureType.VANILLA;
  }

  default boolean isDefaultTextures() {
    return getTextureType() == ClientTextureType.NEW;
  }

  boolean isDisabled();

  boolean isLootTableBlacklisted(ResourceKey<LootTable> table);

  boolean isDimensionBlocked(ResourceKey<Level> dimension);

  boolean isDimensionDecaying(ResourceKey<Level> dimension);

  boolean isDimensionRefreshing(ResourceKey<Level> dimension);

  Set<ResourceKey<Level>> getDimensionBlacklist();

  Set<ResourceKey<Level>> getDimensionWhitelist();

  Set<ResourceKey<LootTable>> getLootTableBlacklist();

  Set<String> getLootModidBlacklist();

  Set<String> getModidDimensionWhitelist();

  Set<String> getModidDimensionBlacklist();

  boolean isDecaying(ILootrInfoProvider provider);

  boolean isRefreshing(ILootrInfoProvider provider);

  Set<String> getModidDecayWhitelist();

  Set<ResourceKey<LootTable>> getDecayWhitelist();

  Set<ResourceKey<Level>> getDecayDimensions();

  Set<String> getRefreshModids();

  Set<ResourceKey<LootTable>> getRefreshWhitelist();

  Set<ResourceKey<Level>> getRefreshDimensions();

  boolean reportUnresolvedTables();

  boolean isCustomTrapped();

  boolean isWorldBorderSafe(Level level, BlockPos pos);

  boolean isWorldBorderSafe(Level level, ChunkPos pos);

  boolean shouldCheckWorldBorder();

  int getMaximumAge();

  boolean hasExpired(long time);

  boolean shouldConvertMineshafts();

  boolean shouldConvertElytras();

  int getDecayValue();

  boolean shouldDecayAll();

  int getRefreshValue();

  boolean shouldRefreshAll();

  Style getInvalidStyle();

  Style getDecayStyle();

  Style getRefreshStyle();

  Style getChatStyle();

  Component getInvalidTableComponent(ResourceKey<LootTable> lootTable);

  boolean canDestroyOrBreak(Player player);

  boolean isBreakDisabled();

  boolean isBreakEnabled();

  boolean isFakePlayerBreakEnabled();

  boolean shouldPerformDecayWhileTicking ();
  boolean shouldPerformRefreshWhileTicking ();
  boolean shouldStartDecayWhileTicking ();
  boolean shouldStartRefreshWhileTicking ();

  boolean performPiecewiseCheck ();

  default boolean isAwarded(ILootrInfoProvider provider, ServerPlayer player) {
    return isAwarded(provider.getInfoUUID(), player);
  }

  boolean isAwarded(UUID uuid, ServerPlayer player);

  default void award(ILootrInfoProvider provider, ServerPlayer player) {
    award(provider.getInfoUUID(), player);
  }

  void award(UUID id, ServerPlayer player);

  int getRemainingDecayValue(ILootrInfoProvider provider);

  boolean isDecayed(ILootrInfoProvider provider);

  void setDecaying(ILootrInfoProvider provider);

  void removeDecayed(ILootrInfoProvider provider);

  int getRemainingRefreshValue(ILootrInfoProvider provider);

  boolean isRefreshed(ILootrInfoProvider provider);

  void setRefreshing(ILootrInfoProvider provider);

  void removeRefreshed(ILootrInfoProvider provider);

  @Nullable
  BlockState replacementBlockState(BlockState original);

  void handleProviderSneak(@Nullable ILootrInfoProvider provider, ServerPlayer player);

  void handleProviderOpen(@Nullable ILootrInfoProvider provider, ServerPlayer player);

  void handleProviderTick(@Nullable ILootrInfoProvider provider);

  boolean anyUnloadedChunks(ResourceKey<Level> dimension, Set<ChunkPos> chunks);

  <T extends BlockEntity> ILootrBlockEntity resolveBlockEntity (T blockEntity);

  <T extends Entity> ILootrCart resolveEntity (T entity);

  boolean isTaggedStructurePresent (ServerLevel level, ChunkPos chunkPos, TagKey<Structure> tag, BlockPos pos);
}


