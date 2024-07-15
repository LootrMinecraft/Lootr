package noobanidus.mods.lootr.util;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import noobanidus.mods.lootr.api.IOpeners;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.advancement.IContainerTrigger;
import noobanidus.mods.lootr.api.data.DefaultLootFiller;
import noobanidus.mods.lootr.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.api.network.ILootrPacket;
import noobanidus.mods.lootr.api.registry.LootrRegistry;
import noobanidus.mods.lootr.data.DataStorage;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

import java.util.UUID;

@SuppressWarnings("unused")
public class ChestUtil {
  // TODO: The code for handling this should probably go into the API.
  public static void handleLootSneak(Block block, Level level, BlockPos pos, ServerPlayer player) {
    if (level.isClientSide() || player.isSpectator()) {
      return;
    }

    BlockEntity be = level.getBlockEntity(pos);
    // TODO:
    if (be instanceof ILootrInfoProvider blockEntity) {
      if (blockEntity.removeVisualOpener(player)) {
        blockEntity.performClose(player);
        blockEntity.performUpdate(player);
      }
    }

  }

  // TODO: Move to API?
  public static void handleLootCartSneak(Level level, ILootrInfoProvider cart, ServerPlayer player) {
    if (level.isClientSide() || player.isSpectator()) {
      return;
    }

    if (cart.removeVisualOpener(player)) {
      cart.performClose(player);
    }
  }

  public static void handleLootChest(Block block, Level level, BlockPos pos, ServerPlayer player) {
    if (level.isClientSide() || player.isSpectator()) {
      if (player.isSpectator()) {
        player.openMenu(null);
      }
      return;
    }
    BlockEntity te = level.getBlockEntity(pos);
    if (te instanceof ILootrInfoProvider provider) {
      UUID infoId = provider.getInfoUUID();
      if (infoId == null) {
        player.displayClientMessage(Component.translatable("lootr.message.invalid_block").setStyle(LootrAPI.getInvalidStyle()), true);
        return;
      }
      if (DataStorage.isDecayed(infoId)) {
        provider.performDecay(player);
        notifyDecay(player, infoId);
        return;
      } else {
        int decayValue = DataStorage.getDecayValue(infoId);
        if (decayValue > 0 && LootrAPI.shouldNotify(decayValue)) {
          player.displayClientMessage(Component.translatable("lootr.message.decay_in", decayValue / 20).setStyle(LootrAPI.getDecayStyle()), true);
        } else if (decayValue == -1) {
          if (LootrAPI.isDecaying(provider)) {
            startDecay(player, infoId, decayValue);
          }
        }
      }
      provider.performTrigger(player);
      // Generalize refresh check
      if (DataStorage.isRefreshed(infoId)) {
        DataStorage.refreshInventory(provider);
        notifyRefresh(player, infoId);
      }
      int refreshValue = DataStorage.getRefreshValue(infoId);
      if (refreshValue > 0 && LootrAPI.shouldNotify(refreshValue)) {
        player.displayClientMessage(Component.translatable("lootr.message.refresh_in", refreshValue / 20).setStyle(LootrAPI.getRefreshStyle()), true);
      } else if (refreshValue == -1) {
        if (LootrAPI.isRefreshing(provider)) {
          startRefresh(player, infoId, refreshValue);
        }
      }
      // Check if it already refreshed
      MenuProvider menuProvider = DataStorage.getInventory(provider, player, DefaultLootFiller.getInstance());
      if (menuProvider == null) {
        // Error messages are already handled by nested methods in `getInventory`
        return;
      }
      checkAndScore(provider, player);
      if (addOpener(provider, player)) {
        provider.markChanged();
        provider.performUpdate(player);
        provider.performOpen(player);
      }
      player.openMenu(menuProvider);
      // TODO: Instances using this check the block tags first.
      PiglinAi.angerNearbyPiglins(player, true);
    }
  }

  private static boolean addOpener(IOpeners openable, Player player) {
    boolean result1 = openable.addActualOpener(player);
    boolean result2 = openable.addVisualOpener(player);
    return result1 || result2;
  }

  public static void handleLootCart(Level level, ILootrInfoProvider cart, ServerPlayer player) {
    if (level.isClientSide() || player.isSpectator()) {
      if (player.isSpectator()) {
        player.openMenu(null);
      }
      return;
    }

    UUID infoId = cart.getInfoUUID();
    cart.performTrigger(player);

    if (DataStorage.isDecayed(infoId)) {
      cart.performDecay(player);
      // TODO: Destruction
      notifyDecay(player, infoId);
      return;
    } else {
      int decayValue = DataStorage.getDecayValue(infoId);
      if (decayValue > 0 && LootrAPI.shouldNotify(decayValue)) {
        player.displayClientMessage(Component.translatable("lootr.message.decay_in", decayValue / 20).setStyle(LootrAPI.getDecayStyle()), true);
      } else if (decayValue == -1) {
        if (LootrAPI.isDecaying(cart)) {
          startDecay(player, infoId, decayValue);
        }
      }
    }
    if (addOpener(cart, player)) {
      cart.performClose(player);
    }
    checkAndScore(cart, player);
    if (DataStorage.isRefreshed(infoId)) {
      DataStorage.refreshInventory(cart);
      notifyRefresh(player, infoId);
    }
    int refreshValue = DataStorage.getRefreshValue(infoId);
    if (refreshValue > 0 && LootrAPI.shouldNotify(refreshValue)) {
      player.displayClientMessage(Component.translatable("lootr.message.refresh_in", refreshValue / 20).setStyle(LootrAPI.getRefreshStyle()), true);
    } else if (refreshValue == -1) {
      if (LootrAPI.isRefreshing(cart)) {
        startRefresh(player, infoId, refreshValue);
      }
    }
    MenuProvider provider = DataStorage.getInventory(cart, player, DefaultLootFiller.getInstance());
    if (provider == null) {
      // Error messages are already handled by nested methods in `getInventory`
      return;
    }
    player.openMenu(provider);
  }

  private static void checkAndScore(IOpeners openable, ServerPlayer player) {
    if (!openable.hasOpened(player)) {
      player.awardStat(LootrRegistry.getLootedStat());
      LootrRegistry.getStatTrigger().trigger(player);
      openable.addActualOpener(player);
    }
  }

  private static void notifyDecay(Player player, UUID infoId) {
    player.displayClientMessage(Component.translatable("lootr.message.decayed").setStyle(LootrAPI.getDecayStyle()), true);
    DataStorage.removeDecayed(infoId);
  }

  private static void notifyRefresh(Player player, UUID infoId) {
    DataStorage.removeRefreshed(infoId);
    player.displayClientMessage(Component.translatable("lootr.message.refreshed").setStyle(LootrAPI.getRefreshStyle()), true);
  }

  private static void startDecay(Player player, UUID infoId, int decayValue) {
    DataStorage.setDecaying(infoId, decayValue);
    player.displayClientMessage(Component.translatable("lootr.message.decay_start", decayValue / 20).setStyle(LootrAPI.getDecayStyle()), true);
  }

  private static void startRefresh(Player player, UUID infoId, int refreshValue) {
    DataStorage.setRefreshing(infoId, refreshValue);
    player.displayClientMessage(Component.translatable("lootr.message.refresh_start", refreshValue / 20).setStyle(LootrAPI.getRefreshStyle()), true);
  }
}
