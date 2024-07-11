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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

  public static Player getPlayer () {
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

  public static float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos position, float defaultProgress) {
    return INSTANCE.getDestroyProgress(state, player, level, position, defaultProgress);
  }

  public static int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos, int defaultSignal) {
    return INSTANCE.getAnalogOutputSignal(pBlockState, pLevel, pPos, defaultSignal);
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

  public static boolean isDisabled() {
    return INSTANCE.isDisabled();
  }

  public static boolean isLootTableBlacklisted(ResourceKey<LootTable> table) {
    return INSTANCE.isLootTableBlacklisted(table);
  }

  public static boolean isDimensionBlocked(ResourceKey<Level> dimension) {
    return INSTANCE.isDimensionBlocked(dimension);
  }

  public static boolean isDecaying(ILootrInfoProvider provider) {
    return INSTANCE.isDecaying(provider);
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

  public static int getRefreshValue() {
    return INSTANCE.getRefreshValue();
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

  public static BlockState replacementBlockState(BlockState original) {
    return INSTANCE.replacementBlockState(original);
  }

  // TODO: Consider if this is really needed
  public static boolean hasCapacity(String capacity) {
    return INSTANCE.hasCapacity(capacity);
  }
}
