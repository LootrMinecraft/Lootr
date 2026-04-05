package noobanidus.mods.lootr.common.api;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.accessor.ILootrDataAccessor;
import noobanidus.mods.lootr.common.api.accessor.ILootrItemFrameAccessor;
import noobanidus.mods.lootr.common.api.config.client.ClientTextureType;
import noobanidus.mods.lootr.common.api.config.SaveMode;
import noobanidus.mods.lootr.common.api.data.ILootrContainerInstance;
import noobanidus.mods.lootr.common.api.data.ILootrInventoryStore;
import noobanidus.mods.lootr.common.api.filler.ILootFiller;
import noobanidus.mods.lootr.common.api.interfaces.ILootrAPI;
import noobanidus.mods.lootr.common.api.interfaces.IMenuBuilder;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;
import noobanidus.mods.lootr.common.api.inventory.ILootrInventory;
import noobanidus.mods.lootr.common.api.filter.ILootrFilter;
import noobanidus.mods.lootr.common.api.integration.PotDecorationsAdapter;
import noobanidus.mods.lootr.common.api.processor.ILootrBlockEntityProcessor;
import noobanidus.mods.lootr.common.api.processor.ILootrEntityProcessor;
import noobanidus.mods.lootr.common.api.type.ILootrType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * This is the standard access to the platform-specific implementations of ILootrAPI.
 * <br />
 * Beware that this is only initialized during mod creation. Accessing the API too early
 * will result in a NullPointerException.
 * <br />
 * All non-platform methods are implemented in DefaultLootrAPIImpl.
 */
public final class LootrAPI {
  public static final Logger LOG = LogUtils.getLogger();
  public static final String MODID = "lootr";
  public static final String NETWORK_VERSION = "lootr-1.21.0-1";
  public static final ResourceKey<LootTable> ELYTRA_CHEST = ResourceKey.create(Registries.LOOT_TABLE, LootrAPI.rl("chests/elytra"));
  public static final ResourceKey<LootTable> TROPHY_REWARD = ResourceKey.create(Registries.LOOT_TABLE, LootrAPI.rl("reward/trophy"));
  public static final ResourceKey<LootTable> ITEM_FRAME_EMPTY = ResourceKey.create(Registries.LOOT_TABLE, LootrAPI.rl("entity/item_frame_empty"));
  public static final ResourceKey<LootTable> EMPTY_CHEST = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("c", "empty"));
  public static final List<Identifier> PROBLEMATIC_CHESTS = Arrays.asList(LootrAPI.rl("twilightforest", "structures/stronghold_boss"), LootrAPI.rl("atum", "chests/pharaoh"));
  public static final List<String> _lootr$digits = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f");

  public static ILootrAPI INSTANCE = null;
  public static boolean shouldDiscardIdAndOpeners;

  public static boolean isReady() {
    return INSTANCE != null;
  }

  public static Identifier rl(String path) {
    return Identifier.fromNamespaceAndPath(MODID, path);
  }

  public static Identifier rl(String namespace, String path) {
    return Identifier.fromNamespaceAndPath(namespace, path);
  }

  public static Identifier mc(String path) {
    return Identifier.withDefaultNamespace(path);
  }

  public static Set<UUID> getPlayerIds() {
    return INSTANCE.getPlayerIds();
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

  public static int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos, int defaultSignal, Direction direction) {
    return INSTANCE.getAnalogOutputSignal(pBlockState, pLevel, pPos, defaultSignal, direction);
  }

  public static boolean shouldPowerComparators() {
    return INSTANCE.shouldPowerComparators();
  }

  public static ClientTextureType getTextureType() {
    return INSTANCE.getTextureType();
  }

  public static boolean isNewTextures() {
    return INSTANCE.isNewTextures();
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

  public static boolean isDecaying(ILootrContainerInstance provider) {
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

  public static boolean isRefreshing(ILootrContainerInstance provider) {
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

  public static boolean shouldConvertElytrasToChests() {
    return INSTANCE.shouldConvertElytrasToChests();
  }

  public static boolean shouldConvertElytrasToItemFrames() {
    return INSTANCE.shouldConvertElytrasToItemFrames();
  }

  public static boolean shouldConvertStructureItemFrames() {
    return INSTANCE.shouldConvertStructureItemFrames();
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

  public static boolean canBrushablesSelfSupport() {
    return INSTANCE.canBrushablesSelfSupport();
  }

  public static boolean canItemFramesSelfSupport() {
    return INSTANCE.canItemFramesSelfSupport();
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

  public static boolean shouldDropPlayerLoot() {
    return INSTANCE.shouldDropPlayerLoot();
  }

  public static boolean shouldPerformDecayWhileTicking() {
    return INSTANCE.shouldPerformDecayWhileTicking();
  }

  public static boolean shouldPerformRefreshWhileTicking() {
    return INSTANCE.shouldPerformRefreshWhileTicking();
  }

  public static boolean shouldStartDecayWhileTicking() {
    return INSTANCE.shouldStartDecayWhileTicking();
  }

  public static boolean shouldStartRefreshWhileTicking() {
    return INSTANCE.shouldStartRefreshWhileTicking();
  }

  public static boolean performPiecewiseCheck() {
    return INSTANCE.performPiecewiseCheck();
  }

  @Nullable
  public static BlockState replacementBlockState(BlockState original) {
    return INSTANCE.replacementBlockState(original);
  }

  @Nullable
  public static ILootrInventory getInventory(ILootrContainerInstance provider, ServerPlayer player, @Nullable IMenuBuilder builder) {
    return INSTANCE.getInventory(provider, player, builder);
  }

  @Nullable
  public static ILootrInventory getInventory(ILootrContainerInstance provider, ServerPlayer player) {
    return INSTANCE.getInventory(provider, player, (IMenuBuilder) null);
  }

  // This is only used when wanting to override the loot filler from the default
  @Nullable
  public static ILootrInventory getInventory(ILootrContainerInstance provider, ServerPlayer player, ILootFiller filler, @Nullable IMenuBuilder builder) {
    return INSTANCE.getInventory(provider, player, filler, builder);
  }

  @Nullable
  public static ILootrInventoryStore getData(ILootrContainerInstance provider) {
    return INSTANCE.getData(provider);
  }

  public static int getRemainingDecayValue(ILootrContainerInstance provider) {
    return INSTANCE.getRemainingDecayValue(provider);
  }

  public static boolean isDecayed(ILootrContainerInstance provider) {
    return INSTANCE.isDecayed(provider);
  }

  public static void setDecaying(ILootrContainerInstance provider) {
    INSTANCE.setDecaying(provider);
  }

  public static int getRemainingRefreshValue(ILootrContainerInstance provider) {
    return INSTANCE.getRemainingRefreshValue(provider);
  }

  public static boolean isRefreshed(ILootrContainerInstance provider) {
    return INSTANCE.isRefreshed(provider);
  }

  public static void setRefreshing(ILootrContainerInstance provider) {
    INSTANCE.setRefreshing(provider);
  }

  public static void removeRefreshed(ILootrContainerInstance provider) {
    INSTANCE.removeRefreshed(provider);
  }

  public static void handleProviderOpen(@Nullable ILootrContainerInstance provider, ServerPlayer player) {
    INSTANCE.handleProviderOpen(provider, player);
  }

  public static void handleProviderSneak(@Nullable ILootrContainerInstance provider, ServerPlayer player) {
    INSTANCE.handleProviderSneak(provider, player);
  }

  public static void handleProviderTick(@Nullable ILootrContainerInstance provider) {
    INSTANCE.handleProviderTick(provider);
  }

  public static void handleProviderClientTick (@Nullable ILootrContainerInstance provider) {
    INSTANCE.handleProviderClientTick(provider);
  }

  @Nullable
  public static <T extends BlockEntity> ILootrBlockEntity resolveBlockEntity(T blockEntity) {
    return INSTANCE.resolveBlockEntity(blockEntity);
  }

  public static <T extends Entity> ILootrEntity resolveEntity(T entity) {
    return INSTANCE.resolveEntity(entity);
  }

  public static boolean isTaggedStructurePresent(ServerLevel level, ChunkPos chunkPos, TagKey<Structure> tag, BlockPos pos) {
    return INSTANCE.isTaggedStructurePresent(level, chunkPos, tag, pos);
  }

  public static void playerDestroyed(Level level, Player player, BlockPos blockPos, @Nullable BlockEntity blockEntity) {
    INSTANCE.playerDestroyed(level, player, blockPos, blockEntity);
  }

  public static void refreshSections() {
    INSTANCE.refreshSections();
  }

  public static void refreshServices () {
    INSTANCE.refreshServices();
  }

  public static List<ILootrFilter> getFilters() {
    return INSTANCE.getFilters();
  }

  public static List<ILootrBlockEntityProcessor.Pre> getBlockEntityPreProcessors() {
    return INSTANCE.getBlockEntityPreProcessors();
  }

  public static List<ILootrBlockEntityProcessor.Post> getBlockEntityPostProcessors() {
    return INSTANCE.getBlockEntityPostProcessors();
  }

  public static List<ILootrEntityProcessor.Pre> getEntityPreProcessors() {
    return INSTANCE.getEntityPreProcessors();
  }

  public static List<ILootrEntityProcessor.Post> getEntityPostProcessors() {
    return INSTANCE.getEntityPostProcessors();
  }

  public static boolean shouldBypassSpawnProtection() {
    return INSTANCE.shouldBypassSpawnProtection();
  }

  public static boolean shouldReplaceWhenDecayed() {
    return INSTANCE.shouldReplaceWhenDecayed();
  }

  public static void postProcess(ServerLevel level, BlockPos position, BlockEntity newBlockEntity, BlockState newState, ResourceKey<LootTable> lootTable, long lootTableSeed) {
    for (ILootrBlockEntityProcessor.Post processor : getBlockEntityPostProcessors()) {
      processor.process(level, position, newBlockEntity, newState, lootTable, lootTableSeed);
    }
  }

  public static void preProcess(ServerLevel level, BlockPos position, BlockEntity oldBlockEntity, BlockState newState, ResourceKey<LootTable> lootTable, long lootTableSeed) {
    for (ILootrBlockEntityProcessor.Pre processor : getBlockEntityPreProcessors()) {
      processor.process(level, position, oldBlockEntity, newState, lootTable, lootTableSeed);
    }
  }

  public static void postProcess(ServerLevel level, Entity newBlockEntity, ResourceKey<LootTable> lootTable, long lootTableSeed) {
    BlockPos pos = newBlockEntity.blockPosition();
    for (ILootrEntityProcessor.Post processor : getEntityPostProcessors()) {
      processor.process(level, pos, newBlockEntity, null, lootTable, lootTableSeed);
    }
  }

  public static void preProcess(ServerLevel level, Entity newBlockEntity, ResourceKey<LootTable> lootTable, long lootTableSeed) {
    BlockPos pos = newBlockEntity.blockPosition();
    for (ILootrEntityProcessor.Pre processor : getEntityPreProcessors()) {
      processor.process(level, pos, newBlockEntity, null, lootTable, lootTableSeed);
    }
  }

  @Nullable
  public static <T> ILootrDataAccessor<T> getAdapter(T type) {
    return INSTANCE.getAdapter(type);
  }

  @Nullable
  public static <T> ILootrItemFrameAccessor<T> getItemFrameAdapter (T type) {
    return INSTANCE.getItemFrameAdapter(type);
  }

  @Nullable
  public static ILootrType getType(String type) {
    return INSTANCE.getType(type);
  }

  @Nullable
  public static PotDecorationsAdapter getDecorationsAdapter(BlockEntity blockEntity) {
    return INSTANCE.getDecorationsAdapter(blockEntity);
  }

  @Nullable
  public static PotDecorationsAdapter getDecorationsAdapter(ItemStack stack) {
    return INSTANCE.getDecorationsAdapter(stack);
  }

  @Nullable
  public static PotDecorationsAdapter getDecorationsAdapter(DataComponentGetter container) {
    return INSTANCE.getDecorationsAdapter(container);
  }

  public static SaveMode getFileSaveMode () {
    return INSTANCE.getFileSaveMode();
  }

  public static boolean shouldDisplayUnopenedParticles () {
    return INSTANCE.shouldDisplayUnopenedParticles();
  }

  public static long getGameTime () {
    return INSTANCE.getGameTime();
  }

  public static void closeContainers (BlockEntity blockEntity) {
    if (!(blockEntity instanceof Container container)) {
      return;
    }

    for (ContainerUser user : container.getEntitiesWithContainerOpen()) {
      if (user instanceof ServerPlayer player) {
        player.closeContainer();
      }
    }
  }
}
