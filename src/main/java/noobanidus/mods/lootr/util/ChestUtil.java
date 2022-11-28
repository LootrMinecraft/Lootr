package noobanidus.mods.lootr.util;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import noobanidus.mods.lootr.api.tile.ILootTile;
import noobanidus.mods.lootr.block.tile.LootrInventoryTileEntity;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.data.DataStorage;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.networking.CloseCart;
import noobanidus.mods.lootr.networking.PacketHandler;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("unused")
public class ChestUtil {
  public static Random random = new Random();
  public static Set<Class<?>> tileClasses = new HashSet<>();

  public static boolean handleLootSneak(Block block, World world, BlockPos pos, EntityPlayer player) {
    if (world.isRemote) {
      return false;
    }
    if (player.isSpectator()) {
      player.closeScreen();
      return false;
    }

    TileEntity te = world.getTileEntity(pos);
    if (te instanceof ILootTile) {
      Set<UUID> openers = ((ILootTile) te).getOpeners();
      openers.remove(player.getUniqueID());
      ((ILootTile) te).updatePacketViaState();
      return true;
    }

    return false;
  }

  public static void handleLootCartSneak(World world, LootrChestMinecartEntity cart, EntityPlayer player) {
    if (world.isRemote) {
      return;
    }

    if (player.isSpectator()) {
      return;
    }

    cart.getOpeners().remove(player.getUniqueID());
    CloseCart open = new CloseCart(cart.getEntityId());
    PacketHandler.sendToAllTracking(cart, open);
  }

  public static boolean handleLootChest(Block block, World world, BlockPos pos, EntityPlayer player) {
    if (world.isRemote) {
      return false;
    }
    if (player.isSpectator()) {
      player.closeScreen();
      return false;
    }
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof ILootTile) {
      UUID tileId = ((ILootTile) te).getTileId();
      if (DataStorage.isDecayed(tileId)) {
        world.destroyBlock(pos, true);
        DataStorage.removeDecayed(tileId);
        player.sendStatusMessage(new TextComponentTranslation("lootr.message.decayed").setStyle(new Style().setColor(TextFormatting.RED).setBold(true)), true);
        return false;
      } else {
        int decayValue = DataStorage.getDecayValue(tileId);
        if (decayValue > 0) {
          player.sendStatusMessage(new TextComponentTranslation("lootr.message.decay_in", decayValue / 20).setStyle(new Style().setColor(TextFormatting.RED).setBold(true)), true);
        } else if (decayValue == -1) {
          if (ConfigManager.isDecaying(world, (ILootTile) te)) {
            DataStorage.setDecaying(tileId, ConfigManager.getDecayValue());
            player.sendStatusMessage(new TextComponentTranslation("lootr.message.decay_start", ConfigManager.getDecayValue() / 20).setStyle(new Style().setColor(TextFormatting.RED).setBold(true)), true);
          }
        }
      }
      
      if (DataStorage.isRefreshed(tileId)) {
        DataStorage.refreshInventory(world, ((ILootTile) te).getTileId(), (EntityPlayerMP) player, pos);
        DataStorage.removeRefreshed(tileId);
        player.sendStatusMessage(new TextComponentTranslation("lootr.message.refreshed").setStyle(new Style().setColor(TextFormatting.BLUE).setBold(true)), true);
      }
      int refreshValue = DataStorage.getRefreshValue(tileId);
      if (refreshValue > 0) {
        player.sendStatusMessage(new TextComponentTranslation("lootr.message.refresh_in", refreshValue / 20).setStyle(new Style().setColor(TextFormatting.BLUE).setBold(true)), true);
      } else if (refreshValue == -1) {
        if (ConfigManager.isRefreshing(world, (ILootTile) te)) {
          DataStorage.setRefreshing(tileId, ConfigManager.getRefreshValue());
          player.sendStatusMessage(new TextComponentTranslation("lootr.message.refresh_start", ConfigManager.getRefreshValue() / 20).setStyle(new Style().setColor(TextFormatting.BLUE).setBold(true)), true);
        }
      }
      IInteractionObject provider = DataStorage.getInventory(world, ((ILootTile) te).getTileId(), pos, (EntityPlayerMP) player, (TileEntityLockableLoot) te, ((ILootTile) te)::fillWithLoot);

      player.displayGUIChest(((IInventory)provider));
      return true;
    } else {
      return false;
    }
  }

  public static void handleLootCart(World world, LootrChestMinecartEntity cart, EntityPlayer player) {
    if (!world.isRemote) {
      if (player.isSpectator()) {
        player.closeScreen(); //openMenu(null);
      } else {
        UUID tileId = cart.getUniqueID();
        if (DataStorage.isDecayed(tileId)) {
          cart.attackEntityFrom(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
          DataStorage.removeDecayed(tileId);
          player.sendStatusMessage(new TextComponentTranslation("lootr.message.decayed").setStyle(new Style().setColor(TextFormatting.RED).setBold(true)), true);
          return;
        } else {
          int decayValue = DataStorage.getDecayValue(tileId);
          if (decayValue > 0) {
            player.sendStatusMessage(new TextComponentTranslation("lootr.message.decay_in", decayValue / 20).setStyle(new Style().setColor(TextFormatting.RED).setBold(true)), true);
          } else if (decayValue == -1) {
            if (ConfigManager.isDecaying(world, cart)) {
              DataStorage.setDecaying(tileId, ConfigManager.getDecayValue());
              player.sendStatusMessage(new TextComponentTranslation("lootr.message.decay_start", ConfigManager.getDecayValue() / 20).setStyle(new Style().setColor(TextFormatting.RED).setBold(true)), true);
            }
          }
        }
        //ModAdvancements.CART_PREDICATE.trigger((EntityPlayerMP) player, cart.getUUID());

        if (!cart.getOpeners().contains(player.getUniqueID())) {
          cart.addOpener(player);
        }

        if (DataStorage.isRefreshed(tileId)) {
          DataStorage.refreshInventory(world, cart, (EntityPlayerMP) player, cart.getPosition());
          DataStorage.removeRefreshed(tileId);
          player.sendStatusMessage(new TextComponentTranslation("lootr.message.refreshed").setStyle(new Style().setColor(TextFormatting.BLUE).setBold(true)), true);
        }
        int refreshValue = DataStorage.getRefreshValue(tileId);
        if (refreshValue > 0) {
          player.sendStatusMessage(new TextComponentTranslation("lootr.message.refresh_in", refreshValue / 20).setStyle(new Style().setColor(TextFormatting.BLUE).setBold(true)), true);
        } else if (refreshValue == -1) {
          if (ConfigManager.isRefreshing(world, cart)) {
            DataStorage.setRefreshing(tileId, ConfigManager.getRefreshValue());
            player.sendStatusMessage(new TextComponentTranslation("lootr.message.refresh_start", ConfigManager.getRefreshValue() / 20).setStyle(new Style().setColor(TextFormatting.BLUE).setBold(true)), true);
          }
        }
        IInteractionObject provider = DataStorage.getInventory(world, cart, (EntityPlayerMP) player, cart::addLoot, cart.getPosition());
        player.displayGui(provider);
      }
    }
  }

  public static boolean handleLootInventory(Block block, World world, BlockPos pos, EntityPlayer player) {
    if (world.isRemote) {
      return false;
    }
    if (player.isSpectator()) {
      player.closeScreen();
      return false;
    }
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof LootrInventoryTileEntity) {
      UUID tileId = ((ILootTile) te).getTileId();
      LootrInventoryTileEntity tile = (LootrInventoryTileEntity)te;
      NonNullList<ItemStack> stacks = null;
      if (tile.getCustomInventory() != null) {
        stacks = copyItemList(tile.getCustomInventory());
      }
      if (DataStorage.isDecayed(tileId)) {
        world.destroyBlock(pos, true);
        DataStorage.removeDecayed(tileId);
        player.sendStatusMessage(new TextComponentTranslation("lootr.message.decayed").setStyle(new Style().setColor(TextFormatting.RED).setBold(true)), true);
        return false;
      } else {
        int decayValue = DataStorage.getDecayValue(tileId);
        if (decayValue > 0) {
          player.sendStatusMessage(new TextComponentTranslation("lootr.message.decay_in", decayValue / 20).setStyle(new Style().setColor(TextFormatting.RED).setBold(true)), true);
        } else if (decayValue == -1) {
          if (ConfigManager.isDecaying(world, (ILootTile) te)) {
            DataStorage.setDecaying(tileId, ConfigManager.getDecayValue());
            player.sendStatusMessage(new TextComponentTranslation("lootr.message.decay_start", ConfigManager.getDecayValue() / 20).setStyle(new Style().setColor(TextFormatting.RED).setBold(true)), true);
          }
        }
      }

      if (DataStorage.isRefreshed(tileId)) {
        DataStorage.refreshInventory(world, ((ILootTile) te).getTileId(), (EntityPlayerMP) player, pos);
        DataStorage.removeRefreshed(tileId);
        player.sendStatusMessage(new TextComponentTranslation("lootr.message.refreshed").setStyle(new Style().setColor(TextFormatting.BLUE).setBold(true)), true);
      }
      int refreshValue = DataStorage.getRefreshValue(tileId);
      if (refreshValue > 0) {
        player.sendStatusMessage(new TextComponentTranslation("lootr.message.refresh_in", refreshValue / 20).setStyle(new Style().setColor(TextFormatting.BLUE).setBold(true)), true);
      } else if (refreshValue == -1) {
        if (ConfigManager.isRefreshing(world, (ILootTile) te)) {
          DataStorage.setRefreshing(tileId, ConfigManager.getRefreshValue());
          player.sendStatusMessage(new TextComponentTranslation("lootr.message.refresh_start", ConfigManager.getRefreshValue() / 20).setStyle(new Style().setColor(TextFormatting.BLUE).setBold(true)), true);
        }
      }
      IInteractionObject provider = DataStorage.getInventory(world, tile.getTileId(), stacks, (EntityPlayerMP) player, pos, tile);
      player.displayGUIChest(((IInventory)provider));
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
