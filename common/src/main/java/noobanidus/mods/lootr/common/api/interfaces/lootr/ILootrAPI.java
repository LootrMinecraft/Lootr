package noobanidus.mods.lootr.common.api.interfaces.lootr;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentGetter;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import noobanidus.mods.lootr.common.api.config.BreakMode;
import noobanidus.mods.lootr.common.api.config.ResistanceMode;
import noobanidus.mods.lootr.common.api.config.SaveMode;
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
import noobanidus.mods.lootr.common.api.interfaces.processor.ILootrBlockEntityProcessor;
import noobanidus.mods.lootr.common.api.interfaces.processor.ILootrEntityProcessor;
import noobanidus.mods.lootr.common.api.interfaces.type.ILootrType;
import noobanidus.mods.lootr.common.client.ClientHooks;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@ApiStatus.Internal
public interface ILootrAPI {
  default Set<UUID> getPlayerIds() {
    MinecraftServer server = LootrAPI.getServer();
    if (server == null) {
      return Set.of();
    }

    Set<UUID> result = new HashSet<>();
    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
      if (LootrAPI.isFakePlayer(player)) {
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

  MinecraftServer getServer();

  boolean isFakePlayer(Player player);

  // Clears player loot for all containers
  default boolean clearPlayerLoot(ServerPlayer entity) {
    return clearPlayerLoot(entity.getUUID());
  }

  boolean clearPlayerLoot(UUID id);

  @Nullable
  default ILootrInventory getInventory(ILootrContainerInstance instance, ServerPlayer player, @Nullable IMenuBuilder builder) {
    return getInventory(instance, player, instance.getDefaultFiller(), builder);
  }

  // Get specified inventory using menubuilder
  @Nullable
  ILootrInventory getInventory(ILootrContainerInstance instance, ServerPlayer player, ILootFiller filler, @Nullable IMenuBuilder builder);

  // Get saved data for specific instance
  @Nullable
  ILootrInventoryStore getData(ILootrContainerInstance instance);

  default long getLootSeed(long seed) {
    if (shouldRandomizeLootSeed() || seed == -1 || seed == 0) {
      return ThreadLocalRandom.current().nextLong();
    }
    return seed;
  }

  boolean shouldRandomizeLootSeed ();

  // Determine if saving block entity data in a structure
  boolean shouldDiscard();

  default float getExplosionResistance(Block block, float defaultResistance) {
    return switch (LootrAPI.getBlastResistanceMode()) {
      case NONE -> defaultResistance;
      case IMMUNE -> Float.MAX_VALUE;
      case RESISTANT -> 16.0f;
    };
  }

  default float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos position, float defaultProgress) {
    if (LootrAPI.getBreakMode() == BreakMode.NEVER) {
      return 0f;
    }
    return defaultProgress;
  }

  default int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos, int defaultSignal, Direction direction) {
    if (LootrAPI.shouldPowerComparators()) {
      return 1;
    }
    return defaultSignal;
  }

  // Determine if comparators should be powered when containers are opened
  boolean shouldPowerComparators();

  // Determine if notifications should be made by checking the remaining time
  boolean shouldNotify(int remaining);

  int getNotificationDelay();

  boolean isNotificationsEnabled();

  boolean isMessageStylesEnabled();

  boolean isVanillaTextures();

  boolean isDisabled();

  boolean isLootTableBlacklisted(ResourceKey<LootTable> table);

  boolean isDimensionBlocked(ResourceKey<Level> dimension);

  boolean isDimensionDecaying(ResourceKey<Level> dimension);

  boolean isDimensionRefreshing(ResourceKey<Level> dimension);

  Set<ResourceKey<Level>> getDimensionBlacklist();

  Set<ResourceKey<Level>> getDimensionWhitelist();

  Set<ResourceKey<LootTable>> getLootTableBlacklist();

  Set<String> getLootModidBlacklist();

  Set<String> getModidDimensionWhitelist();

  Set<String> getModidDimensionBlacklist();

  boolean shouldBeginDecaying(ILootrContainerInstance instance);

  boolean shouldBeginRefreshing(ILootrContainerInstance instance);


  Set<String> getDecayModIds();

  Set<ResourceKey<LootTable>> getDecayLootTables();

  Set<ResourceKey<Level>> getDecayDimensions();

  Set<String> getRefreshLootTableModIds();

  Set<ResourceKey<LootTable>> getRefreshLootTables();

  Set<ResourceKey<Level>> getRefreshDimensions();

  boolean reportUnresolvedTables();

  boolean isCustomTrapped();

  default boolean isWorldBorderSafe(Level level, BlockPos pos) {
    if (!LootrAPI.shouldCheckWorldBorder()) {
      return true;
    }
    return level.getWorldBorder().isWithinBounds(pos);
  }

  default boolean isWorldBorderSafe(Level level, ChunkPos pos) {
    if (!LootrAPI.shouldCheckWorldBorder()) {
      return true;
    }
    return level.getWorldBorder().isWithinBounds(pos);
  }

  boolean shouldCheckWorldBorder();

  boolean shouldConvertElytrasToChests();

  boolean shouldConvertElytrasToItemFrames();

  boolean shouldConvertStructureItemFrames();

  int getDecayValue();

  boolean shouldDecayAll();

  int getRefreshValue();

  boolean shouldRefreshAll();

  default Style getInvalidStyle() {
    return LootrAPI.isMessageStylesEnabled() ? Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED))
        .withBold(true) : Style.EMPTY;
  }

  default Style getDecayStyle() {
    return LootrAPI.isMessageStylesEnabled() ? Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED))
        .withBold(true) : Style.EMPTY;
  }

  default Style getRefreshStyle() {
    return LootrAPI.isMessageStylesEnabled() ? Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE))
        .withBold(true) : Style.EMPTY;
  }

  default Style getChatStyle() {
    return LootrAPI.isMessageStylesEnabled() ? Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA)) : Style.EMPTY;
  }

  default Component getInvalidTableComponent(ResourceKey<LootTable> lootTable) {
    return Component.translatable("lootr.message.invalid_table", lootTable.identifier()
            .getNamespace(), lootTable.toString())
        .setStyle(LootrAPI.isMessageStylesEnabled() ? Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_RED))
            .withBold(true) : Style.EMPTY);
  }

  default boolean canDestroyOrBreak(Player player) {
    return (LootrAPI.isFakePlayer(player) && LootrAPI.isFakePlayerBreakEnabled()) || LootrAPI.getBreakMode() == BreakMode.ALWAYS;
  }

  boolean isFakePlayerBreakEnabled();

  boolean canBrushablesSelfSupport();

  boolean canItemFramesSelfSupport();

  boolean shouldDropPlayerLoot();

  boolean shouldPerformDecayWhileTicking();

  boolean shouldPerformRefreshWhileTicking();

  boolean shouldStartDecayWhileTicking();

  boolean shouldStartRefreshWhileTicking();

  boolean performPiecewiseCheck();

  @Nullable
  BlockState getConvertedBlockState(BlockState original);

  default void handleInstanceSneak(@Nullable ILootrContainerInstance instance, ServerPlayer player) {
    if (instance == null) {
      return;
    }
    if (!instance.canBeMarkedUnopened()) {
      return;
    }
    if (instance.removeVisualOpener(player)) {
      instance.performClose(player);
      instance.performUpdate(player);
    }
  }

  default void handleInstanceOpen(@Nullable ILootrContainerInstance instance, ServerPlayer player) {
    handleInstanceOpen(instance, player, null);
  }

  default void handleInstanceOpen(@Nullable ILootrContainerInstance instance, ServerPlayer player, @Nullable IMenuBuilder menuBuilder) {
    if (instance == null) {
      return;
    }
    if (player.isSpectator()) {
      player.openMenu(null);
      return;
    }
    if (instance.getDataLevel() == null || instance.getDataLevel().isClientSide()) {
      return;
    }

    if (!instance.canPlayerOpen(player)) {
      return;
    }

    var store = LootrAPI.getData(instance);
    if (store == null) {
      return;
    }

    var style = LootrAPI.getDecayStyle();

    if (instance.canDecay()) {
      if (store.isDecayed()) {
        instance.performDecay();
        player.sendOverlayMessage(Component.translatable("lootr.message.decayed")
            .setStyle(style));
        return;
      } else {
        int decayValue = store.remainingDecayTime();
        if (decayValue > 0 && LootrAPI.shouldNotify(decayValue)) {
          player.sendOverlayMessage(Component.translatable("lootr.message.decay_in", decayValue / 20)
              .setStyle(style));
        } else if (decayValue == -1) {
          if (LootrAPI.shouldBeginDecaying(instance)) {
            store.beginDecay();
            player.sendOverlayMessage(Component.translatable("lootr.message.decay_start", LootrAPI.getDecayValue() / 20)
                .setStyle(style));
          }
        }
      }
    }

    style = LootrAPI.getRefreshStyle();

    instance.performTrigger(player);
    boolean shouldUpdate = false;
    if (instance.canRefresh()) {
      if (store.isRefreshed()) {
        store.performRefresh();
        instance.performClose();
        player.sendOverlayMessage(Component.translatable("lootr.message.refreshed")
            .setStyle(style));
        shouldUpdate = true;
      }
      int refreshValue = store.remainingRefreshTime();
      if (refreshValue > 0 && LootrAPI.shouldNotify(refreshValue)) {
        player.sendOverlayMessage(Component.translatable("lootr.message.refresh_in", refreshValue / 20)
            .setStyle(style));
      } else if (refreshValue == -1) {
        if (LootrAPI.shouldBeginRefreshing(instance)) {
          store.beginRefresh();
          player.sendOverlayMessage(Component.translatable("lootr.message.refresh_start", LootrAPI.getRefreshValue() / 20)
              .setStyle(style));
        }
      }
    }


    MenuProvider menuProvider = LootrAPI.getInventory(instance, player, menuBuilder);
    if (menuProvider == null) {
      return;
    }
    // This is pretty important, should be moved out of here
    if (!instance.hasServerOpened(player)) {
      player.awardStat(LootrRegistry.getLootedStat());
      LootrRegistry.getStatTrigger().trigger(player);
    }
    if (instance.addOpener(player)) {
      instance.performOpen(player);
      shouldUpdate = true;
    }

    if (shouldUpdate) {
      instance.performUpdate(player);
    }
    player.openMenu(menuProvider);
    PiglinAi.angerNearbyPiglins(player.level(), player, true);
  }

  default void handleInstanceTick(@Nullable ILootrContainerInstance instance) {
    if (instance == null) {
      return;
    }

    if (instance.getDataLevel() == null || instance.getDataLevel().isClientSide()) {
      return;
    }

    var store = LootrAPI.getData(instance);
    if (store == null) {
      return;
    }

    if (instance.hasBeenOpened()) {
      if (instance.canDecay()) {
        if (LootrAPI.shouldPerformDecayWhileTicking() && store.isDecayed()) {
          instance.performDecay();
          return;
        } else if (LootrAPI.shouldStartDecayWhileTicking() && !store.isDecayed()) {
          int decayValue = store.remainingDecayTime();
          if (decayValue == -1) {
            if (LootrAPI.shouldBeginDecaying(instance)) {
              store.beginDecay();
            }
          }
        }
      }
      if (instance.canRefresh()) {
        if (LootrAPI.shouldPerformRefreshWhileTicking() && store.isRefreshed()) {
          store.performRefresh();
          instance.performClose();
          instance.performUpdate();
        }
        if (LootrAPI.shouldStartRefreshWhileTicking() && !store.isRefreshed()) {
          int refreshValue = store.remainingRefreshTime();
          if (refreshValue == -1) {
            if (LootrAPI.shouldBeginRefreshing(instance)) {
              store.beginRefresh();
            }
          }
        }
      }
    }
  }

  default void handleInstanceClientTick(@Nullable ILootrContainerInstance instance) {
    if (instance == null) {
      return;
    }

    if (instance.getDataLevel() == null || !instance.getDataLevel().isClientSide()) {
      return;
    }

    if (LootrAPI.shouldDisplayUnopenedParticles()) {
      var type = instance.getDataType();
      if (type.displaysUnopenedParticle()) {
        ClientHooks.performUnopenedParticles(instance);
      }
    }
  }

  @Nullable
  <T extends BlockEntity> ILootrBlockEntity wrapBlockEntity(T blockEntity);

  @Nullable
  <T extends Entity> ILootrEntity wrapEntity(T entity);

  default boolean isTaggedStructurePresent(ServerLevel level, ChunkPos chunkPos, TagKey<Structure> tag, BlockPos pos) {
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
        if (LootrAPI.DESERT_PYRAMID_ADDITIONAL.moved(center.getX(), center.getY(), center.getZ()).isInside(pos)) {
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

  default void playerDestroyed(Level level, Player player, BlockPos pos, @Nullable BlockEntity blockEntity) {
    if (!LootrAPI.shouldDropPlayerLoot() || (level.isClientSide() || blockEntity == null)) {
      return;
    }

    if (LootrAPI.wrapBlockEntity(blockEntity) instanceof ILootrContainerInstance instance && player instanceof ServerPlayer serverPlayer && instance.canDropContentsWhenBroken()) {
      ILootrInventory inventory = getInventory(instance, serverPlayer, instance.getDefaultFiller(), null);
      if (inventory != null) {
        Containers.dropContents(level, pos, inventory);
      }
    }
  }

  void refreshSections();

  List<ILootrFilter> getFilters();

  List<ILootrBlockEntityProcessor.Pre> getBlockEntityPreProcessors();

  List<ILootrBlockEntityProcessor.Post> getBlockEntityPostProcessors();

  List<ILootrEntityProcessor.Pre> getEntityPreProcessors();

  List<ILootrEntityProcessor.Post> getEntityPostProcessors();

  @Nullable
  <T> ILootrDataAccessor<T> getAccessor(T type);

  @Nullable
  <T> ILootrItemFrameAccessor<T> getItemFrameAccessor(T type);

  ILootrType getType(String type);

  boolean shouldBypassSpawnProtection();

  boolean shouldReplaceWhenDecayed();

  PotDecorationsAdapter getDecorationsAdapter(BlockEntity blockEntity);

  PotDecorationsAdapter getDecorationsAdapter(ItemStack stack);

  PotDecorationsAdapter getDecorationsAdapter(DataComponentGetter input);

  SaveMode getFileSaveMode();

  boolean shouldDisplayUnopenedParticles();

  default long getGameTime() {
    var server = LootrAPI.getServer();

    if (server == null) {
      return -1;
    }

    return server.getWorldData().overworldData().getGameTime();
  }

  void refreshServices();

  ResistanceMode getBlastResistanceMode();

  BreakMode getBreakMode();
}


