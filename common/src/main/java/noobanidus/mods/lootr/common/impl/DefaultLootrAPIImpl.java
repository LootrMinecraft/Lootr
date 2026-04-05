package noobanidus.mods.lootr.common.impl;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.ILootrAPI;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.api.accessor.ILootrDataAccessor;
import noobanidus.mods.lootr.common.api.accessor.ILootrItemFrameAccessor;
import noobanidus.mods.lootr.common.api.config.client.ClientTextureType;
import noobanidus.mods.lootr.common.api.config.BreakMode;
import noobanidus.mods.lootr.common.api.config.ResistanceMode;
import noobanidus.mods.lootr.common.api.config.SaveMode;
import noobanidus.mods.lootr.common.api.data.ILootrContainerInstance;
import noobanidus.mods.lootr.common.api.data.ILootrInventoryStore;
import noobanidus.mods.lootr.common.api.filler.ILootFiller;
import noobanidus.mods.lootr.common.api.interfaces.MenuBuilder;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;
import noobanidus.mods.lootr.common.api.inventory.ILootrInventory;
import noobanidus.mods.lootr.common.api.filter.ILootrFilter;
import noobanidus.mods.lootr.common.api.integration.PotDecorationsAdapter;
import noobanidus.mods.lootr.common.api.processor.ILootrBlockEntityProcessor;
import noobanidus.mods.lootr.common.api.processor.ILootrEntityProcessor;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.api.type.ILootrType;
import noobanidus.mods.lootr.common.client.ClientHooks;
import noobanidus.mods.lootr.common.api.config.ConfigManager;
import noobanidus.mods.lootr.common.api.config.LootrClientConfig;
import noobanidus.mods.lootr.common.api.config.LootrCommonConfig;
import noobanidus.mods.lootr.common.data.DataStorage;
import noobanidus.mods.lootr.common.integration.sherdsapi.SherdsIntegration;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public abstract class DefaultLootrAPIImpl implements ILootrAPI {
  @Override
  public final void handleProviderSneak(@Nullable ILootrContainerInstance provider, ServerPlayer player) {
    if (provider == null) {
      return;
    }
    if (!provider.canBeMarkedUnopened()) {
      return;
    }
    if (provider.removeVisualOpener(player)) {
      provider.performClose(player);
      provider.performUpdate(player);
    }
  }

  @Override
  public final void handleProviderOpen(@Nullable ILootrContainerInstance provider, ServerPlayer player) {
    handleProviderOpen(provider, player, null);
  }

  @Override
  public final void handleProviderOpen(@Nullable ILootrContainerInstance provider, ServerPlayer player, @Nullable MenuBuilder menuBuilder) {
    if (provider == null) {
      return;
    }
    if (player.isSpectator()) {
      player.openMenu(null);
      return;
    }
    if (provider.getDataLevel() == null || provider.getDataLevel().isClientSide()) {
      return;
    }

    if (!provider.canPlayerOpen(player)) {
      return;
    }
    if (LootrAPI.isDecayed(provider) && provider.canDecay()) {
      provider.performDecay();
      player.sendOverlayMessage(Component.translatable("lootr.message.decayed")
          .setStyle(LootrAPI.getDecayStyle()));
      return;
    } else {
      if (provider.canDecay()) {
        int decayValue = LootrAPI.getRemainingDecayValue(provider);
        if (decayValue > 0 && LootrAPI.shouldNotify(decayValue)) {
          player.sendOverlayMessage(Component.translatable("lootr.message.decay_in", decayValue / 20)
              .setStyle(LootrAPI.getDecayStyle()));
        } else if (decayValue == -1) {
          if (LootrAPI.isDecaying(provider)) {
            LootrAPI.setDecaying(provider);
            player.sendOverlayMessage(Component.translatable("lootr.message.decay_start", LootrAPI.getDecayValue() / 20)
                .setStyle(LootrAPI.getDecayStyle()));
          }
        }
      }
    }
    provider.performTrigger(player);
    boolean shouldUpdate = false;
    if (LootrAPI.isRefreshed(provider) && provider.canRefresh()) {
      provider.performRefresh();
      provider.performClose();
      player.sendOverlayMessage(Component.translatable("lootr.message.refreshed")
          .setStyle(LootrAPI.getRefreshStyle()));
      shouldUpdate = true;
    }
    if (provider.canRefresh()) {
      int refreshValue = LootrAPI.getRemainingRefreshValue(provider);
      if (refreshValue > 0 && LootrAPI.shouldNotify(refreshValue)) {
        player.sendOverlayMessage(Component.translatable("lootr.message.refresh_in", refreshValue / 20)
            .setStyle(LootrAPI.getRefreshStyle()));
      } else if (refreshValue == -1) {
        if (LootrAPI.isRefreshing(provider)) {
          LootrAPI.setRefreshing(provider);
          player.sendOverlayMessage(Component.translatable("lootr.message.refresh_start", LootrAPI.getRefreshValue() / 20)
              .setStyle(LootrAPI.getRefreshStyle()));
        }
      }
    }
    MenuProvider menuProvider = LootrAPI.getInventory(provider, player, menuBuilder);
    if (menuProvider == null) {
      return;
    }
    // This is pretty important, should be moved out of here
    if (!provider.hasServerOpened(player)) {
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
    PiglinAi.angerNearbyPiglins(player.level(), player, true);
  }

  @Override
  public final void handleProviderTick(@Nullable ILootrContainerInstance provider) {
    if (provider == null) {
      return;
    }

    if (provider.getDataLevel() == null || provider.getDataLevel().isClientSide()) {
      return;
    }

    if (LootrAPI.shouldPerformDecayWhileTicking() && LootrAPI.isDecayed(provider) && provider.hasBeenOpened() && provider.canDecay()) {
      provider.performDecay();
      return;
    } else if (LootrAPI.shouldStartDecayWhileTicking() && !LootrAPI.isDecayed(provider) && provider.hasBeenOpened() && provider.canDecay()) {
      int decayValue = LootrAPI.getRemainingDecayValue(provider);
      if (decayValue == -1) {
        if (LootrAPI.isDecaying(provider)) {
          LootrAPI.setDecaying(provider);
        }
      }
    }
    if (LootrAPI.shouldPerformRefreshWhileTicking() && LootrAPI.isRefreshed(provider) && provider.hasBeenOpened() && provider.canRefresh()) {
      provider.performRefresh();
      provider.performClose();
      provider.performUpdate();
    }
    if (LootrAPI.shouldStartRefreshWhileTicking() && !LootrAPI.isRefreshed(provider) && provider.hasBeenOpened() && provider.canRefresh()) {
      int refreshValue = LootrAPI.getRemainingRefreshValue(provider);
      if (refreshValue == -1) {
        if (LootrAPI.isRefreshing(provider)) {
          LootrAPI.setRefreshing(provider);
        }
      }
    }
  }

  @Override
  public final void handleProviderClientTick(@Nullable ILootrContainerInstance provider) {
    if (provider == null) {
      return;
    }

    if (provider.getDataLevel() == null || !provider.getDataLevel().isClientSide()) {
      return;
    }

    if (LootrAPI.shouldDisplayUnopenedParticles()) {
      var type = provider.getDataType();
      if (type.displaysUnopenedParticle()) {
        ClientHooks.performUnopenedParticles(provider);
      }
    }
  }

  @Override
  public final Set<UUID> getPlayerIds() {
    MinecraftServer server = getServer();
    if (server == null) {
      return Set.of();
    }

    Set<UUID> result = new HashSet<>();
    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
      if (isFakePlayer(player)) {
        continue;
      }
      // It can be null for some fake players?
      UUID thisUuid = player.getUUID();
      //noinspection ConstantValue
      if (thisUuid != null) {
        result.add(thisUuid);
      }
    }
    return result;
  }

  @Override
  public final boolean clearPlayerLoot(UUID id) {
    return DataStorage.clearInventories(id);
  }

  @Override
  public final ILootrInventory getInventory(ILootrContainerInstance provider, ServerPlayer player, ILootFiller filler, @Nullable MenuBuilder menuBuilder) {
    ILootrInventory inventory = DataStorage.getInventory(provider, player, filler);
    if (inventory != null && menuBuilder != null) {
      inventory.setMenuBuilder(menuBuilder);
    }
    return inventory;
  }

  @Override
  public final @Nullable ILootrInventoryStore getData(ILootrContainerInstance provider) {
    return DataStorage.getData(provider);
  }

  @Override
  public final boolean shouldDiscard() {
    return LootrAPI.shouldDiscardIdAndOpeners;
  }

  @Override
  public final int getRemainingDecayValue(ILootrContainerInstance provider) {
    return DataStorage.getDecayValue(provider);
  }

  @Override
  public final boolean isDecayed(ILootrContainerInstance provider) {
    return DataStorage.isDecayed(provider);
  }

  @Override
  public final void setDecaying(ILootrContainerInstance provider) {
    DataStorage.setDecaying(provider);
  }

  @Override
  public final int getRemainingRefreshValue(ILootrContainerInstance provider) {
    return DataStorage.getRefreshValue(provider);
  }

  @Override
  public final boolean isRefreshed(ILootrContainerInstance provider) {
    return DataStorage.isRefreshed(provider);
  }

  @Override
  public final void setRefreshing(ILootrContainerInstance provider) {
    DataStorage.setRefreshing(provider);
  }

  @Override
  public final void removeRefreshed(ILootrContainerInstance provider) {
    DataStorage.removeRefreshed(provider);
  }

  @Override
  @Nullable
  public final <T extends BlockEntity> ILootrBlockEntity resolveBlockEntity(T blockEntity) {
    return LootrServiceRegistry.wrapBlockEntity(blockEntity);
  }

  @Override
  public final <T extends Entity> ILootrEntity resolveEntity(T entity) {
    return LootrServiceRegistry.wrapEntity(entity);
  }

  private static final BoundingBox DESERT_PYRAMID_ADDITIONAL = new BoundingBox(-5, -30, -5, 5, 4, 4);

  @Override
  public boolean isTaggedStructurePresent(ServerLevel level, ChunkPos chunkPos, TagKey<Structure> tag, BlockPos pos) {
    Registry<Structure> registry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);
    List<StructureStart> starts = level.structureManager()
        .startsForStructure(chunkPos, o -> registry.get(registry.getId(o)).map(b -> b.is(tag)).orElse(false));
    for (StructureStart start : starts) {
      BoundingBox extended = start.getBoundingBox().inflatedBy(8);
      if (extended.isInside(pos)) {
        return true;
      }
      if (start.getStructure().type().equals(StructureType.DESERT_PYRAMID)) {
        // Compensate for the fact that desert pyramid pits aren't within the bounding box
        BlockPos center = start.getBoundingBox().getCenter();
        if (DESERT_PYRAMID_ADDITIONAL.moved(center.getX(), center.getY(), center.getZ()).isInside(pos)) {
          return true;
        }
      }
    }
    if (LootrAPI.performPiecewiseCheck()) {
      for (StructureStart start : starts) {
        for (StructurePiece piece : start.getPieces()) {
          if (piece.getBoundingBox().inflatedBy(8).isInside(pos)) {
            return true;
          }
        }
      }
    }

    return false;
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
  public void playerDestroyed(Level level, Player player, BlockPos pos, @Nullable BlockEntity blockEntity) {
    if (!shouldDropPlayerLoot() || (level.isClientSide() || blockEntity == null)) {
      return;
    }

    if (LootrAPI.resolveBlockEntity(blockEntity) instanceof ILootrContainerInstance provider && player instanceof ServerPlayer serverPlayer && provider.canDropContentsWhenBroken()) {
      ILootrInventory inventory = getInventory(provider, serverPlayer, provider.getDefaultFiller(), null);
      if (inventory != null) {
        Containers.dropContents(level, pos, inventory);
      }
    }
  }

  @Override
  public void refreshSections() {
    MinecraftServer server = getServer();
    if (server == null || server.isSingleplayer() || !server.isDedicatedServer()) {
      ClientHooks.refreshSection();
    }
  }

  @Override
  public void refreshServices() {
    LootrServiceRegistry.clearBlockConverters();
  }

  @Override
  @Nullable
  public BlockState replacementBlockState(BlockState original) {
    return LootrServiceRegistry.getConvertedBlockState(original);
  }

  @Override
  public List<ILootrFilter> getFilters() {
    return LootrServiceRegistry.getFilters();
  }

  @Override
  public List<ILootrBlockEntityProcessor.Pre> getBlockEntityPreProcessors() {
    return LootrServiceRegistry.getBlockEntityPreProcessors();
  }

  @Override
  public List<ILootrBlockEntityProcessor.Post> getBlockEntityPostProcessors() {
    return LootrServiceRegistry.getBlockEntityPostProcessors();
  }

  @Override
  public List<ILootrEntityProcessor.Pre> getEntityPreProcessors() {
    return LootrServiceRegistry.getEntityPreProcessors();
  }

  @Override
  public List<ILootrEntityProcessor.Post> getEntityPostProcessors() {
    return LootrServiceRegistry.getEntityPostProcessors();
  }

  @Nullable
  @Override
  public <T> ILootrDataAccessor<T> getAdapter(T type) {
    return LootrServiceRegistry.getDataAccessor(type);
  }

  @Nullable
  @Override
  public <T> ILootrItemFrameAccessor<T> getItemFrameAdapter(T type) {
    return LootrServiceRegistry.getItemFrameDataAccessor(type);
  }

  @Override
  public ILootrType getType(String type) {
    return LootrServiceRegistry.getType(type);
  }

  private boolean sherdsChecked = false;
  private DataComponentType<?> sherdsType = null;

  @Nullable
  private DataComponentType<?> getSherdsComponent() {
    if (!sherdsChecked) {
      sherdsChecked = true;
      sherdsType = BuiltInRegistries.DATA_COMPONENT_TYPE.get(LootrConstants.SHERDSAPI_POT_DECORATIONS)
          .map(Holder::value).orElse(null);
    }
    return sherdsType;
  }

  @Override
  @Nullable
  public PotDecorationsAdapter getDecorationsAdapter(ItemStack stack) {
    DataComponentType<?> sherdsType = getSherdsComponent();
    if (sherdsType != null && stack.has(sherdsType)) {
      return SherdsIntegration.getAdapterFrom(stack);
    }
    var output = stack.get(DataComponents.POT_DECORATIONS);
    if (output == null) {
      return null;
    }
    return new PotDecorationsAdapter(output);
  }

  @Override
  @Nullable
  public PotDecorationsAdapter getDecorationsAdapter(DataComponentGetter stack) {
    DataComponentType<?> sherdsType = getSherdsComponent();
    if (sherdsType != null) {
      var comp = stack.get(sherdsType);
      if (comp != null) {
        return SherdsIntegration.getAdapterFrom(stack);
      }
    }
    var output = stack.get(DataComponents.POT_DECORATIONS);
    if (output == null) {
      return null;
    }
    return new PotDecorationsAdapter(output);
  }

  @Override
  @Nullable
  public PotDecorationsAdapter getDecorationsAdapter(BlockEntity blockEntity) {
    if (!(blockEntity instanceof DecoratedPotBlockEntity decoratedPotBlockEntity)) {
      return null;
    }

    DataComponentType<?> sherdsType = getSherdsComponent();
    if (sherdsType != null) {
      return SherdsIntegration.getAdapterFrom(blockEntity);
    }

    if (decoratedPotBlockEntity.getDecorations() != PotDecorations.EMPTY) {
      return new PotDecorationsAdapter(decoratedPotBlockEntity.getDecorations());
    }

    return null;
  }

  @Override
  public long getLootSeed(long seed) {
    if (LootrCommonConfig.Conversion.randomiseSeed || seed == -1 || seed == 0) {
      return ThreadLocalRandom.current().nextLong();
    }
    return seed;
  }

  @Override
  public float getExplosionResistance(Block block, float defaultResistance) {
    return switch (LootrCommonConfig.Breaking.blastResistance) {
      case NONE -> defaultResistance;
      case IMMUNE -> Float.MAX_VALUE;
      case RESISTANT -> 16.0f;
    };
  }

  @Override
  public boolean isBlastResistant() {
    return LootrCommonConfig.Breaking.blastResistance == ResistanceMode.RESISTANT;
  }

  @Override
  public boolean isBlastImmune() {
    return LootrCommonConfig.Breaking.blastResistance == ResistanceMode.IMMUNE;
  }

  @Override
  public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos position, float defaultProgress) {
    if (LootrCommonConfig.Breaking.breakMode == BreakMode.NEVER) {
      return 0f;
    }
    return defaultProgress;
  }

  @Override
  public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos, int defaultSignal, Direction direction) {
    if (shouldPowerComparators()) {
      return 1;
    }
    return defaultSignal;
  }

  @Override
  public boolean shouldPowerComparators() {
    return LootrCommonConfig.Redstone.powerComparators;
  }

  @Override
  public boolean shouldNotify(int remaining) {
    return ConfigManager.shouldNotify(remaining);
  }

  @Override
  public int getNotificationDelay() {
    return LootrCommonConfig.Notifications.maximumNotificationDelay;
  }

  @Override
  public boolean isNotificationsEnabled() {
    return !LootrCommonConfig.Notifications.disableNotifications;
  }

  @Override
  public boolean isMessageStylesEnabled() {
    return !LootrCommonConfig.Notifications.disableMessageStyles;
  }

  @Override
  public ClientTextureType getTextureType() {
    if (ConfigManager.isVanillaTextures()) {
      return ClientTextureType.VANILLA;
    } else {
      return ClientTextureType.NEW;
    }
  }

  @Override
  public boolean isDisabled() {
    return LootrCommonConfig.Conversion.disable;
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
  public boolean isDecaying(ILootrContainerInstance provider) {
    return ConfigManager.isDecaying(provider);
  }

  @Override
  public boolean isRefreshing(ILootrContainerInstance provider) {
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
    return LootrCommonConfig.Notifications.reportUnresolvedTables;
  }

  @Override
  public boolean isCustomTrapped() {
    return LootrCommonConfig.Redstone.customTrapped;
  }

  @Override
  public boolean shouldCheckWorldBorder() {
    return LootrCommonConfig.Conversion.checkWorldBorder;
  }

  @Override
  public boolean shouldConvertElytrasToChests() {
    return LootrCommonConfig.Conversion.convertElytrasToChests;
  }

  @Override
  public boolean shouldConvertElytrasToItemFrames() {
    return LootrCommonConfig.Conversion.convertElytrasToItemFrames;
  }

  @Override
  public boolean shouldConvertStructureItemFrames() {
    return LootrCommonConfig.Conversion.convertStructureItemFrames;
  }

  @Override
  public int getDecayValue() {
    return LootrCommonConfig.Decay.decayValue;
  }

  @Override
  public boolean shouldDecayAll() {
    return LootrCommonConfig.Decay.decayAll;
  }

  @Override
  public int getRefreshValue() {
    return LootrCommonConfig.Refresh.refreshValue;
  }

  @Override
  public boolean shouldRefreshAll() {
    return LootrCommonConfig.Refresh.refreshAll;
  }

  @Override
  public Style getInvalidStyle() {
    return isMessageStylesEnabled() ? Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED))
        .withBold(true) : Style.EMPTY;
  }

  @Override
  public Style getDecayStyle() {
    return isMessageStylesEnabled() ? Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED))
        .withBold(true) : Style.EMPTY;
  }

  @Override
  public Style getRefreshStyle() {
    return isMessageStylesEnabled() ? Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE))
        .withBold(true) : Style.EMPTY;
  }

  @Override
  public Style getChatStyle() {
    return isMessageStylesEnabled() ? Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA)) : Style.EMPTY;
  }

  @Override
  public boolean canDestroyOrBreak(Player player) {
    return (isFakePlayer(player) && isFakePlayerBreakEnabled()) || LootrCommonConfig.Breaking.breakMode == BreakMode.ALWAYS;
  }

  @Override
  public boolean isBreakDisabled() {
    return LootrCommonConfig.Breaking.breakMode == BreakMode.NEVER;
  }

  @Override
  public boolean isBreakEnabled() {
    return LootrCommonConfig.Breaking.breakMode == BreakMode.ALWAYS;
  }

  @Override
  public boolean isFakePlayerBreakEnabled() {
    return LootrCommonConfig.Breaking.enableFakePlayerBreak;
  }

  @Override
  public boolean canBrushablesSelfSupport() {
    return LootrCommonConfig.Breaking.brushablesSelfSupport;
  }

  @Override
  public boolean canItemFramesSelfSupport() {
    return LootrCommonConfig.Breaking.itemFramesSelfSupport;
  }

  @Override
  public boolean shouldDropPlayerLoot() {
    return LootrCommonConfig.Breaking.shouldDropPlayerLoot;
  }

  @Override
  public boolean shouldPerformDecayWhileTicking() {
    return LootrCommonConfig.Decay.performDecayWhileTicking;
  }

  @Override
  public boolean shouldPerformRefreshWhileTicking() {
    return LootrCommonConfig.Refresh.performRefreshWhileTicking;
  }

  @Override
  public boolean shouldStartDecayWhileTicking() {
    return LootrCommonConfig.Decay.startDecayWhileTicking;
  }

  @Override
  public boolean shouldStartRefreshWhileTicking() {
    return LootrCommonConfig.Refresh.startRefreshWhileTicking;
  }

  @Override
  public boolean performPiecewiseCheck() {
    return ConfigManager.shouldPerformPiecewiseCheck();
  }

  @Override
  public boolean shouldBypassSpawnProtection() {
    return LootrCommonConfig.Interaction.bypassSpawnProtection;
  }

  @Override
  public boolean shouldReplaceWhenDecayed() {
    return LootrCommonConfig.Decay.replaceWhenDecayed;
  }

  @Override
  public SaveMode getFileSaveMode() {
    return LootrCommonConfig.Conversion.saveMode;
  }

  @Override
  public boolean shouldDisplayUnopenedParticles() {
    return LootrClientConfig.Particles.showUnopenedParticles;
  }

  @Override
  public Component getInvalidTableComponent(ResourceKey<LootTable> lootTable) {
    return Component.translatable("lootr.message.invalid_table", lootTable.identifier()
            .getNamespace(), lootTable.toString())
        .setStyle(isMessageStylesEnabled() ? Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_RED))
            .withBold(true) : Style.EMPTY);
  }
}
