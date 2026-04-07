package noobanidus.mods.lootr.common.impl;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.api.config.*;
import noobanidus.mods.lootr.common.api.data.ILootrContainerInstance;
import noobanidus.mods.lootr.common.api.data.ILootrInventoryStore;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;
import noobanidus.mods.lootr.common.api.filler.ILootFiller;
import noobanidus.mods.lootr.common.api.integration.decorated.PotDecorationsAdapter;
import noobanidus.mods.lootr.common.api.interfaces.accessor.ILootrDataAccessor;
import noobanidus.mods.lootr.common.api.interfaces.accessor.ILootrItemFrameAccessor;
import noobanidus.mods.lootr.common.api.interfaces.container.IMenuBuilder;
import noobanidus.mods.lootr.common.api.interfaces.filter.ILootrFilter;
import noobanidus.mods.lootr.common.api.interfaces.inventory.ILootrInventory;
import noobanidus.mods.lootr.common.api.interfaces.lootr.ILootrAPI;
import noobanidus.mods.lootr.common.api.interfaces.processor.ILootrBlockEntityProcessor;
import noobanidus.mods.lootr.common.api.interfaces.processor.ILootrEntityProcessor;
import noobanidus.mods.lootr.common.api.interfaces.type.ILootrType;
import noobanidus.mods.lootr.common.client.ClientHooks;
import noobanidus.mods.lootr.common.data.DataStorage;
import noobanidus.mods.lootr.common.integration.sherdsapi.SherdsIntegration;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class DefaultLootrAPIImpl implements ILootrAPI {
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
  public BlockState getConvertedBlockState(BlockState original) {
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
  public <T> ILootrDataAccessor<T> getAccessor(T type) {
    return LootrServiceRegistry.getDataAccessor(type);
  }

  @Nullable
  @Override
  public <T> ILootrItemFrameAccessor<T> getItemFrameAccessor(T type) {
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
  public boolean shouldRandomizeLootSeed() {
    return LootrCommonConfig.Conversion.randomiseSeed;
  }

  @Override
  public ResistanceMode getBlastResistanceMode() {
    return LootrCommonConfig.Breaking.blastResistance;
  }

  @Override
  public BreakMode getBreakMode() {
    return LootrCommonConfig.Breaking.breakMode;
  }

  @Override
  public boolean shouldPowerComparators() {
    return LootrCommonConfig.Redstone.powerComparators;
  }

  @Override
  public boolean shouldNotify(int remaining) {
    return LootrConfig.shouldNotify(remaining);
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
  public boolean isVanillaTextures() {
    return LootrClientConfig.Textures.useVanillaTextures;
  }

  @Override
  public boolean isDisabled() {
    return LootrCommonConfig.Conversion.disable;
  }

  @Override
  public boolean isLootTableBlacklisted(ResourceKey<LootTable> table) {
    return LootrConfig.isBlacklisted(table);
  }

  @Override
  public boolean isDimensionBlocked(ResourceKey<Level> dimension) {
    return LootrConfig.isDimensionBlocked(dimension);
  }

  @Override
  public boolean isDimensionDecaying(ResourceKey<Level> dimension) {
    return LootrConfig.isDimensionDecaying(dimension);
  }

  @Override
  public boolean isDimensionRefreshing(ResourceKey<Level> dimension) {
    return LootrConfig.isDimensionRefreshing(dimension);
  }

  @Override
  public Set<ResourceKey<Level>> getDimensionBlacklist() {
    return LootrConfig.getDimensionBlacklist();
  }

  @Override
  public Set<ResourceKey<Level>> getDimensionWhitelist() {
    return LootrConfig.getDimensionWhitelist();
  }

  @Override
  public Set<ResourceKey<LootTable>> getLootTableBlacklist() {
    return LootrConfig.getLootTableBlacklist();
  }

  @Override
  public Set<String> getLootModidBlacklist() {
    return LootrConfig.getLootModIdsBlacklist();
  }

  @Override
  public Set<String> getModidDimensionWhitelist() {
    return LootrConfig.getDimensionModIdWhitelist();
  }

  @Override
  public Set<String> getModidDimensionBlacklist() {
    return LootrConfig.getDimensionModIdBlacklist();
  }

  @Override
  public boolean shouldBeginDecaying(ILootrContainerInstance instance) {
    return LootrConfig.isDecaying(instance);
  }

  @Override
  public boolean shouldBeginRefreshing(ILootrContainerInstance instance) {
    return LootrConfig.isRefreshing(instance);
  }

  @Override
  public Set<String> getDecayModIds() {
    return LootrConfig.getDecayMods();
  }

  @Override
  public Set<ResourceKey<LootTable>> getDecayLootTables() {
    return LootrConfig.getDecayingTables();
  }

  @Override
  public Set<ResourceKey<Level>> getDecayDimensions() {
    return LootrConfig.getDecayDimensions();
  }

  @Override
  public Set<String> getRefreshLootTableModIds() {
    return LootrConfig.getRefreshLootTableModIds();
  }

  @Override
  public Set<ResourceKey<LootTable>> getRefreshLootTables() {
    return LootrConfig.getRefreshingTables();
  }

  @Override
  public Set<ResourceKey<Level>> getRefreshDimensions() {
    return LootrConfig.getRefreshDimensions();
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
    return LootrCommonConfig.Conversion.performPiecewiseCheck;
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
  public final boolean clearPlayerLoot(UUID id) {
    return DataStorage.clearInventories(id);
  }

  @Override
  public final ILootrInventory getInventory(ILootrContainerInstance instance, ServerPlayer player, ILootFiller filler, @Nullable IMenuBuilder menuBuilder) {
    ILootrInventory inventory = DataStorage.getInventory(instance, player, filler);
    if (inventory != null && menuBuilder != null) {
      inventory.setMenuBuilder(menuBuilder);
    }
    return inventory;
  }

  @Override
  public final @Nullable ILootrInventoryStore getData(ILootrContainerInstance instance) {
    return DataStorage.getData(instance);
  }

  @Override
  public final boolean shouldDiscard() {
    return LootrAPI.shouldDiscardIdAndOpeners;
  }

  @Override
  @Nullable
  public final <T extends BlockEntity> ILootrBlockEntity wrapBlockEntity(T blockEntity) {
    return LootrServiceRegistry.wrapBlockEntity(blockEntity);
  }

  @Override
  public final <T extends Entity> ILootrEntity wrapEntity(T entity) {
    return LootrServiceRegistry.wrapEntity(entity);
  }
}
