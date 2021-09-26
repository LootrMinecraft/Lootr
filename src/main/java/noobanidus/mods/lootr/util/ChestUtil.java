package noobanidus.mods.lootr.util;

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
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.ILootTile;
import noobanidus.mods.lootr.data.NewChestData;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.init.ModStats;
import noobanidus.mods.lootr.networking.OpenCart;
import noobanidus.mods.lootr.networking.PacketHandler;
import noobanidus.mods.lootr.tiles.SpecialLootInventoryTile;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

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
    // TODO: CloseCart packet
    OpenCart open = new OpenCart(cart.getId());
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
      if (block instanceof BarrelBlock) {
        Lootr.BARREL_PREDICATE.trigger((ServerPlayerEntity) player, null);
      } else if (block instanceof ChestBlock) {
        Lootr.CHEST_PREDICATE.trigger((ServerPlayerEntity) player, null);
      }
      INamedContainerProvider provider = NewChestData.getInventory(world, ((ILootTile) te).getTileId(), pos, (ServerPlayerEntity) player, (LockableLootTileEntity) te, ((ILootTile) te)::fillWithLoot);
      if (!((ILootTile) te).getOpeners().contains(player.getUUID())) {
        player.awardStat(ModStats.LOOTED_STAT);
        Lootr.SCORE_PREDICATE.trigger((ServerPlayerEntity) player, null);
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
        Lootr.CART_PREDICATE.trigger((ServerPlayerEntity) player, null);
        if (!cart.getOpeners().contains(player.getUUID())) {
          cart.addOpener(player);
          player.awardStat(ModStats.LOOTED_STAT);
          Lootr.SCORE_PREDICATE.trigger((ServerPlayerEntity) player, null);
        }
        INamedContainerProvider provider = NewChestData.getInventory(world, cart, (ServerPlayerEntity) player, cart::addLoot);
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
    if (te instanceof SpecialLootInventoryTile) {
      Lootr.CHEST_PREDICATE.trigger((ServerPlayerEntity) player, null);
      SpecialLootInventoryTile tile = (SpecialLootInventoryTile) te;
      NonNullList<ItemStack> stacks = null;
      if (tile.getCustomInventory() != null) {
        stacks = copyItemList(tile.getCustomInventory());
      }
      INamedContainerProvider provider = NewChestData.getInventory(world, tile.getTileId(), stacks, (ServerPlayerEntity) player, pos, tile);
      if (!((ILootTile) te).getOpeners().contains(player.getUUID())) {
        player.awardStat(ModStats.LOOTED_STAT);
        Lootr.SCORE_PREDICATE.trigger((ServerPlayerEntity) player, null);
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
