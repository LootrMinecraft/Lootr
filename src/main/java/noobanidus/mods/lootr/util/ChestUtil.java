package noobanidus.mods.lootr.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import noobanidus.mods.lootr.advancement.ContainerTrigger;
import noobanidus.mods.lootr.api.IHasOpeners;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.block.LootrShulkerBlock;
import noobanidus.mods.lootr.block.entities.LootrInventoryBlockEntity;
import noobanidus.mods.lootr.data.DataStorage;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.init.ModAdvancements;
import noobanidus.mods.lootr.init.ModStats;
import noobanidus.mods.lootr.network.to_client.PacketCloseCart;
import noobanidus.mods.lootr.network.to_client.PacketCloseContainer;

import java.util.UUID;

@SuppressWarnings("unused")
public class ChestUtil {
  // TODO: The code for handling this should probably go into the API.
  public static void handleLootSneak(Block block, Level level, BlockPos pos, Player player) {
    if (level.isClientSide() || player.isSpectator()) {
      return;
    }

    BlockEntity te = level.getBlockEntity(pos);
    if (te instanceof ILootrBlockEntity tile) {
      if (tile.getOpeners().remove(player.getUUID())) {
        te.setChanged();
        tile.updatePacketViaForce(te);
        PacketDistributor.sendToPlayer((ServerPlayer) player, new PacketCloseContainer(te.getBlockPos()));
      }
    }

  }

  // TODO: Move to API?
  public static void handleLootCartSneak(Level level, LootrChestMinecartEntity cart, Player player) {
    if (level.isClientSide() || player.isSpectator()) {
      return;
    }

    cart.getOpeners().remove(player.getUUID());
    PacketDistributor.sendToPlayersTrackingEntity(cart, new PacketCloseCart(cart.getId()));
  }

  public static void handleLootChest(Block block, Level level, BlockPos pos, Player player) {
    if (level.isClientSide() || player.isSpectator()) {
      if (player.isSpectator()) {
        player.openMenu(null);
      }
      return;
    }
    BlockEntity te = level.getBlockEntity(pos);
    if (te instanceof ILootrBlockEntity tile) {
      UUID tileId = tile.getInfoUUID();
      if (tileId == null) {
        player.displayClientMessage(Component.translatable("lootr.message.invalid_block").setStyle(LootrAPI.getInvalidStyle()), true);
        return;
      }
      if (DataStorage.isDecayed(tileId)) {
        level.destroyBlock(pos, true);
        notifyDecay(player, tileId);
        return;
      } else {
        int decayValue = DataStorage.getDecayValue(tileId);
        if (decayValue > 0 && LootrAPI.shouldNotify(decayValue)) {
          player.displayClientMessage(Component.translatable("lootr.message.decay_in", decayValue / 20).setStyle(LootrAPI.getDecayStyle()), true);
        } else if (decayValue == -1) {
          if (LootrAPI.isDecaying(tile)) {
            startDecay(player, tileId, decayValue);
          }
        }
      }
      ContainerTrigger trigger = ModAdvancements.CHEST.get();
      if (block instanceof BarrelBlock) {
        trigger = ModAdvancements.BARREL.get();
      } else if (block instanceof LootrShulkerBlock) {
        trigger = ModAdvancements.SHULKER.get();
      }
      trigger.trigger((ServerPlayer) player, tileId);
      // Generalize refresh check
      if (DataStorage.isRefreshed(tileId)) {
        DataStorage.refreshInventory(level, pos, tileId, (ServerPlayer) player);
        notifyRefresh(player, tileId);
      }
      int refreshValue = DataStorage.getRefreshValue(tileId);
      if (refreshValue > 0 && LootrAPI.shouldNotify(refreshValue)) {
        player.displayClientMessage(Component.translatable("lootr.message.refresh_in", refreshValue / 20).setStyle(LootrAPI.getRefreshStyle()), true);
      } else if (refreshValue == -1) {
        if (LootrAPI.isRefreshing(tile)) {
          startRefresh(player, tileId, refreshValue);
        }
      }
      // Check if it already refreshed
      MenuProvider provider = DataStorage.getInventory(level, tileId, pos, (ServerPlayer) player, (RandomizableContainerBlockEntity) te, tile::lootFiller);
      if (provider == null) {
        // Error messages are already handled by nested methods in `getInventory`
        return;
      }
      checkScore((ServerPlayer) player, tileId);
      if (addOpener(tile, player)) {
        te.setChanged();
        ((ILootrBlockEntity) te).updatePacketViaForce(te);
      }
      player.openMenu(provider);
      // TODO: Instances using this check the block tags first.
      PiglinAi.angerNearbyPiglins(player, true);
    }
  }

  private static boolean addOpener(IHasOpeners openable, Player player) {
    return openable.getOpeners().add(player.getUUID());
  }

  public static void handleLootCart(Level level, LootrChestMinecartEntity cart, Player player) {
    if (level.isClientSide() || player.isSpectator()) {
      if (player.isSpectator()) {
        player.openMenu(null);
      }
      return;
    }

    ModAdvancements.CART.get().trigger((ServerPlayer) player, cart.getUUID());
    UUID tileId = cart.getUUID();
    if (DataStorage.isDecayed(tileId)) {
      cart.destroy(cart.damageSources().fellOutOfWorld());
      notifyDecay(player, tileId);
      return;
    } else {
      int decayValue = DataStorage.getDecayValue(tileId);
      if (decayValue > 0 && LootrAPI.shouldNotify(decayValue)) {
        player.displayClientMessage(Component.translatable("lootr.message.decay_in", decayValue / 20).setStyle(LootrAPI.getDecayStyle()), true);
      } else if (decayValue == -1) {
        if (LootrAPI.isDecaying(cart)) {
          startDecay(player, tileId, decayValue);
        }
      }
    }
    addOpener(cart, player);
    checkScore((ServerPlayer) player, cart.getUUID());
    if (DataStorage.isRefreshed(tileId)) {
      DataStorage.refreshInventory(level, cart, (ServerPlayer) player);
      notifyRefresh(player, tileId);
    }
    int refreshValue = DataStorage.getRefreshValue(tileId);
    if (refreshValue > 0 && LootrAPI.shouldNotify(refreshValue)) {
      player.displayClientMessage(Component.translatable("lootr.message.refresh_in", refreshValue / 20).setStyle(LootrAPI.getRefreshStyle()), true);
    } else if (refreshValue == -1) {
      if (LootrAPI.isRefreshing(cart)) {
        startRefresh(player, tileId, refreshValue);
      }
    }
    MenuProvider provider = DataStorage.getInventory(level, cart, (ServerPlayer) player, cart::lootFiller);
    if (provider == null) {
      // Error messages are already handled by nested methods in `getInventory`
      return;
    }
    player.openMenu(provider);
  }

  public static void handleLootInventory(Block block, Level level, BlockPos pos, Player player) {
    if (level.isClientSide() || player.isSpectator()) {
      if (player.isSpectator()) {
        player.openMenu(null);
      }
      return;
    }
    BlockEntity te = level.getBlockEntity(pos);
    if (te instanceof LootrInventoryBlockEntity tile) {
      ModAdvancements.CHEST.get().trigger((ServerPlayer) player, tile.getInfoUUID());
      NonNullList<ItemStack> stacks = null;
      if (tile.getCustomInventory() != null) {
        stacks = copyItemList(tile.getCustomInventory());
      }
      UUID tileId = tile.getInfoUUID();
      if (DataStorage.isRefreshed(tileId)) {
        DataStorage.refreshInventory(level, pos, tile.getInfoUUID(), stacks, (ServerPlayer) player);
        notifyRefresh(player, tileId);
      }
      int refreshValue = DataStorage.getRefreshValue(tileId);
      if (refreshValue > 0 && LootrAPI.shouldNotify(refreshValue)) {
        player.displayClientMessage(Component.translatable("lootr.message.refresh_in", refreshValue / 20).setStyle(LootrAPI.getRefreshStyle()), true);
      } else if (refreshValue == -1) {
        if (LootrAPI.isRefreshing(tile)) {
          startRefresh(player, tileId, refreshValue);
        }
      }
      MenuProvider provider = DataStorage.getInventory(level, tile.getInfoUUID(), stacks, (ServerPlayer) player, pos, tile);
      if (provider == null) {
        // Error messages are already handled by nested methods in `getInventory`
        return;
      }
      checkScore((ServerPlayer) player, tile.getInfoUUID());
      if (addOpener(tile, player)) {
        te.setChanged();
        tile.updatePacketViaForce(tile);
      }
      player.openMenu(provider);
      PiglinAi.angerNearbyPiglins(player, true);
    }
  }

  public static NonNullList<ItemStack> copyItemList(NonNullList<ItemStack> reference) {
    NonNullList<ItemStack> contents = NonNullList.withSize(reference.size(), ItemStack.EMPTY);
    for (int i = 0; i < reference.size(); i++) {
      contents.set(i, reference.get(i).copy());
    }
    return contents;
  }

  private static void checkScore(ServerPlayer player, UUID tileId) {
    if (!DataStorage.isScored(player.getUUID(), tileId)) {
      player.awardStat(ModStats.LOOTED_STAT);
      ModAdvancements.STAT.get().trigger(player);
      DataStorage.score(player.getUUID(), tileId);
    }
  }

  private static void notifyDecay(Player player, UUID tileId) {
    player.displayClientMessage(Component.translatable("lootr.message.decayed").setStyle(LootrAPI.getDecayStyle()), true);
    DataStorage.removeDecayed(tileId);
  }

  private static void notifyRefresh(Player player, UUID tileId) {
    DataStorage.removeRefreshed(tileId);
    player.displayClientMessage(Component.translatable("lootr.message.refreshed").setStyle(LootrAPI.getRefreshStyle()), true);
  }

  private static void startDecay(Player player, UUID tileId, int decayValue) {
    DataStorage.setDecaying(tileId, decayValue);
    player.displayClientMessage(Component.translatable("lootr.message.decay_start", decayValue / 20).setStyle(LootrAPI.getDecayStyle()), true);
  }

  private static void startRefresh(Player player, UUID tileId, int refreshValue) {
    DataStorage.setRefreshing(tileId, refreshValue);
    player.displayClientMessage(Component.translatable("lootr.message.refresh_start", refreshValue / 20).setStyle(LootrAPI.getRefreshStyle()), true);
  }
}
