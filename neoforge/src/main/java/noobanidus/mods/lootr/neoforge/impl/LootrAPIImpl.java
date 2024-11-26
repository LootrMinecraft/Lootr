package noobanidus.mods.lootr.neoforge.impl;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
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
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.common.api.client.ClientTextureType;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.common.impl.DefaultLootrAPIImpl;
import noobanidus.mods.lootr.neoforge.config.ConfigManager;
import noobanidus.mods.lootr.neoforge.event.HandleChunk;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class LootrAPIImpl extends DefaultLootrAPIImpl {
  @Override
  public MinecraftServer getServer() {
    return ServerLifecycleHooks.getCurrentServer();
  }

  @Override
  public boolean isFakePlayer(Player player) {
    if (player instanceof ServerPlayer sPlayer) {
      //noinspection ConstantValue
      if (sPlayer.connection == null) {
        return true;
      }
    }
    return player instanceof FakePlayer;
  }

  @Override
  public long getLootSeed(long seed) {
    if (ConfigManager.RANDOMISE_SEED.get() || seed == -1 || seed == 0) {
      return ThreadLocalRandom.current().nextLong();
    }
    return seed;
  }

  @Override
  public float getExplosionResistance(Block block, float defaultResistance) {
    if (ConfigManager.BLAST_IMMUNE.get()) {
      return Float.MAX_VALUE;
    } else if (ConfigManager.BLAST_RESISTANT.get()) {
      return 16.0f;
    } else {
      return defaultResistance;
    }
  }

  @Override
  public boolean isBlastResistant() {
    return ConfigManager.BLAST_RESISTANT.get();
  }

  @Override
  public boolean isBlastImmune() {
    return ConfigManager.BLAST_IMMUNE.get();
  }

  @Override
  public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos position, float defaultProgress) {
    if (ConfigManager.DISABLE_BREAK.get()) {
      return 0f;
    }
    return defaultProgress;
  }

  @Override
  public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos, int defaultSignal) {
    if (ConfigManager.POWER_COMPARATORS.get()) {
      return 1;
    }
    return defaultSignal;
  }

  @Override
  public boolean shouldPowerComparators() {
    return ConfigManager.POWER_COMPARATORS.get();
  }

  @Override
  public boolean shouldNotify(int remaining) {
    return ConfigManager.shouldNotify(remaining);
  }

  @Override
  public int getNotificationDelay() {
    return ConfigManager.NOTIFICATION_DELAY.get();
  }

  @Override
  public boolean isNotificationsEnabled() {
    return !ConfigManager.DISABLE_NOTIFICATIONS.get();
  }

  @Override
  public boolean isMessageStylesEnabled() {
    return !ConfigManager.DISABLE_MESSAGE_STYLES.get();
  }

  @Override
  public ClientTextureType getTextureType() {
    if (ConfigManager.isNewTextures()) {
      return ClientTextureType.NEW;
    } else if (ConfigManager.isVanillaTextures()) {
      return ClientTextureType.VANILLA;
    } else {
      return ClientTextureType.OLD;
    }
  }

  @Override
  public boolean isDisabled() {
    return ConfigManager.DISABLE.get();
  }

  @Override
  public boolean isLootTableBlacklisted(ResourceKey<LootTable> table) {
    return ConfigManager.isLootTableBlacklisted(table);
  }

  @Override
  public boolean isDimensionBlocked(ResourceKey<Level> dimension) {
    return ConfigManager.isDimensionBlocked(dimension);
  }

  @Override
  public boolean isDimensionDecaying(ResourceKey<Level> dimension) {
    return ConfigManager.isDimensionDecaying(dimension);
  }

  @Override
  public boolean isDimensionRefreshing(ResourceKey<Level> dimension) {
    return ConfigManager.isDimensionRefreshing(dimension);
  }

  @Override
  public Set<ResourceKey<Level>> getDimensionBlacklist() {
    return ConfigManager.getDimensionBlacklist();
  }

  @Override
  public Set<ResourceKey<Level>> getDimensionWhitelist() {
    return ConfigManager.getDimensionWhitelist();
  }

  @Override
  public Set<ResourceKey<LootTable>> getLootTableBlacklist() {
    return ConfigManager.getLootBlacklist();
  }

  @Override
  public Set<String> getLootModidBlacklist() {
    return ConfigManager.getLootModids();
  }

  @Override
  public Set<String> getModidDimensionWhitelist() {
    return ConfigManager.getDimensionModidWhitelist();
  }

  @Override
  public Set<String> getModidDimensionBlacklist() {
    return ConfigManager.getDimensionModidBlacklist();
  }

  @Override
  public boolean isDecaying(ILootrInfoProvider provider) {
    return ConfigManager.isDecaying(provider);
  }

  @Override
  public boolean isRefreshing(ILootrInfoProvider provider) {
    return ConfigManager.isRefreshing(provider);
  }

  @Override
  public Set<String> getModidDecayWhitelist() {
    return ConfigManager.getDecayMods();
  }

  @Override
  public Set<ResourceKey<LootTable>> getDecayWhitelist() {
    return ConfigManager.getDecayingTables();
  }

  @Override
  public Set<ResourceKey<Level>> getDecayDimensions() {
    return ConfigManager.getDecayDimensions();
  }

  @Override
  public Set<String> getRefreshModids() {
    return ConfigManager.getRefreshMods();
  }

  @Override
  public Set<ResourceKey<LootTable>> getRefreshWhitelist() {
    return ConfigManager.getRefreshingTables();
  }

  @Override
  public Set<ResourceKey<Level>> getRefreshDimensions() {
    return ConfigManager.getRefreshDimensions();
  }

  @Override
  public boolean reportUnresolvedTables() {
    return ConfigManager.REPORT_UNRESOLVED_TABLES.get();
  }

  @Override
  public boolean isCustomTrapped() {
    return ConfigManager.TRAPPED_CUSTOM.get();
  }

  @Override
  public boolean shouldCheckWorldBorder() {
    return ConfigManager.CHECK_WORLD_BORDER.get();
  }

  @Override
  public int getMaximumAge() {
    return ConfigManager.MAXIMUM_AGE.get();
  }

  @Override
  public boolean hasExpired(long time) {
    return time > ConfigManager.MAXIMUM_AGE.get();
  }

  @Override
  public boolean shouldConvertMineshafts() {
    return ConfigManager.CONVERT_MINESHAFTS.get();
  }

  @Override
  public boolean shouldConvertElytras() {
    return ConfigManager.CONVERT_ELYTRAS.get();
  }

  @Override
  public int getDecayValue() {
    return ConfigManager.DECAY_VALUE.get();
  }

  @Override
  public boolean shouldDecayAll() {
    return ConfigManager.DECAY_ALL.get();
  }

  @Override
  public int getRefreshValue() {
    return ConfigManager.REFRESH_VALUE.get();
  }

  @Override
  public boolean shouldRefreshAll() {
    return ConfigManager.REFRESH_ALL.get();
  }

  @Override
  public Style getInvalidStyle() {
    return ConfigManager.DISABLE_MESSAGE_STYLES.get() ? Style.EMPTY : Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED)).withBold(true);
  }

  @Override
  public Style getDecayStyle() {
    return ConfigManager.DISABLE_MESSAGE_STYLES.get() ? Style.EMPTY : Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED)).withBold(true);
  }

  @Override
  public Style getRefreshStyle() {
    return ConfigManager.DISABLE_MESSAGE_STYLES.get() ? Style.EMPTY : Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)).withBold(true);
  }

  @Override
  public Style getChatStyle() {
    return ConfigManager.DISABLE_MESSAGE_STYLES.get() ? Style.EMPTY : Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA));
  }

  @Override
  public boolean canDestroyOrBreak(Player player) {
    return (isFakePlayer(player) && ConfigManager.ENABLE_FAKE_PLAYER_BREAK.get()) || ConfigManager.ENABLE_BREAK.get();
  }

  @Override
  public boolean isBreakDisabled() {
    return ConfigManager.DISABLE_BREAK.get();
  }

  @Override
  public boolean isBreakEnabled() {
    return ConfigManager.ENABLE_BREAK.get();
  }

  @Override
  public boolean isFakePlayerBreakEnabled() {
    return ConfigManager.ENABLE_FAKE_PLAYER_BREAK.get();
  }

  @Override
  public boolean shouldDropPlayerLoot() {
    return ConfigManager.SHOULD_DROP_PLAYER_LOOT.get();
  }

  @Override
  public boolean shouldPerformDecayWhileTicking() {
    return ConfigManager.PERFORM_DECAY_WHILE_TICKING.get();
  }

  @Override
  public boolean shouldPerformRefreshWhileTicking() {
    return ConfigManager.PERFORM_REFRESH_WHILE_TICKING.get();
  }

  @Override
  public boolean shouldStartDecayWhileTicking() {
    return ConfigManager.START_DECAY_WHILE_TICKING.get();
  }

  @Override
  public boolean shouldStartRefreshWhileTicking() {
    return ConfigManager.START_REFRESH_WHILE_TICKING.get();
  }

  @Override
  public boolean performPiecewiseCheck() {
    return ConfigManager.PERFORM_PIECEWISE_CHECK.get();
  }

  @Override
  @Nullable
  public BlockState replacementBlockState(BlockState original) {
    return ConfigManager.replacement(original);
  }

  @Override
  public Component getInvalidTableComponent(ResourceKey<LootTable> lootTable) {
    return Component.translatable("lootr.message.invalid_table", lootTable.location().getNamespace(), lootTable.toString()).setStyle(ConfigManager.DISABLE_MESSAGE_STYLES.get() ? Style.EMPTY : Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_RED)).withBold(true));
  }

  @Override
  public boolean anyUnloadedChunks(ResourceKey<Level> dimension, Set<ChunkPos> chunks) {
    synchronized (HandleChunk.LOADED_CHUNKS) {
      Set<ChunkPos> syncedChunks = HandleChunk.LOADED_CHUNKS.get(dimension);
      if (syncedChunks == null || syncedChunks.isEmpty()) {
        return true;
      }

      for (ChunkPos myPos : chunks) {
        if (!syncedChunks.contains(myPos)) {
          return true;
        }
      }
    }

    return false;
  }
}
