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
import noobanidus.mods.lootr.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.api.data.ILootrSavedData;
import noobanidus.mods.lootr.api.data.LootFiller;
import noobanidus.mods.lootr.api.data.inventory.ILootrInventory;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface ILootrAPI {
  Set<UUID> getPlayerIds ();

  Player getPlayer ();

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

  @Nullable
  ILootrSavedData getData (ILootrInfoProvider provider);

  long getLootSeed(long seed);

  boolean shouldDiscard();

  float getExplosionResistance(Block block, float defaultResistance);

  boolean isBlastResistant();

  boolean isBlastImmune();

  float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos position, float defaultProgress);

  int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos, int defaultSignal);

  boolean shouldPowerComparators();

  boolean shouldNotify(int remaining);

  int getNotificationDelay();

  boolean isNotificationsEnabled ();

  boolean isMessageStylesEnabled();

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

  default boolean isAwarded(ILootrInfoProvider provider, ServerPlayer player) {
    return isAwarded(provider.getInfoUUID(), player);
  }
  boolean isAwarded(UUID uuid, ServerPlayer player);
  default void award(ILootrInfoProvider provider, ServerPlayer player) {
    award(provider.getInfoUUID(), player);
  }
  void award(UUID id, ServerPlayer player);
  int getDecayValue(ILootrInfoProvider provider);
  boolean isDecayed(ILootrInfoProvider provider);
  void setDecaying(ILootrInfoProvider provider, int decay);
  void removeDecayed(ILootrInfoProvider provider);
  int getRefreshValue(ILootrInfoProvider provider);
  boolean isRefreshed(ILootrInfoProvider provider);
  void setRefreshing(ILootrInfoProvider provider, int decay);
  void removeRefreshed(ILootrInfoProvider provider);

  @Nullable
  BlockState replacementBlockState(BlockState original);

  void handleProviderSneak(@Nullable ILootrInfoProvider provider, ServerPlayer player);

  void handleProviderOpen(@Nullable ILootrInfoProvider provider, ServerPlayer player) ;

  void handleProviderTick (@Nullable ILootrInfoProvider provider);

  boolean anyUnloadedChunks (ResourceKey<Level> dimension, Set<ChunkPos> chunks);
}


