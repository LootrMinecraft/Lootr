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
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.ILootrAPI;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.MenuBuilder;
import noobanidus.mods.lootr.common.api.client.ClientTextureType;
import noobanidus.mods.lootr.common.api.data.DefaultLootFiller;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.common.api.data.ILootrSavedData;
import noobanidus.mods.lootr.common.api.data.LootFiller;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrCart;
import noobanidus.mods.lootr.common.api.data.inventory.ILootrInventory;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.data.DataStorage;
import noobanidus.mods.lootr.common.impl.LootrServiceRegistry;
import noobanidus.mods.lootr.fabric.config.ConfigManager;
import noobanidus.mods.lootr.fabric.event.HandleChunk;
import noobanidus.mods.lootr.fabric.event.LootrEventsInit;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

// TODO: A lot of this can get moved to the default implementation
public class LootrAPIImpl implements ILootrAPI {
  @Override
  public void handleProviderSneak(@Nullable ILootrInfoProvider provider, ServerPlayer player) {
    if (provider == null) {
      return;
    }
    if (provider.removeVisualOpener(player)) {
      provider.performClose(player);
      provider.performUpdate(player);
    }
  }

  @Override
  public void handleProviderOpen(@Nullable ILootrInfoProvider provider, ServerPlayer player) {
    if (provider == null) {
      return;
    }
    if (player.isSpectator()) {
      player.openMenu(null);
      return;
    }

    if (provider.getInfoUUID() == null) {
      player.displayClientMessage(Component.translatable("lootr.message.invalid_block").setStyle(LootrAPI.getInvalidStyle()), true);
      return;
    }
    // This handles the `lockKey` parameter
    if (provider instanceof BaseContainerBlockEntity baseContainer) {
      if (!baseContainer.canOpen(player)) {
        return;
      }
    }
    if (LootrAPI.isDecayed(provider)) {
      provider.performDecay();
      player.displayClientMessage(Component.translatable("lootr.message.decayed").setStyle(LootrAPI.getDecayStyle()), true);
      LootrAPI.removeDecayed(provider);
      return;
    } else {
      int decayValue = LootrAPI.getRemainingDecayValue(provider);
      if (decayValue > 0 && LootrAPI.shouldNotify(decayValue)) {
        player.displayClientMessage(Component.translatable("lootr.message.decay_in", decayValue / 20).setStyle(LootrAPI.getDecayStyle()), true);
      } else if (decayValue == -1) {
        if (LootrAPI.isDecaying(provider)) {
          LootrAPI.setDecaying(provider);
          player.displayClientMessage(Component.translatable("lootr.message.decay_start", LootrAPI.getDecayValue() / 20).setStyle(LootrAPI.getDecayStyle()), true);
        }
      }
    }
    provider.performTrigger(player);
    boolean shouldUpdate = false;
    if (LootrAPI.isRefreshed(provider)) {
      provider.performRefresh();
      provider.performClose();
      LootrAPI.removeRefreshed(provider);
      player.displayClientMessage(Component.translatable("lootr.message.refreshed").setStyle(LootrAPI.getRefreshStyle()), true);
      shouldUpdate = true;
    }
    int refreshValue = LootrAPI.getRemainingRefreshValue(provider);
    if (refreshValue > 0 && LootrAPI.shouldNotify(refreshValue)) {
      player.displayClientMessage(Component.translatable("lootr.message.refresh_in", refreshValue / 20).setStyle(LootrAPI.getRefreshStyle()), true);
    } else if (refreshValue == -1) {
      if (LootrAPI.isRefreshing(provider)) {
        LootrAPI.setRefreshing(provider);
        player.displayClientMessage(Component.translatable("lootr.message.refresh_start", LootrAPI.getRefreshValue() / 20).setStyle(LootrAPI.getRefreshStyle()), true);
      }
    }
    MenuProvider menuProvider = LootrAPI.getInventory(provider, player, DefaultLootFiller.getInstance());
    if (menuProvider == null) {
      return;
    }
    if (!provider.hasOpened(player)) {
      player.awardStat(LootrRegistry.getLootedStat());
      LootrRegistry.getStatTrigger().trigger(player);
    }
    if (provider.addOpener(player)) {
      provider.performOpen(player);
      shouldUpdate = true;
    }

    if (shouldUpdate) {
      provider.performUpdate(player);
    }
    player.openMenu(menuProvider);
    PiglinAi.angerNearbyPiglins(player, true);
  }

  @Override
  public void handleProviderTick(@Nullable ILootrInfoProvider provider) {
    if (provider == null) {
      return;
    }

    if (provider.getInfoUUID() == null) {
      return;
    }

    if (provider.hasBeenOpened() && LootrAPI.shouldPerformDecayWhileTicking() && LootrAPI.isDecayed(provider)) {
      provider.performDecay();
      LootrAPI.removeDecayed(provider);
      return;
    } else if (provider.hasBeenOpened() && LootrAPI.shouldStartDecayWhileTicking() && !LootrAPI.isDecayed(provider)) {
      int decayValue = LootrAPI.getRemainingDecayValue(provider);
      if (decayValue == -1) {
        if (LootrAPI.isDecaying(provider)) {
          LootrAPI.setDecaying(provider);
        }
      }
    }
    if (provider.hasBeenOpened() && LootrAPI.shouldPerformRefreshWhileTicking() && LootrAPI.isRefreshed(provider)) {
      provider.performRefresh();
      provider.performClose();
      LootrAPI.removeRefreshed(provider);
      provider.performUpdate();
    }
    if (provider.hasBeenOpened() && LootrAPI.shouldStartRefreshWhileTicking() && !LootrAPI.isRefreshed(provider)) {
      int refreshValue = LootrAPI.getRemainingRefreshValue(provider);
      if (refreshValue == -1) {
        if (LootrAPI.isRefreshing(provider)) {
          LootrAPI.setRefreshing(provider);
        }
      }
    }
  }

  @Override
  public Set<UUID> getPlayerIds() {
    MinecraftServer server = getServer();
    if (server == null) {
      return Set.of();
    }

    Set<UUID> result = new HashSet<>();
    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
      if (isFakePlayer(player)) {
        continue;
      }
      UUID thisUuid = player.getUUID();
      // I don't know if this is possible but let's just check for it anyway.
      //noinspection ConstantValue
      if (thisUuid != null) {
        result.add(thisUuid);
      }
    }
    return result;
  }

  // TODO:
  @Override
  public Player getPlayer() {
    return null;
  }

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
  public boolean clearPlayerLoot(UUID id) {
    return DataStorage.clearInventories(id);
  }

  @Override
  public ILootrInventory getInventory(ILootrInfoProvider provider, ServerPlayer player, LootFiller filler) {
    return DataStorage.getInventory(provider, player, filler);
  }

  @Override
  public ILootrInventory getInventory(ILootrInfoProvider provider, ServerPlayer player, LootFiller filler, MenuBuilder menuBuilder) {
    ILootrInventory inventory = DataStorage.getInventory(provider, player, filler);
    if (inventory != null) {
      inventory.setMenuBuilder(menuBuilder);
    }
    return inventory;
  }

  @Override
  public @Nullable ILootrSavedData getData(ILootrInfoProvider provider) {
    return DataStorage.getData(provider);
  }

  @Override
  public long getLootSeed(long seed) {
    if (ConfigManager.get().seed.randomize_seed || seed == -1 || seed == 0) {
      return ThreadLocalRandom.current().nextLong();
    }
    return seed;
  }

  @Override
  public boolean shouldDiscard() {
    return LootrAPI.shouldDiscardIdAndOpeners;
  }

  @Override
  public float getExplosionResistance(Block block, float defaultResistance) {
    if (ConfigManager.get().breaking.blast_resistant) {
      return 16.0f;
    } else if (ConfigManager.get().breaking.blast_immune) {
      return Float.MAX_VALUE;
    } else {
      return defaultResistance;
    }
  }

  @Override
  public boolean isBlastResistant () {
    return ConfigManager.get().breaking.blast_resistant;
  }

  @Override
  public boolean isBlastImmune () {
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
  public boolean shouldPowerComparators () {
    return ConfigManager.get().breaking.power_comparators;
  }

  @Override
  public boolean shouldNotify(int remaining) {
    return ConfigManager.shouldNotify(remaining);
  }

  @Override
  public int getNotificationDelay () {
    return ConfigManager.get().notifications.notification_delay;
  }

  @Override
  public boolean isNotificationsEnabled () {
    return !ConfigManager.get().notifications.disable_notifications;
  }

  @Override
  public boolean isMessageStylesEnabled() {
    return !ConfigManager.get().notifications.disable_message_styles;
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
  public Set<ResourceKey<Level>> getDimensionWhitelist () {
    return ConfigManager.getDimensionWhitelist();
  }

  @Override
  public Set<ResourceKey<LootTable>> getLootTableBlacklist () {
    return ConfigManager.getLootBlacklist();
  }

  @Override
  public Set<String> getLootModidBlacklist () {
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
  public boolean isWorldBorderSafe(Level level, BlockPos pos) {
    if (!shouldCheckWorldBorder()) {
      return true;
    }
    return level.getWorldBorder().isWithinBounds(pos);
  }

  @Override
  public boolean isWorldBorderSafe(Level level, ChunkPos pos) {
    if (!shouldCheckWorldBorder()) {
      return true;
    }
    return level.getWorldBorder().isWithinBounds(pos);
  }

  @Override
  public boolean shouldCheckWorldBorder () {
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
  public boolean shouldDecayAll () {
    return ConfigManager.get().decay.decay_all;
  }

  @Override
  public int getRefreshValue() {
    return ConfigManager.get().refresh.refresh_value;
  }

  @Override
  public boolean shouldRefreshAll () {
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
  public boolean isFakePlayerBreakEnabled () {
    return ConfigManager.get().breaking.enable_fake_player_break;
  }

  @Override
  public boolean shouldPerformDecayWhileTicking () {
    return ConfigManager.get().decay.perform_tick_decay;
  }

  @Override
  public boolean shouldPerformRefreshWhileTicking () {
    return ConfigManager.get().refresh.perform_tick_refresh;
  }

  @Override
  public boolean shouldStartDecayWhileTicking () {
    return ConfigManager.get().decay.start_tick_decay;
  }

  @Override
  public boolean shouldStartRefreshWhileTicking () {
    return ConfigManager.get().refresh.start_tick_refresh;
  }

  @Override
  public boolean isAwarded(UUID uuid, ServerPlayer player) {
    return DataStorage.isAwarded(uuid, player);
  }

  @Override
  public void award(UUID id, ServerPlayer player) {
    DataStorage.award(id, player);
  }

  @Override
  public int getRemainingDecayValue(ILootrInfoProvider provider) {
    return DataStorage.getDecayValue(provider);
  }

  @Override
  public boolean isDecayed(ILootrInfoProvider provider) {
    return DataStorage.isDecayed(provider);
  }

  @Override
  public void setDecaying(ILootrInfoProvider provider) {
    DataStorage.setDecaying(provider);
  }

  @Override
  public void removeDecayed(ILootrInfoProvider provider) {
    DataStorage.removeDecayed(provider);
  }

  @Override
  public int getRemainingRefreshValue(ILootrInfoProvider provider) {
    return DataStorage.getRefreshValue(provider);
  }

  @Override
  public boolean isRefreshed(ILootrInfoProvider provider) {
    return DataStorage.isRefreshed(provider);
  }

  @Override
  public void setRefreshing(ILootrInfoProvider provider) {
    DataStorage.setRefreshing(provider);
  }

  @Override
  public void removeRefreshed(ILootrInfoProvider provider) {
    DataStorage.removeRefreshed(provider);
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
  public boolean anyUnloadedChunks (ResourceKey<Level> dimension, Set<ChunkPos> chunks) {
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

  @Override
  public <T extends BlockEntity> ILootrBlockEntity resolveBlockEntity(T blockEntity) {
    return LootrServiceRegistry.convertBlockEntity(blockEntity);
  }

  @Override
  public <T extends Entity>ILootrCart resolveEntity(T entity) {
    return LootrServiceRegistry.convertEntity(entity);
  }
}
