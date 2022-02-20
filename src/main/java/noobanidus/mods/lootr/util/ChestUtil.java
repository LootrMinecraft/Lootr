package noobanidus.mods.lootr.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import noobanidus.mods.lootr.api.tile.ILootTile;
import noobanidus.mods.lootr.block.LootrShulkerBlock;
import noobanidus.mods.lootr.block.tile.LootrInventoryTileEntity;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.data.DataStorage;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.init.ModAdvancements;
import noobanidus.mods.lootr.init.ModStats;
import noobanidus.mods.lootr.networking.CloseCart;
import noobanidus.mods.lootr.networking.PacketHandler;

@SuppressWarnings("unused")
public class ChestUtil {
  public static Random random = new Random();
  public static Set<Class<?>> tileClasses = new HashSet<>();

  public static boolean handleLootSneak(Block block, World world, BlockPos pos, PlayerEntity player) {
    if (world.isClientSide()) {
      return false;
    }
    if (player.isSpectator()) {
      return false;
    }

    TileEntity te = world.getBlockEntity(pos);
    if (te instanceof ILootTile) {
      Set<UUID> openers = ((ILootTile) te).getOpeners();
      openers.remove(player.getUUID());
      ((ILootTile) te).updatePacketViaState();
      return true;
    }

    return false;
  }

  public static void handleLootCartSneak(World world, LootrChestMinecartEntity cart, PlayerEntity player) {
    if (world.isClientSide()) {
      return;
    }

    if (player.isSpectator()) {
      return;
    }

    cart.getOpeners().remove(player.getUUID());
    CloseCart open = new CloseCart(cart.getId());
    PacketHandler.sendInternal(PacketDistributor.TRACKING_ENTITY.with(() -> cart), open);
  }

  public static boolean handleLootChest(Block block, World world, BlockPos pos, PlayerEntity player) {
    if (world.isClientSide()) {
      return false;
    }
    if (player.isSpectator()) {
      player.openMenu(null);
      return false;
    }
    TileEntity te = world.getBlockEntity(pos);
    if (te instanceof ILootTile) {
      UUID tileId = ((ILootTile) te).getTileId();
      if (DataStorage.isDecayed(tileId, world.getGameTime())) {
        world.destroyBlock(pos, true);
        DataStorage.removeDecayed(tileId);
        player.sendMessage(new TranslationTextComponent("lootr.message.decayed").setStyle(Style.EMPTY.withColor(TextFormatting.RED).withBold(true)), Util.NIL_UUID);
        return false;
      } else {
        int decayValue = DataStorage.getDecayValue(tileId, world.getGameTime());
        if (decayValue > 0) {
          player.sendMessage(new TranslationTextComponent("lootr.message.decay_in", decayValue / 20).setStyle(Style.EMPTY.withColor(TextFormatting.RED).withBold(true)), Util.NIL_UUID);
        } else if (decayValue == -1) {
          if (ConfigManager.isDecaying(world, (ILootTile) te)) {
            DataStorage.setDecaying(tileId, world.getGameTime() + ConfigManager.DECAY_VALUE.get());
            player.sendMessage(new TranslationTextComponent("lootr.message.decay_start", ConfigManager.DECAY_VALUE.get() / 20).setStyle(Style.EMPTY.withColor(TextFormatting.RED).withBold(true)), Util.NIL_UUID);
          }
        }
      }
      if (block instanceof BarrelBlock) {
        ModAdvancements.BARREL_PREDICATE.trigger((ServerPlayerEntity) player, ((ILootTile) te).getTileId());
      } else if (block instanceof ChestBlock) {
        ModAdvancements.CHEST_PREDICATE.trigger((ServerPlayerEntity) player, ((ILootTile) te).getTileId());
      } else if (block instanceof LootrShulkerBlock) {
        ModAdvancements.SHULKER_PREDICATE.trigger((ServerPlayerEntity) player, ((ILootTile) te).getTileId());
      }
      if (DataStorage.isRefreshed(player.getUUID(), tileId, world.getGameTime())) {
        DataStorage.refreshInventory(world, ((ILootTile) te).getTileId(), (ServerPlayerEntity) player);
        DataStorage.removeRefreshed(player.getUUID(), tileId);
        player.sendMessage(new TranslationTextComponent("lootr.message.refreshed").setStyle(Style.EMPTY.withColor(TextFormatting.BLUE).withBold(true)), Util.NIL_UUID);
      } else {
        int refreshValue = DataStorage.getRefreshValue(player.getUUID(), tileId, world.getGameTime());
        if (refreshValue > 0) {
          player.sendMessage(new TranslationTextComponent("lootr.message.refresh_in", refreshValue / 20).setStyle(Style.EMPTY.withColor(TextFormatting.BLUE).withBold(true)), Util.NIL_UUID);
        } else if (refreshValue == -1) {
          if (ConfigManager.isRefreshing(world, (ILootTile) te)) {
            DataStorage.setRefreshing(player.getUUID(), tileId, world.getGameTime() + ConfigManager.REFRESH_VALUE.get());
            player.sendMessage(new TranslationTextComponent("lootr.message.refresh_start", ConfigManager.REFRESH_VALUE.get() / 20).setStyle(Style.EMPTY.withColor(TextFormatting.BLUE).withBold(true)), Util.NIL_UUID);
          }
        }
      }
      INamedContainerProvider provider = DataStorage.getInventory(world, ((ILootTile) te).getTileId(), pos, (ServerPlayerEntity) player, (LockableLootTileEntity) te, ((ILootTile) te)::fillWithLoot);
      if (!DataStorage.isScored(player.getUUID(), ((ILootTile) te).getTileId())) {
        player.awardStat(ModStats.LOOTED_STAT);
        ModAdvancements.SCORE_PREDICATE.trigger((ServerPlayerEntity) player, null);
        DataStorage.score(player.getUUID(), ((ILootTile) te).getTileId());
      }
      player.openMenu(provider);
      PiglinTasks.angerNearbyPiglins(player, true);
      return true;
    } else {
      return false;
    }
  }

  public static void handleLootCart(World world, LootrChestMinecartEntity cart, PlayerEntity player) {
    if (!world.isClientSide()) {
      if (player.isSpectator()) {
        player.openMenu(null);
      } else {
        UUID tileId = cart.getUUID();
        if (DataStorage.isDecayed(tileId, world.getGameTime())) {
          cart.destroy(DamageSource.OUT_OF_WORLD);
          DataStorage.removeDecayed(tileId);
          player.sendMessage(new TranslationTextComponent("lootr.message.decayed").setStyle(Style.EMPTY.withColor(TextFormatting.RED).withBold(true)), Util.NIL_UUID);
          return;
        } else {
          int decayValue = DataStorage.getDecayValue(tileId, world.getGameTime());
          if (decayValue > 0) {
            player.sendMessage(new TranslationTextComponent("lootr.message.decay_in", decayValue / 20).setStyle(Style.EMPTY.withColor(TextFormatting.RED).withBold(true)), Util.NIL_UUID);
          } else if (decayValue == -1) {
            if (ConfigManager.isDecaying(world, cart)) {
              DataStorage.setDecaying(tileId, world.getGameTime() + ConfigManager.DECAY_VALUE.get());
              player.sendMessage(new TranslationTextComponent("lootr.message.decay_start", ConfigManager.DECAY_VALUE.get() / 20).setStyle(Style.EMPTY.withColor(TextFormatting.RED).withBold(true)), Util.NIL_UUID);
            }
          }
        }
        ModAdvancements.CART_PREDICATE.trigger((ServerPlayerEntity) player, cart.getUUID());

        if (!cart.getOpeners().contains(player.getUUID())) {
          cart.addOpener(player);
        }
        if (!DataStorage.isScored(player.getUUID(), cart.getUUID())) {
          player.awardStat(ModStats.LOOTED_STAT);
          ModAdvancements.SCORE_PREDICATE.trigger((ServerPlayerEntity) player, null);
          DataStorage.score(player.getUUID(), cart.getUUID());
        }
        if (DataStorage.isRefreshed(player.getUUID(), tileId, world.getGameTime())) {
          DataStorage.refreshInventory(world, cart, (ServerPlayerEntity) player);
          DataStorage.removeRefreshed(player.getUUID(), tileId);
          player.sendMessage(new TranslationTextComponent("lootr.message.refreshed").setStyle(Style.EMPTY.withColor(TextFormatting.BLUE).withBold(true)), Util.NIL_UUID);
        } else {
          int refreshValue = DataStorage.getRefreshValue(player.getUUID(), tileId, world.getGameTime());
          if (refreshValue > 0) {
            player.sendMessage(new TranslationTextComponent("lootr.message.refresh_in", refreshValue / 20).setStyle(Style.EMPTY.withColor(TextFormatting.BLUE).withBold(true)), Util.NIL_UUID);
          } else if (refreshValue == -1) {
            if (ConfigManager.isRefreshing(world, cart)) {
              DataStorage.setRefreshing(player.getUUID(), tileId, world.getGameTime() + ConfigManager.REFRESH_VALUE.get());
              player.sendMessage(new TranslationTextComponent("lootr.message.refresh_start", ConfigManager.REFRESH_VALUE.get() / 20).setStyle(Style.EMPTY.withColor(TextFormatting.BLUE).withBold(true)), Util.NIL_UUID);
            }
          }
        }
        INamedContainerProvider provider = DataStorage.getInventory(world, cart, (ServerPlayerEntity) player, cart::addLoot);
        player.openMenu(provider);
      }
    }
  }

  public static boolean handleLootInventory(Block block, World world, BlockPos pos, PlayerEntity player) {
    if (world.isClientSide()) {
      return false;
    }
    if (player.isSpectator()) {
      player.openMenu(null);
      return false;
    }
    TileEntity te = world.getBlockEntity(pos);
    if (te instanceof LootrInventoryTileEntity) {
      ModAdvancements.CHEST_PREDICATE.trigger((ServerPlayerEntity) player, ((LootrInventoryTileEntity) te).getTileId());
      LootrInventoryTileEntity tile = (LootrInventoryTileEntity) te;
      NonNullList<ItemStack> stacks = null;
      if (tile.getCustomInventory() != null) {
        stacks = copyItemList(tile.getCustomInventory());
      }
      UUID tileId = tile.getTileId();
      if (DataStorage.isRefreshed(player.getUUID(), tileId, world.getGameTime())) {
        DataStorage.refreshInventory(world, ((ILootTile) te).getTileId(), stacks, (ServerPlayerEntity) player);
        DataStorage.removeRefreshed(player.getUUID(), tileId);
        player.sendMessage(new TranslationTextComponent("lootr.message.refreshed").setStyle(Style.EMPTY.withColor(TextFormatting.BLUE).withBold(true)), Util.NIL_UUID);
      } else {
        int refreshValue = DataStorage.getRefreshValue(player.getUUID(), tileId, world.getGameTime());
        if (refreshValue > 0) {
          player.sendMessage(new TranslationTextComponent("lootr.message.refresh_in", refreshValue / 20).setStyle(Style.EMPTY.withColor(TextFormatting.BLUE).withBold(true)), Util.NIL_UUID);
        } else if (refreshValue == -1) {
          if (ConfigManager.isRefreshing(world, tile)) {
            DataStorage.setRefreshing(player.getUUID(), tileId, world.getGameTime() + ConfigManager.REFRESH_VALUE.get());
            player.sendMessage(new TranslationTextComponent("lootr.message.refresh_start", ConfigManager.REFRESH_VALUE.get() / 20).setStyle(Style.EMPTY.withColor(TextFormatting.BLUE).withBold(true)), Util.NIL_UUID);
          }
        }
      }
      INamedContainerProvider provider = DataStorage.getInventory(world, tile.getTileId(), stacks, (ServerPlayerEntity) player, pos, tile);
      if (!DataStorage.isScored(player.getUUID(), ((ILootTile) te).getTileId())) {
        player.awardStat(ModStats.LOOTED_STAT);
        ModAdvancements.SCORE_PREDICATE.trigger((ServerPlayerEntity) player, null);
        DataStorage.score(player.getUUID(), ((ILootTile) te).getTileId());
      }
      player.openMenu(provider);
      PiglinTasks.angerNearbyPiglins(player, true);
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
