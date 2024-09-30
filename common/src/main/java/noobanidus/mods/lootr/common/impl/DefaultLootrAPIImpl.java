package noobanidus.mods.lootr.common.impl;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.ILootrAPI;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.MenuBuilder;
import noobanidus.mods.lootr.common.api.data.DefaultLootFiller;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.common.api.data.ILootrSavedData;
import noobanidus.mods.lootr.common.api.data.LootFiller;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrCart;
import noobanidus.mods.lootr.common.api.data.inventory.ILootrInventory;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.data.DataStorage;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class DefaultLootrAPIImpl implements ILootrAPI {
  @Override
  public final void handleProviderSneak(@Nullable ILootrInfoProvider provider, ServerPlayer player) {
    if (provider == null) {
      return;
    }
    if (provider.removeVisualOpener(player)) {
      provider.performClose(player);
      provider.performUpdate(player);
    }
  }

  @Override
  public final void handleProviderOpen(@Nullable ILootrInfoProvider provider, ServerPlayer player) {
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
    // TODO: HANDLE LOCKKEY ELSEWHERE
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
  public final void handleProviderTick(@Nullable ILootrInfoProvider provider) {
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
  public final ILootrInventory getInventory(ILootrInfoProvider provider, ServerPlayer player, LootFiller filler) {
    return DataStorage.getInventory(provider, player, filler);
  }

  @Override
  public final ILootrInventory getInventory(ILootrInfoProvider provider, ServerPlayer player, LootFiller filler, MenuBuilder menuBuilder) {
    ILootrInventory inventory = DataStorage.getInventory(provider, player, filler);
    if (inventory != null) {
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
  public final boolean isAwarded(UUID uuid, ServerPlayer player) {
    return DataStorage.isAwarded(uuid, player);
  }

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
  public final <T extends BlockEntity> ILootrBlockEntity resolveBlockEntity(T blockEntity) {
    return LootrServiceRegistry.convertBlockEntity(blockEntity);
  }

  @Override
  public final <T extends Entity> ILootrCart resolveEntity(T entity) {
    return LootrServiceRegistry.convertEntity(entity);
  }
}
