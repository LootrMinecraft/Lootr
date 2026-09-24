package noobanidus.mods.lootr.neoforge.impl;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.common.api.client.ClientTextureType;
import noobanidus.mods.lootr.common.api.config.SaveMode;
import noobanidus.mods.lootr.common.api.config.SyncedConfig;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.common.impl.DefaultLootrAPIImpl;
import noobanidus.mods.lootr.neoforge.config.ConfigManager;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class LootrAPIImpl extends DefaultLootrAPIImpl {
  @Override
  @Nullable
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
  public boolean isBlastResistant() {
    return ConfigManager.BLAST_RESISTANT.get();
  }

  @Override
  public boolean isBlastImmune() {
    return ConfigManager.BLAST_IMMUNE.get();
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
  @Deprecated
  public boolean shouldConvertMineshafts() {
    return ConfigManager.CONVERT_MINESHAFTS.get();
  }

  @Override
  @Deprecated
  public boolean shouldConvertElytras() {
    return false;
  }

  @Override
  public boolean shouldConvertElytrasToChests() {
    return ConfigManager.CONVERT_ELYTRAS_TO_CHESTS.get();
  }

  @Override
  public boolean shouldConvertElytrasToItemFrames() {
    return ConfigManager.CONVERT_ELYTRAS_TO_ITEM_FRAMES.get();
  }

  @Override
  public boolean shouldConvertStructureItemFrames() {
    return ConfigManager.CONVERT_ITEM_FRAMES.get();
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
  public boolean isBreakDisabled() {
    return SyncedConfig.getDisableBreak(ConfigManager.DISABLE_BREAK.get());
  }

  @Override
  public boolean isBreakEnabled() {
    return SyncedConfig.getEnableBreak(ConfigManager.ENABLE_BREAK.get());
  }

  @Override
  public boolean isFakePlayerBreakEnabled() {
    return ConfigManager.ENABLE_FAKE_PLAYER_BREAK.get();
  }

  @Override
  public boolean canBrushablesSelfSupport() {
    return ConfigManager.BRUSHABLES_SELF_SUPPORT.get();
  }

  @Override
  public boolean canItemFramesSelfSupport() {
    return ConfigManager.ITEM_FRAMES_SELF_SUPPORT.get();
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
  public boolean shouldWarnNoLootTables() {
    return !ConfigManager.SHOULD_WARN_NO_LOOT_TABLE_AT_GENERATION.get();
  }

  @Override
  public boolean performPiecewiseCheck() {
    return ConfigManager.shouldPerformPiecewiseCheck();
  }

  @Override
  public boolean shouldBypassSpawnProtection() {
    return ConfigManager.BYPASS_SPAWN_PROTECTION.get();
  }

  @Override
  public boolean shouldReplaceWhenDecayed() {
    return ConfigManager.REPLACE_WHEN_DECAYED.get();
  }

  @Override
  public SaveMode getFileSaveMode() {
    return ConfigManager.SAVE_MODE.get();
  }

  @Override
  public boolean shouldDisplayUnopenedParticles() {
    return ConfigManager.UNOPENED_PARTICLES.get();
  }

  @Override
  public int getTickDelay() {
    return ConfigManager.TICK_DELAY.get();
  }

  @Override
  public boolean breakToDropLoot() {
    return ConfigManager.BREAK_TO_DROP_LOOT.get();
  }

  @Override
  public boolean isTeamLoot() {
    return SyncedConfig.getTeamLootEnabled(ConfigManager.TEAM_LOOT.get());
  }

  @Override
  public ResourceLocation getPinnedTeamResolver() {
    return SyncedConfig.getPinnedResolver(ConfigManager.getPinnedTeamResolver());
  }

  @Override
  public boolean shouldDisplayRefreshParticles() {
    return ConfigManager.REFRESH_PARTICLES.get();
  }

  @Override
  public boolean shouldDisplayDecayParticles() {
    return ConfigManager.DECAY_PARTICLES.get();
  }

  @Override
  public boolean shouldDisplayToasts() {
    return ConfigManager.TOASTS.get();
  }
}
