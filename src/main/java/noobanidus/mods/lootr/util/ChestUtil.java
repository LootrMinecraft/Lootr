package noobanidus.mods.lootr.util;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.data.DefaultLootFiller;
import noobanidus.mods.lootr.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.api.registry.LootrRegistry;
import noobanidus.mods.lootr.data.DataStorage;

@SuppressWarnings("unused")
public class ChestUtil {
  // TODO: The code for handling this should probably go into the API.
  public static void handleLootSneak(Block block, Level level, BlockPos pos, ServerPlayer player) {
    BlockEntity be = level.getBlockEntity(pos);
    // TODO:
    if (be instanceof ILootrInfoProvider blockEntity) {
      handleSneak(blockEntity, player);
    }
  }

  public static void handleSneak(ILootrInfoProvider provider, ServerPlayer player) {
    if (provider.removeVisualOpener(player)) {
      provider.performClose(player);
      provider.performUpdate(player);
    }
  }

  // TODO: Move to API?
  public static void handleLootCartSneak(Level level, ILootrInfoProvider cart, ServerPlayer player) {
    handleSneak(cart, player);
  }

  public static void handleProvider(ILootrInfoProvider provider, ServerPlayer player) {
    if (player.isSpectator()) {
      player.openMenu(null);
      return;
    }

    if (provider.getInfoUUID() == null) {
      player.displayClientMessage(Component.translatable("lootr.message.invalid_block").setStyle(LootrAPI.getInvalidStyle()), true);
      return;
    }
    if (DataStorage.isDecayed(provider)) {
      provider.performDecay(player);
      player.displayClientMessage(Component.translatable("lootr.message.decayed").setStyle(LootrAPI.getDecayStyle()), true);
      DataStorage.removeDecayed(provider);
      return;
    } else {
      int decayValue = DataStorage.getDecayValue(provider);
      if (decayValue > 0 && LootrAPI.shouldNotify(decayValue)) {
        player.displayClientMessage(Component.translatable("lootr.message.decay_in", decayValue / 20).setStyle(LootrAPI.getDecayStyle()), true);
      } else if (decayValue == -1) {
        if (LootrAPI.isDecaying(provider)) {
          DataStorage.setDecaying(provider, decayValue);
          player.displayClientMessage(Component.translatable("lootr.message.decay_start", decayValue / 20).setStyle(LootrAPI.getDecayStyle()), true);
        }
      }
    }
    provider.performTrigger(player);
    if (DataStorage.isRefreshed(provider)) {
      DataStorage.refreshInventory(provider);
      DataStorage.removeRefreshed(provider);
      player.displayClientMessage(Component.translatable("lootr.message.refreshed").setStyle(LootrAPI.getRefreshStyle()), true);
    }
    int refreshValue = DataStorage.getRefreshValue(provider);
    if (refreshValue > 0 && LootrAPI.shouldNotify(refreshValue)) {
      player.displayClientMessage(Component.translatable("lootr.message.refresh_in", refreshValue / 20).setStyle(LootrAPI.getRefreshStyle()), true);
    } else if (refreshValue == -1) {
      if (LootrAPI.isRefreshing(provider)) {
        DataStorage.setRefreshing(provider, refreshValue);
        player.displayClientMessage(Component.translatable("lootr.message.refresh_start", refreshValue / 20).setStyle(LootrAPI.getRefreshStyle()), true);
      }
    }
    MenuProvider menuProvider = DataStorage.getInventory(provider, player, DefaultLootFiller.getInstance());
    if (menuProvider == null) {
      return;
    }
    if (!provider.hasOpened(player)) {
      player.awardStat(LootrRegistry.getLootedStat());
      LootrRegistry.getStatTrigger().trigger(player);
    }
    if (provider.addOpener(player)) {
      provider.performOpen(player);
      provider.performUpdate(player);
    }
    // TODO: Opened stat
    player.openMenu(menuProvider);
    // TODO: Instances using this check the block tags first.
    PiglinAi.angerNearbyPiglins(player, true);

  }

  public static void handleLootChest(Block block, Level level, BlockPos pos, ServerPlayer player) {
    BlockEntity te = level.getBlockEntity(pos);
    if (te instanceof ILootrInfoProvider provider) {
      handleProvider(provider, player);
    }
  }

  public static void handleLootCart(Level level, ILootrInfoProvider cart, ServerPlayer player) {
    handleProvider(cart, player);
  }

}
