package noobanidus.mods.lootr.fabric.impl;

import net.fabricmc.fabric.api.entity.FakePlayer;
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
import noobanidus.mods.lootr.common.api.client.ClientTextureType;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.common.impl.DefaultLootrAPIImpl;
import noobanidus.mods.lootr.fabric.config.ConfigManager;
import noobanidus.mods.lootr.fabric.event.HandleChunk;
import noobanidus.mods.lootr.fabric.event.LootrEventsInit;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class LootrAPIImpl extends DefaultLootrAPIImpl {
  @Override
  public MinecraftServer getServer() {
    return LootrEventsInit.serverInstance;
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
    if (ConfigManager.get().seed.randomize_seed || seed == -1 || seed == 0) {
      return ThreadLocalRandom.current().nextLong();
    }
    return seed;
  }

  @Override
  public float getExplosionResistance(Block block, float defaultResistance) {
    if (ConfigManager.get().breaking.blast_immune) {
      return Float.MAX_VALUE;
    } else if (ConfigManager.get().breaking.blast_resistant) {
      return 16.0f;
    } else {
      return defaultResistance;
    }
  }

  @Override
  public boolean isBlastResistant() {
    return ConfigManager.get().breaking.blast_resistant;
  }

  @Override
  public boolean isBlastImmune() {
    return ConfigManager.get().breaking.blast_immune;
  }

  @Override
  public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos position, float defaultProgress) {
    if (ConfigManager.get().breaking.disable_break) {
      return 0f;
    }
    return defaultProgress;
  }

  @Override
  public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos, int defaultSignal) {
    if (shouldPowerComparators()) {
      return 1;
    }
    return defaultSignal;
  }

  @Override
  public boolean shouldPowerComparators() {
    return ConfigManager.get().breaking.power_comparators;
  }

  @Override
  public boolean shouldNotify(int remaining) {
    return ConfigManager.shouldNotify(remaining);
  }

  @Override
  public int getNotificationDelay() {
    return ConfigManager.get().notifications.notification_delay;
  }

  @Override
  public boolean isNotificationsEnabled() {
    return !ConfigManager.get().notifications.disable_notifications;
  }

  @Override
  public boolean isMessageStylesEnabled() {
    return !ConfigManager.get().notifications.disable_message_styles;
  }

  @Override
  public ClientTextureType getTextureType() {
    if (ConfigManager.isVanillaTextures()) {
      return ClientTextureType.VANILLA;
    } else if (ConfigManager.isNewTextures()) {
      return ClientTextureType.NEW;
    } else {
      return ClientTextureType.OLD;
    }
  }

  @Override
  public boolean isDisabled() {
    return ConfigManager.get().conversion.disable;
  }

  @Override
  public boolean isLootTableBlacklisted(ResourceKey<LootTable> table) {
    return ConfigManager.isBlacklisted(table);
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
    return ConfigManager.getLootModidsBlacklist();
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
    return ConfigManager.get().debug.report_unresolved_tables;
  }

  @Override
  public boolean isCustomTrapped() {
    return ConfigManager.get().breaking.trapped_custom;
  }

  @Override
  public boolean shouldCheckWorldBorder() {
    return ConfigManager.get().conversion.world_border;
  }

  @Override
  public int getMaximumAge() {
    return ConfigManager.get().conversion.max_entry_age;
  }

  @Override
  public boolean hasExpired(long time) {
    return time > getMaximumAge();
  }

  @Override
  public boolean shouldConvertMineshafts() {
    return ConfigManager.get().conversion.convert_mineshafts;
  }

  @Override
  public boolean shouldConvertElytras() {
    return ConfigManager.get().conversion.convert_elytras;
  }

  @Override
  public int getDecayValue() {
    return ConfigManager.get().decay.decay_value;
  }

  @Override
  public boolean shouldDecayAll() {
    return ConfigManager.get().decay.decay_all;
  }

  @Override
  public int getRefreshValue() {
    return ConfigManager.get().refresh.refresh_value;
  }

  @Override
  public boolean shouldRefreshAll() {
    return ConfigManager.get().refresh.refresh_all;
  }

  @Override
  public Style getInvalidStyle() {
    return !isMessageStylesEnabled() ? Style.EMPTY : Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED)).withBold(true);
  }

  @Override
  public Style getDecayStyle() {
    return !isMessageStylesEnabled() ? Style.EMPTY : Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED)).withBold(true);
  }

  @Override
  public Style getRefreshStyle() {
    return !isMessageStylesEnabled() ? Style.EMPTY : Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)).withBold(true);
  }

  @Override
  public Style getChatStyle() {
    return !isMessageStylesEnabled() ? Style.EMPTY : Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA));
  }

  @Override
  public boolean canDestroyOrBreak(Player player) {
    return (isFakePlayer(player) && isFakePlayerBreakEnabled() || isBreakEnabled());
  }

  @Override
  public boolean isBreakDisabled() {
    return ConfigManager.get().breaking.disable_break;
  }

  @Override
  public boolean isBreakEnabled() {
    return ConfigManager.get().breaking.enable_break;
  }

  @Override
  public boolean isFakePlayerBreakEnabled() {
    return ConfigManager.get().breaking.enable_fake_player_break;
  }

  @Override
  public boolean shouldDropPlayerLoot() {
    return ConfigManager.get().breaking.should_drop_player_loot;
  }

  @Override
  public boolean shouldPerformDecayWhileTicking() {
    return ConfigManager.get().decay.perform_tick_decay;
  }

  @Override
  public boolean shouldPerformRefreshWhileTicking() {
    return ConfigManager.get().refresh.perform_tick_refresh;
  }

  @Override
  public boolean shouldStartDecayWhileTicking() {
    return ConfigManager.get().decay.start_tick_decay;
  }

  @Override
  public boolean shouldStartRefreshWhileTicking() {
    return ConfigManager.get().refresh.start_tick_refresh;
  }

  @Override
  public boolean performPiecewiseCheck() {
    return ConfigManager.shouldPerformPiecewiseCheck();
  }

  @Override
  @Nullable
  public BlockState replacementBlockState(BlockState original) {
    return ConfigManager.replacement(original);
  }

  @Override
  public Component getInvalidTableComponent(ResourceKey<LootTable> lootTable) {
    return Component.translatable("lootr.message.invalid_table", lootTable.location().getNamespace(), lootTable.toString()).setStyle(!isMessageStylesEnabled() ? Style.EMPTY : Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_RED)).withBold(true));
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
