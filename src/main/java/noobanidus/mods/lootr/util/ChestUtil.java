package noobanidus.mods.lootr.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraftforge.network.PacketDistributor;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.block.LootrShulkerBlock;
import noobanidus.mods.lootr.block.entities.LootrInventoryBlockEntity;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.data.DataStorage;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.init.ModAdvancements;
import noobanidus.mods.lootr.init.ModStats;
import noobanidus.mods.lootr.network.CloseCart;
import noobanidus.mods.lootr.network.PacketHandler;
import noobanidus.mods.lootr.network.UpdateModelData;

import java.util.Random;
import java.util.UUID;

@SuppressWarnings("unused")
public class ChestUtil {
  public static Random random = new Random();

  public static boolean handleLootSneak(Block block, Level level, BlockPos pos, Player player) {
    if (level.isClientSide()) {
      return false;
    }
    if (player.isSpectator()) {
      return false;
    }

    BlockEntity te = level.getBlockEntity(pos);
    if (te instanceof ILootBlockEntity tile) {
      if (tile.getOpeners().remove(player.getUUID())) {
        te.setChanged();
        tile.updatePacketViaState();
        UpdateModelData message = new UpdateModelData(te.getBlockPos());
        PacketHandler.sendToInternal(message, (ServerPlayer) player);
      }
      return true;
    }

    return false;
  }

  public static void handleLootCartSneak(Level level, LootrChestMinecartEntity cart, Player player) {
    if (level.isClientSide()) {
      return;
    }

    if (player.isSpectator()) {
      return;
    }

    cart.getOpeners().remove(player.getUUID());
    CloseCart open = new CloseCart(cart.getId());
    PacketHandler.sendInternal(PacketDistributor.TRACKING_ENTITY.with(() -> cart), open);
  }

  public static boolean handleLootChest(Block block, Level level, BlockPos pos, Player player) {
    if (level.isClientSide()) {
      return false;
    }
    if (player.isSpectator()) {
      player.openMenu(null);
      return false;
    }
    BlockEntity te = level.getBlockEntity(pos);
    if (te instanceof ILootBlockEntity tile) {
      UUID tileId = tile.getTileId();
      if (DataStorage.isDecayed(tileId)) {
        level.destroyBlock(pos, true);
        player.displayClientMessage(new TranslatableComponent("lootr.message.decayed").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED)).withBold(true)), true);
        DataStorage.removeDecayed(tileId);
        return false;
      } else {
        int decayValue = DataStorage.getDecayValue(tileId);
        if (decayValue > 0 && ConfigManager.shouldNotify(decayValue)) {
          player.displayClientMessage(new TranslatableComponent("lootr.message.decay_in", decayValue / 20).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED)).withBold(true)), true);
        } else if (decayValue == -1) {
          if (ConfigManager.isDecaying((ServerLevel) level, (ILootBlockEntity) te)) {
            DataStorage.setDecaying(tileId, ConfigManager.DECAY_VALUE.get());
            player.displayClientMessage(new TranslatableComponent("lootr.message.decay_start", ConfigManager.DECAY_VALUE.get() / 20).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED)).withBold(true)), true);
          }
        }
      }
      if (block instanceof BarrelBlock) {
        ModAdvancements.BARREL_PREDICATE.trigger((ServerPlayer) player, ((ILootBlockEntity) te).getTileId());
      } else if (block instanceof ChestBlock) {
        ModAdvancements.CHEST_PREDICATE.trigger((ServerPlayer) player, ((ILootBlockEntity) te).getTileId());
      } else if (block instanceof LootrShulkerBlock) {
        ModAdvancements.SHULKER_PREDICATE.trigger((ServerPlayer) player, ((ILootBlockEntity) te).getTileId());
      }
      if (DataStorage.isRefreshed(tileId)) {
        DataStorage.refreshInventory(level, pos, ((ILootBlockEntity) te).getTileId(), (ServerPlayer) player);
        DataStorage.removeRefreshed(tileId);
        player.displayClientMessage(new TranslatableComponent("lootr.message.refreshed").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)).withBold(true)), true);
      }
      int refreshValue = DataStorage.getRefreshValue(tileId);
      if (refreshValue > 0 && ConfigManager.shouldNotify(refreshValue)) {
        player.displayClientMessage(new TranslatableComponent("lootr.message.refresh_in", refreshValue / 20).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)).withBold(true)), true);
      } else if (refreshValue == -1) {
        if (ConfigManager.isRefreshing((ServerLevel) level, (ILootBlockEntity) te)) {
          DataStorage.setRefreshing(tileId, ConfigManager.REFRESH_VALUE.get());
          player.displayClientMessage(new TranslatableComponent("lootr.message.refresh_start", ConfigManager.REFRESH_VALUE.get() / 20).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)).withBold(true)), true);
        }
      }
      MenuProvider provider = DataStorage.getInventory(level, ((ILootBlockEntity) te).getTileId(), pos, (ServerPlayer) player, (RandomizableContainerBlockEntity) te, ((ILootBlockEntity) te)::unpackLootTable);
      if (!DataStorage.isScored(player.getUUID(), ((ILootBlockEntity) te).getTileId())) {
        player.awardStat(ModStats.LOOTED_STAT);
        ModAdvancements.SCORE_PREDICATE.trigger((ServerPlayer) player, null);
        DataStorage.score(player.getUUID(), ((ILootBlockEntity) te).getTileId());
      }
      if (tile.getOpeners().add(player.getUUID())) {
        te.setChanged();
        tile.updatePacketViaState();
      }
      player.openMenu(provider);
      PiglinAi.angerNearbyPiglins(player, true);
      return true;
    } else {
      return false;
    }
  }

  public static void handleLootCart(Level level, LootrChestMinecartEntity cart, Player player) {
    if (!level.isClientSide()) {
      if (player.isSpectator()) {
        player.openMenu(null);
      } else {
        ModAdvancements.CART_PREDICATE.trigger((ServerPlayer) player, cart.getUUID());
        UUID tileId = cart.getUUID();
        if (DataStorage.isDecayed(tileId)) {
          cart.destroy(DamageSource.OUT_OF_WORLD);
          player.displayClientMessage(new TranslatableComponent("lootr.message.decayed").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED)).withBold(true)), true);
          DataStorage.removeDecayed(tileId);
          return;
        } else {
          int decayValue = DataStorage.getDecayValue(tileId);
          if (decayValue > 0 && ConfigManager.shouldNotify(decayValue)) {
            player.displayClientMessage(new TranslatableComponent("lootr.message.decay_in", decayValue / 20).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED)).withBold(true)), true);
          } else if (decayValue == -1) {
            if (ConfigManager.isDecaying((ServerLevel) level, cart)) {
              DataStorage.setDecaying(tileId, ConfigManager.DECAY_VALUE.get());
              player.displayClientMessage(new TranslatableComponent("lootr.message.decay_start", ConfigManager.DECAY_VALUE.get() / 20).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED)).withBold(true)), true);
            }
          }
        }
        if (!cart.getOpeners().contains(player.getUUID())) {
          cart.addOpener(player);
        }
        if (!DataStorage.isScored(player.getUUID(), cart.getUUID())) {
          player.awardStat(ModStats.LOOTED_STAT);
          ModAdvancements.SCORE_PREDICATE.trigger((ServerPlayer) player, null);
          DataStorage.score(player.getUUID(), cart.getUUID());
        }
        if (DataStorage.isRefreshed(tileId)) {
          DataStorage.refreshInventory(level, cart, (ServerPlayer) player);
          DataStorage.removeRefreshed(tileId);
          player.displayClientMessage(new TranslatableComponent("lootr.message.refreshed").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)).withBold(true)), true);
        }
        int refreshValue = DataStorage.getRefreshValue(tileId);
        if (refreshValue > 0 && ConfigManager.shouldNotify(refreshValue)) {
          player.displayClientMessage(new TranslatableComponent("lootr.message.refresh_in", refreshValue / 20).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)).withBold(true)), true);
        } else if (refreshValue == -1) {
          if (ConfigManager.isRefreshing((ServerLevel) level, cart)) {
            DataStorage.setRefreshing(tileId, ConfigManager.REFRESH_VALUE.get());
            player.displayClientMessage(new TranslatableComponent("lootr.message.refresh_start", ConfigManager.REFRESH_VALUE.get() / 20).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)).withBold(true)), true);
          }
        }
        MenuProvider provider = DataStorage.getInventory(level, cart, (ServerPlayer) player, cart::addLoot);
        player.openMenu(provider);
      }
    }
  }

  public static boolean handleLootInventory(Block block, Level level, BlockPos pos, Player player) {
    if (level.isClientSide()) {
      return false;
    }
    if (player.isSpectator()) {
      player.openMenu(null);
      return false;
    }
    BlockEntity te = level.getBlockEntity(pos);
    if (te instanceof LootrInventoryBlockEntity tile) {
      ModAdvancements.CHEST_PREDICATE.trigger((ServerPlayer) player, tile.getTileId());
      NonNullList<ItemStack> stacks = null;
      if (tile.getCustomInventory() != null) {
        stacks = copyItemList(tile.getCustomInventory());
      }
      UUID tileId = tile.getTileId();
      if (DataStorage.isRefreshed(tileId)) {
        DataStorage.refreshInventory(level, pos, ((ILootBlockEntity) te).getTileId(), stacks, (ServerPlayer) player);
        DataStorage.removeRefreshed(tileId);
        player.displayClientMessage(new TranslatableComponent("lootr.message.refreshed").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)).withBold(true)), true);
      }
      int refreshValue = DataStorage.getRefreshValue(tileId);
      if (refreshValue > 0 || ConfigManager.shouldNotify(refreshValue)) {
        player.displayClientMessage(new TranslatableComponent("lootr.message.refresh_in", refreshValue / 20).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)).withBold(true)), true);
      } else if (refreshValue == -1) {
        if (ConfigManager.isRefreshing((ServerLevel) level, tile)) {
          DataStorage.setRefreshing(tileId, ConfigManager.REFRESH_VALUE.get());
          player.displayClientMessage(new TranslatableComponent("lootr.message.refresh_start", ConfigManager.REFRESH_VALUE.get() / 20).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)).withBold(true)), true);
        }
      }
      MenuProvider provider = DataStorage.getInventory(level, tile.getTileId(), stacks, (ServerPlayer) player, pos, tile);
      if (!DataStorage.isScored(player.getUUID(), ((ILootBlockEntity) te).getTileId())) {
        player.awardStat(ModStats.LOOTED_STAT);
        ModAdvancements.SCORE_PREDICATE.trigger((ServerPlayer) player, null);
        DataStorage.score(player.getUUID(), ((ILootBlockEntity) te).getTileId());
      }
      if (tile.getOpeners().add(player.getUUID())) {
        te.setChanged();
        tile.updatePacketViaState();
      }
      player.openMenu(provider);
      PiglinAi.angerNearbyPiglins(player, true);
      return true;
    } else {
      return false;
    }
  }

  public static NonNullList<ItemStack> copyItemList(NonNullList<ItemStack> reference) {
    NonNullList<ItemStack> contents = NonNullList.withSize(reference.size(), ItemStack.EMPTY);
    for (int i = 0; i < reference.size(); i++) {
      contents.set(i, reference.get(i).copy());
    }
    return contents;
  }
}
