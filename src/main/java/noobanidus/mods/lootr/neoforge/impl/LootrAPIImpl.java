package noobanidus.mods.lootr.neoforge.impl;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.api.ILootrAPI;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.MenuBuilder;
import noobanidus.mods.lootr.api.client.ClientTextureType;
import noobanidus.mods.lootr.api.data.DefaultLootFiller;
import noobanidus.mods.lootr.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.api.data.ILootrSavedData;
import noobanidus.mods.lootr.api.data.LootFiller;
import noobanidus.mods.lootr.api.data.inventory.ILootrInventory;
import noobanidus.mods.lootr.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.data.DataStorage;
import noobanidus.mods.lootr.neoforge.config.ConfigManager;
import noobanidus.mods.lootr.neoforge.event.HandleChunk;
import noobanidus.mods.lootr.neoforge.network.client.ClientHandlers;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

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
    if (LootrAPI.isDecayed(provider)) {
      provider.performDecay();
      player.displayClientMessage(Component.translatable("lootr.message.decayed").setStyle(LootrAPI.getDecayStyle()), true);
      LootrAPI.removeDecayed(provider);
      return;
    } else {
      int decayValue = LootrAPI.getDecayValue(provider);
      if (decayValue > 0 && LootrAPI.shouldNotify(decayValue)) {
        player.displayClientMessage(Component.translatable("lootr.message.decay_in", decayValue / 20).setStyle(LootrAPI.getDecayStyle()), true);
      } else if (decayValue == -1) {
        if (LootrAPI.isDecaying(provider)) {
          LootrAPI.setDecaying(provider, decayValue);
          player.displayClientMessage(Component.translatable("lootr.message.decay_start", decayValue / 20).setStyle(LootrAPI.getDecayStyle()), true);
        }
      }
    }
    provider.performTrigger(player);
    boolean shouldUpdate = false;
    if (LootrAPI.isRefreshed(provider)) {
      provider.performRefresh();
      LootrAPI.removeRefreshed(provider);
      player.displayClientMessage(Component.translatable("lootr.message.refreshed").setStyle(LootrAPI.getRefreshStyle()), true);
      shouldUpdate = true;
    }
    int refreshValue = LootrAPI.getRefreshValue(provider);
    if (refreshValue > 0 && LootrAPI.shouldNotify(refreshValue)) {
      player.displayClientMessage(Component.translatable("lootr.message.refresh_in", refreshValue / 20).setStyle(LootrAPI.getRefreshStyle()), true);
    } else if (refreshValue == -1) {
      if (LootrAPI.isRefreshing(provider)) {
        LootrAPI.setRefreshing(provider, refreshValue);
        player.displayClientMessage(Component.translatable("lootr.message.refresh_start", refreshValue / 20).setStyle(LootrAPI.getRefreshStyle()), true);
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
    // TODO: Opened stat
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

    if (LootrAPI.shouldPerformDecayWhileTicking() && LootrAPI.isDecayed(provider)) {
      provider.performDecay();
      LootrAPI.removeDecayed(provider);
      return;
    } else if (LootrAPI.shouldStartDecayWhileTicking() && !LootrAPI.isDecayed(provider)) {
      int decayValue = LootrAPI.getDecayValue(provider);
      if (decayValue == -1) {
        if (LootrAPI.isDecaying(provider)) {
          LootrAPI.setDecaying(provider, decayValue);
        }
      }
    }
    if (LootrAPI.shouldPerformRefreshWhileTicking() && LootrAPI.isRefreshed(provider)) {
      provider.performRefresh();
      LootrAPI.removeRefreshed(provider);
      provider.performUpdate();
    }
    if (LootrAPI.shouldStartRefreshWhileTicking() && !LootrAPI.isRefreshed(provider)) {
      int refreshValue = LootrAPI.getRefreshValue(provider);
      if (refreshValue == -1) {
        if (LootrAPI.isRefreshing(provider)) {
          LootrAPI.setRefreshing(provider, refreshValue);
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
      // TODO Offline servers?
      if (thisUuid != null) {
        result.add(thisUuid);
      }
    }
    return result;
  }

  @Override
  public Player getPlayer() {
    if (FMLEnvironment.dist == Dist.CLIENT) {
      return ClientHandlers.getPlayer();
    } else {
      return null;
    }
  }

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
    if (ConfigManager.RANDOMISE_SEED.get() || seed == -1 || seed == 0) {
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
    if (ConfigManager.BLAST_RESISTANT.get()) {
      return 16.0f;
    } else if (ConfigManager.BLAST_IMMUNE.get()) {
      return Float.MAX_VALUE;
    } else {
      return defaultResistance;
    }
  }

  @Override
  public boolean isBlastResistant () {
    return ConfigManager.BLAST_RESISTANT.get();
  }

  @Override
  public boolean isBlastImmune () {
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
  public boolean shouldPowerComparators () {
    return ConfigManager.POWER_COMPARATORS.get();
  }

  @Override
  public boolean shouldNotify(int remaining) {
    return ConfigManager.shouldNotify(remaining);
  }

  @Override
  public int getNotificationDelay () {
    return ConfigManager.NOTIFICATION_DELAY.get();
  }

  @Override
  public boolean isNotificationsEnabled () {
    return !ConfigManager.DISABLE_NOTIFICATIONS.get();
  }

  @Override
  public boolean isMessageStylesEnabled() {
    return !ConfigManager.DISABLE_MESSAGE_STYLES.get();
  }

  @Override
  public ClientTextureType getTextureType() {
    if (ConfigManager.isOldTextures()) {
      return ClientTextureType.OLD;
    } else if (ConfigManager.isVanillaTextures()) {
      return ClientTextureType.VANILLA;
    } else {
      return ClientTextureType.DEFAULT;
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
  public Set<ResourceKey<Level>> getDimensionWhitelist () {
    return ConfigManager.getDimensionWhitelist();
  }

  @Override
  public Set<ResourceKey<LootTable>> getLootTableBlacklist () {
    return ConfigManager.getLootBlacklist();
  }

  @Override
  public Set<String> getLootModidBlacklist () {
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
  public boolean isWorldBorderSafe(Level level, BlockPos pos) {
    if (!ConfigManager.CHECK_WORLD_BORDER.get()) {
      return true;
    }
    return level.getWorldBorder().isWithinBounds(pos);
  }

  @Override
  public boolean isWorldBorderSafe(Level level, ChunkPos pos) {
    if (!ConfigManager.CHECK_WORLD_BORDER.get()) {
      return true;
    }
    return level.getWorldBorder().isWithinBounds(pos);
  }

  @Override
  public boolean shouldCheckWorldBorder () {
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
  public boolean shouldDecayAll () {
    return ConfigManager.DECAY_ALL.get();
  }

  @Override
  public int getRefreshValue() {
    return ConfigManager.REFRESH_VALUE.get();
  }

  @Override
  public boolean shouldRefreshAll () {
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
  public boolean isFakePlayerBreakEnabled () {
    return ConfigManager.ENABLE_FAKE_PLAYER_BREAK.get();
  }

  @Override
  public boolean shouldPerformDecayWhileTicking () {
    return ConfigManager.PERFORM_DECAY_WHILE_TICKING.get();
  }

  @Override
  public boolean shouldPerformRefreshWhileTicking () {
    return ConfigManager.PERFORM_REFRESH_WHILE_TICKING.get();
  }

  @Override
  public boolean shouldStartDecayWhileTicking () {
    return ConfigManager.START_DECAY_WHILE_TICKING.get();
  }

  @Override
  public boolean shouldStartRefreshWhileTicking () {
    return ConfigManager.START_REFRESH_WHILE_TICKING.get();
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
  public int getDecayValue(ILootrInfoProvider provider) {
    return DataStorage.getDecayValue(provider);
  }

  @Override
  public boolean isDecayed(ILootrInfoProvider provider) {
    return DataStorage.isDecayed(provider);
  }

  @Override
  public void setDecaying(ILootrInfoProvider provider, int decay) {
    DataStorage.setDecaying(provider, decay);
  }

  @Override
  public void removeDecayed(ILootrInfoProvider provider) {
    DataStorage.removeDecayed(provider);
  }

  @Override
  public int getRefreshValue(ILootrInfoProvider provider) {
    return DataStorage.getRefreshValue(provider);
  }

  @Override
  public boolean isRefreshed(ILootrInfoProvider provider) {
    return DataStorage.isRefreshed(provider);
  }

  @Override
  public void setRefreshing(ILootrInfoProvider provider, int decay) {
    DataStorage.setRefreshing(provider, decay);
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
    return Component.translatable("lootr.message.invalid_table", lootTable.location().getNamespace(), lootTable.toString()).setStyle(ConfigManager.DISABLE_MESSAGE_STYLES.get() ? Style.EMPTY : Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_RED)).withBold(true));
  }

  @Override
  public boolean anyUnloadedChunks (ResourceKey<Level> dimension, Set<ChunkPos> chunks) {
    synchronized (HandleChunk.LOADED_CHUNKS) {
      Set<ChunkPos> syncedChunks = HandleChunk.LOADED_CHUNKS.get(dimension);
      if (syncedChunks == null) {
        return false;
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
