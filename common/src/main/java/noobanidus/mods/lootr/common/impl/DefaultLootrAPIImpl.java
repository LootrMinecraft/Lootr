package noobanidus.mods.lootr.common.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.*;
import noobanidus.mods.lootr.common.api.*;
import noobanidus.mods.lootr.common.api.adapter.ILootrDataAdapter;
import noobanidus.mods.lootr.common.api.adapter.ILootrItemFrameAdapter;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.common.api.data.ILootrSavedData;
import noobanidus.mods.lootr.common.api.data.LootFiller;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;
import noobanidus.mods.lootr.common.api.data.inventory.ILootrInventory;
import noobanidus.mods.lootr.common.api.filter.ILootrFilter;
import noobanidus.mods.lootr.common.api.processor.ILootrBlockEntityProcessor;
import noobanidus.mods.lootr.common.api.processor.ILootrEntityProcessor;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.client.ClientHooks;
import noobanidus.mods.lootr.common.data.DataStorage;
import noobanidus.mods.lootr.common.integration.sherdsapi.SherdsIntegration;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class DefaultLootrAPIImpl implements ILootrAPI {
  @Override
  public final void handleProviderSneak(@Nullable ILootrInfoProvider provider, ServerPlayer player) {
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
  public final void handleProviderOpen(@Nullable ILootrInfoProvider provider, ServerPlayer player) {
    handleProviderOpen(provider, player, null);
  }

  @Override
  public final void handleProviderOpen(@Nullable ILootrInfoProvider provider, ServerPlayer player, @Nullable MenuBuilder menuBuilder) {
    if (provider == null) {
      return;
    }
    if (player.isSpectator()) {
      player.openMenu(null);
      return;
    }
    if (provider.getInfoLevel() == null || provider.getInfoLevel().isClientSide()) {
      return;
    }

    if (!provider.canPlayerOpen(player)) {
      return;
    }
    if (LootrAPI.isDecayed(provider) && provider.canDecay()) {
      provider.performDecay();
      player.displayClientMessage(Component.translatable("lootr.message.decayed")
          .setStyle(LootrAPI.getDecayStyle()), true);
      LootrAPI.removeDecayed(provider);
      return;
    } else {
      if (provider.canDecay()) {
        int decayValue = LootrAPI.getRemainingDecayValue(provider);
        if (decayValue > 0 && LootrAPI.shouldNotify(decayValue)) {
          player.displayClientMessage(Component.translatable("lootr.message.decay_in", decayValue / 20)
              .setStyle(LootrAPI.getDecayStyle()), true);
        } else if (decayValue == -1) {
          if (LootrAPI.isDecaying(provider)) {
            LootrAPI.setDecaying(provider);
            player.displayClientMessage(Component.translatable("lootr.message.decay_start", LootrAPI.getDecayValue() / 20)
                .setStyle(LootrAPI.getDecayStyle()), true);
          }
        }
      }
    }
    provider.performTrigger(player);
    boolean shouldUpdate = false;
    if (LootrAPI.isRefreshed(provider) && provider.canRefresh()) {
      provider.performRefresh();
      provider.performClose();
      LootrAPI.removeRefreshed(provider);
      player.displayClientMessage(Component.translatable("lootr.message.refreshed")
          .setStyle(LootrAPI.getRefreshStyle()), true);
      shouldUpdate = true;
    }
    if (provider.canRefresh()) {
      int refreshValue = LootrAPI.getRemainingRefreshValue(provider);
      if (refreshValue > 0 && LootrAPI.shouldNotify(refreshValue)) {
        player.displayClientMessage(Component.translatable("lootr.message.refresh_in", refreshValue / 20)
            .setStyle(LootrAPI.getRefreshStyle()), true);
      } else if (refreshValue == -1) {
        if (LootrAPI.isRefreshing(provider)) {
          LootrAPI.setRefreshing(provider);
          player.displayClientMessage(Component.translatable("lootr.message.refresh_start", LootrAPI.getRefreshValue() / 20)
              .setStyle(LootrAPI.getRefreshStyle()), true);
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
    PiglinAi.angerNearbyPiglins(player, true);
  }

  @Override
  public final void handleProviderTick(@Nullable ILootrInfoProvider provider) {
    if (provider == null) {
      return;
    }

    if (provider.getInfoLevel() == null || provider.getInfoLevel().isClientSide()) {
      return;
    }

    // TODO: Refactor this to avoid loading the data save unnecessarily
    if (LootrAPI.shouldPerformDecayWhileTicking() && LootrAPI.isDecayed(provider) && provider.hasBeenOpened() && provider.canDecay()) {
      provider.performDecay();
      LootrAPI.removeDecayed(provider);
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
      LootrAPI.removeRefreshed(provider);
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
  public final void handleProviderClientTick(@Nullable ILootrInfoProvider provider) {
    if (provider == null) {
      return;
    }

    if (provider.getInfoLevel() == null || !provider.getInfoLevel().isClientSide()) {
      return;
    }

    if (LootrAPI.shouldDisplayUnopenedParticles()) {
      var type = provider.getInfoNewType();
      if (type != null && type.displaysUnopenedParticle()) {
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
  @Deprecated
  public final ILootrInventory getInventory(ILootrInfoProvider provider, ServerPlayer player, LootFiller filler) {
    return DataStorage.getInventory(provider, player, filler);
  }

  @Override
  public final ILootrInventory getInventory(ILootrInfoProvider provider, ServerPlayer player, LootFiller filler, @Nullable MenuBuilder menuBuilder) {
    ILootrInventory inventory = DataStorage.getInventory(provider, player, filler);
    if (inventory != null && menuBuilder != null) {
      inventory.setMenuBuilder(menuBuilder);
    }
    return inventory;
  }

  @Override
  public final @Nullable ILootrSavedData getData(ILootrInfoProvider provider) {
    return DataStorage.getData(provider);
  }

  @Override
  public final boolean shouldDiscard() {
    return LootrAPI.shouldDiscardIdAndOpeners;
  }

  @Override
  @Deprecated
  public final boolean isAwarded(UUID uuid, ServerPlayer player) {
    return DataStorage.isAwarded(uuid, player);
  }

  @Deprecated
  @Override
  public final void award(UUID id, ServerPlayer player) {
    DataStorage.award(id, player);
  }

  @Override
  public final int getRemainingDecayValue(ILootrInfoProvider provider) {
    return DataStorage.getDecayValue(provider);
  }

  @Override
  public final boolean isDecayed(ILootrInfoProvider provider) {
    return DataStorage.isDecayed(provider);
  }

  @Override
  public final void setDecaying(ILootrInfoProvider provider) {
    DataStorage.setDecaying(provider);
  }

  @Override
  public final void removeDecayed(ILootrInfoProvider provider) {
    DataStorage.removeDecayed(provider);
  }

  @Override
  public final int getRemainingRefreshValue(ILootrInfoProvider provider) {
    return DataStorage.getRefreshValue(provider);
  }

  @Override
  public final boolean isRefreshed(ILootrInfoProvider provider) {
    return DataStorage.isRefreshed(provider);
  }

  @Override
  public final void setRefreshing(ILootrInfoProvider provider) {
    DataStorage.setRefreshing(provider);
  }

  @Override
  public final void removeRefreshed(ILootrInfoProvider provider) {
    DataStorage.removeRefreshed(provider);
  }

  @Override
  @Nullable
  public final <T extends BlockEntity> ILootrBlockEntity resolveBlockEntity(T blockEntity) {
    return LootrServiceRegistry.convertBlockEntity(blockEntity);
  }

  @Override
  public final <T extends Entity> ILootrEntity resolveEntity(T entity) {
    return LootrServiceRegistry.convertEntity(entity);
  }

  private static final BoundingBox DESERT_PYRAMID_ADDITIONAL = new BoundingBox(-5, -30, -5, 5, 4, 4);

  @Override
  public boolean isTaggedStructurePresent(ServerLevel level, ChunkPos chunkPos, TagKey<Structure> tag, BlockPos pos) {
    Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
    List<StructureStart> starts = level.structureManager()
        .startsForStructure(chunkPos, o -> registry.getHolder(registry.getId(o)).map(b -> b.is(tag)).orElse(false));
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
/*      } else if (start.getStructure().type().equals(StructureType.JUNGLE_TEMPLE)) {
        // Compensate for the fact that the jungle pyramid bounding box is 2 short
        // TODO: I don't think it ever reaches this point even if it is the jungle temple
        // due to inflation.
        BoundingBox jungle = new BoundingBox(extended.minX(), extended.minY() - 2, extended.minZ(), extended.maxX(), extended.maxY(), extended.maxZ());
        if (jungle.isInside(pos)) {
          return true;
        }*/
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

    if (LootrAPI.resolveBlockEntity(blockEntity) instanceof ILootrInfoProvider provider && player instanceof ServerPlayer serverPlayer && provider.canDropContentsWhenBroken()) {
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
  @Nullable
  public BlockState replacementBlockState(BlockState original) {
    return LootrServiceRegistry.getReplacementBlockState(original);
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
  public <T> ILootrDataAdapter<T> getAdapter(T type) {
    return LootrServiceRegistry.getAdapter(type);
  }

  @Nullable
  @Override
  public <T> ILootrItemFrameAdapter<T> getItemFrameAdapter(T type) {
    return LootrServiceRegistry.getItemFrameAdapter(type);
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
      sherdsType = BuiltInRegistries.DATA_COMPONENT_TYPE.get(LootrConstants.SHERDSAPI_POT_DECORATIONS);
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
  public PotDecorationsAdapter getDecorationsAdapter(BlockEntity.DataComponentInput stack) {
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
}
