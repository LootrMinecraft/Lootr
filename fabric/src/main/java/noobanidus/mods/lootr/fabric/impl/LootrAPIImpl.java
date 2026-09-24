package noobanidus.mods.lootr.fabric.impl;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.client.ClientTextureType;
import noobanidus.mods.lootr.common.api.config.SaveMode;
import noobanidus.mods.lootr.common.api.config.SyncedConfig;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.common.impl.DefaultLootrAPIImpl;
import noobanidus.mods.lootr.fabric.config.ConfigManager;
import noobanidus.mods.lootr.fabric.event.LootrEventsInit;
import org.jetbrains.annotations.NotNull;

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
  public boolean isBlastResistant() {
    return ConfigManager.get().breaking.blast_resistant;
  }

  @Override
  public boolean isBlastImmune() {
    return ConfigManager.get().breaking.blast_immune;
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
  @Deprecated
  public boolean shouldConvertMineshafts() {
    return ConfigManager.get().conversion.convert_mineshafts;
  }

  @Override
  @Deprecated
  public boolean shouldConvertElytras() {
    return false;
  }

  @Override
  public boolean shouldConvertElytrasToChests() {
    return ConfigManager.get().conversion.convert_elytras_to_chests;
  }

  @Override
  public boolean shouldConvertElytrasToItemFrames() {
    return ConfigManager.get().conversion.convert_elytras_to_item_frames;
  }

  @Override
  public boolean shouldConvertStructureItemFrames() {
    return ConfigManager.get().conversion.convert_structure_item_frames;
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
  public boolean isBreakDisabled() {
    return SyncedConfig.getDisableBreak(ConfigManager.get().breaking.disable_break);
  }

  @Override
  public boolean isBreakEnabled() {
    return SyncedConfig.getEnableBreak(ConfigManager.get().breaking.enable_break);
  }

  @Override
  public boolean isFakePlayerBreakEnabled() {
    return ConfigManager.get().breaking.enable_fake_player_break;
  }

  @Override
  public boolean canBrushablesSelfSupport() {
    return ConfigManager.get().breaking.brushables_self_support;
  }

  @Override
  public boolean canItemFramesSelfSupport() {
    return ConfigManager.get().breaking.item_frames_self_support;
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
  public boolean shouldWarnNoLootTables() {
    return !ConfigManager.get().conversion.skip_logging_no_loot_table_at_generation;
  }

  @Override
  public boolean performPiecewiseCheck() {
    return ConfigManager.shouldPerformPiecewiseCheck();
  }

  @Override
  public boolean shouldBypassSpawnProtection() {
    return SyncedConfig.getBypassSpawnProtection(ConfigManager.get().conversion.bypass_spawn_protection);
  }

  @Override
  public boolean shouldReplaceWhenDecayed() {
    return ConfigManager.get().decay.replace_when_decayed;
  }

  @Override
  public SaveMode getFileSaveMode() {
    return ConfigManager.get().conversion.save_mode;
  }

  @Override
  public boolean shouldDisplayUnopenedParticles() {
    return ConfigManager.get().client.unopened_particles;
  }

  @Override
  public int getTickDelay() {
    return ConfigManager.get().conversion.tickDelay;
  }

  @Override
  public boolean breakToDropLoot() {
    return ConfigManager.get().breaking.break_to_drop_loot;
  }

  @Override
  public boolean isTeamLoot() {
    return SyncedConfig.getTeamLootEnabled(ConfigManager.get().team.team_loot);
  }

  @Override
  public @NotNull ResourceLocation getPinnedTeamResolver() {
    return SyncedConfig.getPinnedResolver(ConfigManager.getPinnedTeamResolver());
  }

  @Override
  public boolean shouldDisplayRefreshParticles() {
    return ConfigManager.get().client.refresh_particles;
  }

  @Override
  public boolean shouldDisplayDecayParticles() {
    return ConfigManager.get().client.decay_particles;
  }
}
