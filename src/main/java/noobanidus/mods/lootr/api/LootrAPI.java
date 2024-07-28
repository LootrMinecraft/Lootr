package noobanidus.mods.lootr.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class LootrAPI {
  public static final Logger LOG = LogManager.getLogger();
  public static final String MODID = "lootr";
  public static final String NETWORK_VERSION = "lootr-1.21.0-1";
  public static final ResourceKey<LootTable> ELYTRA_CHEST = ResourceKey.create(Registries.LOOT_TABLE, LootrAPI.rl("chests/elytra"));

  public static ILootrAPI INSTANCE;
  public static boolean shouldDiscardIdAndOpeners;

  public static ResourceLocation rl(String path) {
    return ResourceLocation.fromNamespaceAndPath(MODID, path);
  }

  public static ResourceLocation rl(String namespace, String path) {
    return ResourceLocation.fromNamespaceAndPath(namespace, path);
  }

  public static Set<UUID> getPlayerIds() {
    return INSTANCE.getPlayerIds();
  }

  public static Player getPlayer() {
    return INSTANCE.getPlayer();
  }

  public static MinecraftServer getServer() {
    return INSTANCE.getServer();
  }

  public static int getCurrentTicks() {
    return INSTANCE.getCurrentTicks();
  }

  public static boolean isFakePlayer(Player player) {
    return INSTANCE.isFakePlayer(player);
  }

  public static boolean clearPlayerLoot(ServerPlayer entity) {
    return INSTANCE.clearPlayerLoot(entity);
  }

  public static boolean clearPlayerLoot(UUID id) {
    return INSTANCE.clearPlayerLoot(id);
  }

  public static long getLootSeed(long seed) {
    return INSTANCE.getLootSeed(seed);
  }

  public static boolean shouldDiscard() {
    return INSTANCE.shouldDiscard();
  }

  public static float getExplosionResistance(Block block, float defaultResistance) {
    return INSTANCE.getExplosionResistance(block, defaultResistance);
  }

  public static boolean isBlastResistant() {
    return INSTANCE.isBlastResistant();
  }

  public static boolean isBlastImmune() {
    return INSTANCE.isBlastImmune();
  }

  public static float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos position, float defaultProgress) {
    return INSTANCE.getDestroyProgress(state, player, level, position, defaultProgress);
  }

  public static int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos, int defaultSignal) {
    return INSTANCE.getAnalogOutputSignal(pBlockState, pLevel, pPos, defaultSignal);
  }

  public static boolean shouldPowerComparators() {
    return INSTANCE.shouldPowerComparators();
  }

  public static ClientTextureType getTextureType() {
    return INSTANCE.getTextureType();
  }

  public static boolean isOldTextures() {
    return INSTANCE.isOldTextures();
  }

  public static boolean isVanillaTextures() {
    return INSTANCE.isVanillaTextures();
  }

  public static boolean isDefaultTextures() {
    return INSTANCE.isDefaultTextures();
  }

  public static boolean shouldNotify(int remaining) {
    return INSTANCE.shouldNotify(remaining);
  }

  public static int getNotificationDelay() {
    return INSTANCE.getNotificationDelay();
  }

  public static boolean isNotificationsEnabled() {
    return INSTANCE.isNotificationsEnabled();
  }

  public static boolean isDisabled() {
    return INSTANCE.isDisabled();
  }

  public static boolean isLootTableBlacklisted(ResourceKey<LootTable> table) {
    return INSTANCE.isLootTableBlacklisted(table);
  }

  public static boolean isDimensionBlocked(ResourceKey<Level> dimension) {
    return INSTANCE.isDimensionBlocked(dimension);
  }

  public static Set<ResourceKey<Level>> getDimensionBlacklist() {
    return INSTANCE.getDimensionBlacklist();
  }

  public static Set<ResourceKey<Level>> getDimensionWhitelist() {
    return INSTANCE.getDimensionWhitelist();
  }

  public static Set<ResourceKey<LootTable>> getLootTableBlacklist() {
    return INSTANCE.getLootTableBlacklist();
  }

  public static Set<String> getLootModidBlacklist() {
    return INSTANCE.getLootModidBlacklist();
  }

  public static Set<String> getModidDimensionWhitelist() {
    return INSTANCE.getModidDimensionWhitelist();
  }

  public static Set<String> getModidDimensionBlacklist() {
    return INSTANCE.getModidDimensionBlacklist();
  }

  public static boolean isDecaying(ILootrInfoProvider provider) {
    return INSTANCE.isDecaying(provider);
  }


  public static Set<String> getModidDecayWhitelist() {
    return INSTANCE.getModidDecayWhitelist();
  }

  public static Set<ResourceKey<LootTable>> getDecayWhitelist() {
    return INSTANCE.getDecayWhitelist();
  }

  public static Set<ResourceKey<Level>> getDecayDimensions() {
    return INSTANCE.getDecayDimensions();
  }

  public static Set<String> getRefreshModids() {
    return INSTANCE.getRefreshModids();
  }

  public static Set<ResourceKey<LootTable>> getRefreshWhitelist() {
    return INSTANCE.getRefreshWhitelist();
  }

  public static Set<ResourceKey<Level>> getRefreshDimensions() {
    return INSTANCE.getRefreshDimensions();
  }

  public static boolean isRefreshing(ILootrInfoProvider provider) {
    return INSTANCE.isRefreshing(provider);
  }

  public static boolean reportUnresolvedTables() {
    return INSTANCE.reportUnresolvedTables();
  }

  public static boolean isCustomTrapped() {
    return INSTANCE.isCustomTrapped();
  }

  public static boolean isWorldBorderSafe(Level level, BlockPos pos) {
    return INSTANCE.isWorldBorderSafe(level, pos);
  }

  public static boolean isWorldBorderSafe(Level level, ChunkPos pos) {
    return INSTANCE.isWorldBorderSafe(level, pos);
  }

  public static boolean shouldCheckWorldBorder() {
    return INSTANCE.shouldCheckWorldBorder();
  }

  public static int getMaximumAge() {
    return INSTANCE.getMaximumAge();
  }

  public static boolean hasExpired(long time) {
    return INSTANCE.hasExpired(time);
  }

  public static boolean shouldConvertMineshafts() {
    return INSTANCE.shouldConvertMineshafts();
  }

  public static boolean shouldConvertElytras() {
    return INSTANCE.shouldConvertElytras();
  }

  public static int getDecayValue() {
    return INSTANCE.getDecayValue();
  }

  public static boolean shouldDecayAll() {
    return INSTANCE.shouldDecayAll();
  }

  public static int getRefreshValue() {
    return INSTANCE.getRefreshValue();
  }

  public static boolean shouldRefreshAll() {
    return INSTANCE.shouldRefreshAll();
  }

  public static boolean isMessageStylesEnabled() {
    return INSTANCE.isMessageStylesEnabled();
  }

  public static Style getInvalidStyle() {
    return INSTANCE.getInvalidStyle();
  }

  public static Style getDecayStyle() {
    return INSTANCE.getDecayStyle();
  }

  public static Style getRefreshStyle() {
    return INSTANCE.getRefreshStyle();
  }

  public static Style getChatStyle() {
    return INSTANCE.getChatStyle();
  }

  public static Component getInvalidTableComponent(ResourceKey<LootTable> lootTable) {
    return INSTANCE.getInvalidTableComponent(lootTable);
  }

  public static boolean canDestroyOrBreak(Player player) {
    return INSTANCE.canDestroyOrBreak(player);
  }

  public static boolean isBreakDisabled() {
    return INSTANCE.isBreakDisabled();
  }

  public static boolean isBreakEnabled() {
    return INSTANCE.isBreakEnabled();
  }

  public static boolean isFakePlayerBreakEnabled() {
    return INSTANCE.isFakePlayerBreakEnabled();
  }


  public static boolean shouldPerformDecayWhileTicking () {
    return INSTANCE.shouldPerformDecayWhileTicking();
  }
  public static boolean shouldPerformRefreshWhileTicking () {
    return INSTANCE.shouldPerformRefreshWhileTicking();
  }
  public static boolean shouldStartDecayWhileTicking () {
    return INSTANCE.shouldStartDecayWhileTicking();
  }
  public static boolean shouldStartRefreshWhileTicking () {
    return INSTANCE.shouldStartRefreshWhileTicking();
  }

  public static BlockState replacementBlockState(BlockState original) {
    return INSTANCE.replacementBlockState(original);
  }

  @Nullable
  public static ILootrInventory getInventory(ILootrInfoProvider provider, ServerPlayer player, LootFiller filler) {
    return INSTANCE.getInventory(provider, player, filler);
  }

  @Nullable
  public static ILootrInventory getInventory(ILootrInfoProvider provider, ServerPlayer player, LootFiller filler, MenuBuilder builder) {
    return INSTANCE.getInventory(provider, player, filler, builder);
  }

  @Nullable
  public static ILootrSavedData getData(ILootrInfoProvider provider) {
    return INSTANCE.getData(provider);
  }

  public static boolean isAwarded(ILootrInfoProvider provider, ServerPlayer player) {
    return INSTANCE.isAwarded(provider, player);
  }

  public static boolean isAwarded(UUID uuid, ServerPlayer player) {
    return INSTANCE.isAwarded(uuid, player);
  }

  public static void award(ILootrInfoProvider provider, ServerPlayer player) {
    INSTANCE.award(provider, player);
  }

  public static void award(UUID id, ServerPlayer player) {
    INSTANCE.award(id, player);
  }

  public static int getDecayValue(ILootrInfoProvider provider) {
    return INSTANCE.getDecayValue(provider);
  }

  public static boolean isDecayed(ILootrInfoProvider provider) {
    return INSTANCE.isDecayed(provider);
  }

  public static void setDecaying(ILootrInfoProvider provider, int decay) {
    INSTANCE.setDecaying(provider, decay);
  }

  public static void removeDecayed(ILootrInfoProvider provider) {
    INSTANCE.removeDecayed(provider);
  }

  public static int getRefreshValue(ILootrInfoProvider provider) {
    return INSTANCE.getRefreshValue(provider);
  }

  public static boolean isRefreshed(ILootrInfoProvider provider) {
    return INSTANCE.isRefreshed(provider);
  }

  public static void setRefreshing(ILootrInfoProvider provider, int decay) {
    INSTANCE.setRefreshing(provider, decay);
  }

  public static void removeRefreshed(ILootrInfoProvider provider) {
    INSTANCE.removeRefreshed(provider);
  }

  public static void handleProviderOpen(@Nullable ILootrInfoProvider provider, ServerPlayer player) {
    INSTANCE.handleProviderOpen(provider, player);
  }

  public static void handleProviderSneak(@Nullable ILootrInfoProvider provider, ServerPlayer player) {
    INSTANCE.handleProviderSneak(provider, player);
  }

  public static void handleProviderTick(@Nullable ILootrInfoProvider provider) {
    INSTANCE.handleProviderTick(provider);
  }

  public static boolean anyUnloadedChunks (ResourceKey<Level> dimension, Set<ChunkPos> chunks) {
    return INSTANCE.anyUnloadedChunks(dimension, chunks);
  }
}
