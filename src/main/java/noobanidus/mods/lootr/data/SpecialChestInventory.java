package noobanidus.mods.lootr.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.MenuBuilder;
import noobanidus.mods.lootr.api.inventory.ILootrInventory;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SuppressWarnings("NullableProblems")
public class SpecialChestInventory implements ILootrInventory {
  private final Component name;
  private final ChestData newChestData;
  private NonNullList<ItemStack> contents;
  private MenuBuilder menuBuilder = null;

  public SpecialChestInventory(ChestData newChestData, NonNullList<ItemStack> contents, Component name) {
    this.newChestData = newChestData;
    if (!contents.isEmpty()) {
      this.contents = contents;
    } else {
      this.contents = NonNullList.withSize(newChestData.getSize(), ItemStack.EMPTY);
    }
    this.name = name;
  }

  public SpecialChestInventory(ChestData newChestData, CompoundTag items, String componentAsJSON, HolderLookup.Provider provider) {
    this.newChestData = newChestData;
    this.name = Component.Serializer.fromJson(componentAsJSON, provider);
    this.contents = NonNullList.withSize(newChestData.getSize(), ItemStack.EMPTY);
    ContainerHelper.loadAllItems(items, this.contents, provider);
  }

  public void setMenuBuilder(MenuBuilder builder) {
    this.menuBuilder = builder;
  }


  @Override
  @Nullable
  public BaseContainerBlockEntity getBlockEntity(Level level) {
    if (level == null || level.isClientSide() || newChestData.getPos() == null) {
      return null;
    }

    BlockEntity te = level.getBlockEntity(newChestData.getPos());
    if (te instanceof BaseContainerBlockEntity be) {
      return be;
    }

    return null;
  }

  @Override
  @Nullable
  public LootrChestMinecartEntity getEntity(Level world) {
    if (world == null || world.isClientSide() || newChestData.getEntityId() == null) {
      return null;
    }

    if (!(world instanceof ServerLevel serverWorld)) {
      return null;
    }

    Entity entity = serverWorld.getEntity(newChestData.getEntityId());
    if (entity instanceof LootrChestMinecartEntity) {
      return (LootrChestMinecartEntity) entity;
    }

    return null;
  }

  @org.jetbrains.annotations.Nullable
  @Override
  public BlockPos getPos() {
    return newChestData.getPos();
  }

  @Override
  public int getContainerSize() {
    // This should always synchronize to newChestData.size()
    return this.contents.size();
  }

  @Override
  public boolean isEmpty() {
    for (ItemStack itemstack : this.contents) {
      if (!itemstack.isEmpty()) {
        return false;
      }
    }

    return true;
  }

  @Override
  public ItemStack getItem(int index) {
    return contents.get(index);
  }

  @Override
  public ItemStack removeItem(int index, int count) {
    ItemStack itemstack = ContainerHelper.removeItem(this.contents, index, count);
    if (!itemstack.isEmpty()) {
      this.setChanged();
    }

    return itemstack;
  }

  @Override
  public ItemStack removeItemNoUpdate(int index) {
    ItemStack result = ContainerHelper.takeItem(contents, index);
    if (!result.isEmpty()) {
      this.setChanged();
    }

    return result;
  }

  @Override
  public void setItem(int index, ItemStack stack) {
    this.contents.set(index, stack);
    if (stack.getCount() > this.getMaxStackSize()) {
      stack.setCount(this.getMaxStackSize());
    }

    this.setChanged();
  }

  @Override
  public void setChanged() {
    newChestData.setDirty();
  }

  @Override
  public boolean stillValid(Player player) {
    if (!player.level().dimension().equals(newChestData.getDimension())) {
      return false;
    }
    if (newChestData.isEntity()) {
      if (newChestData.getEntityId() == null) {
        return false;
      }
      if (player.level() instanceof ServerLevel serverLevel) {
        Entity entity = serverLevel.getEntity(newChestData.getEntityId());
        if (entity instanceof ContainerEntity container) {
          return container.isChestVehicleStillValid(player);
        } else {
          return false;
        }
      } else {
        return true; // I'm not sure if this happens on the client or not.
      }
    } else {
      BlockEntity be = player.level().getBlockEntity(newChestData.getPos());
      if (be == null) {
        return false;
      }
      return Container.stillValidBlockEntity(be, player);
    }
  }

  @Override
  public void clearContent() {
    contents.clear();
    setChanged();
  }

  @Override
  public Component getDisplayName() {
    return name;
  }

  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
    if (menuBuilder != null) {
      return menuBuilder.build(id, inventory, this, getContainerSize() / 9);
    }
    return switch (getContainerSize()) {
      case 9 -> new ChestMenu(MenuType.GENERIC_9x1, id, inventory, this, 1);
      case 18 -> new ChestMenu(MenuType.GENERIC_9x2, id, inventory, this, 2);
      case 36 -> new ChestMenu(MenuType.GENERIC_9x4, id, inventory, this, 4);
      case 45 -> new ChestMenu(MenuType.GENERIC_9x5, id, inventory, this, 5);
      case 54 -> ChestMenu.sixRows(id, inventory, this);
      default -> ChestMenu.threeRows(id, inventory, this);
    };
  }

  @Override
  public void startOpen(Player player) {
    Level world = player.level();
    BaseContainerBlockEntity tile = getBlockEntity(world);
    if (tile != null) {
      tile.startOpen(player);
    }
    if (newChestData.getEntityId() != null) {
      LootrChestMinecartEntity entity = getEntity(world);
      if (entity != null) {
        entity.startOpen(player);
      }
    }
  }

  @Override
  public void stopOpen(Player player) {
    setChanged();
    Level world = player.level();
    if (newChestData.getPos() != null) {
      BaseContainerBlockEntity tile = getBlockEntity(world);
      if (tile != null) {
        tile.stopOpen(player);
      }
    }
    if (newChestData.getEntityId() != null) {
      LootrChestMinecartEntity entity = getEntity(world);
      if (entity != null) {
        entity.stopOpen(player);
      }
    }
  }

  @Nullable
  public UUID getTileId() {
    if (newChestData == null) {
      return null;
    }
    return newChestData.getTileId();
  }

  public CompoundTag writeItems(HolderLookup.Provider provider) {
    CompoundTag result = new CompoundTag();
    return ContainerHelper.saveAllItems(result, this.contents, provider);
  }

  public String writeName(HolderLookup.Provider provider) {
    return Component.Serializer.toJson(this.name, provider);
  }

  @Override
  public NonNullList<ItemStack> getInventoryContents() {
    return this.contents;
  }

  public void resizeInventory(int newSize) {
    if (newSize > this.contents.size()) {
      NonNullList<ItemStack> oldContents = this.contents;
      this.contents = NonNullList.withSize(newSize, ItemStack.EMPTY);
      for (int i = 0; i < oldContents.size(); i++) {
        this.contents.set(i, oldContents.get(i));
      }
      // TODO: Remove this once we confirm it works
      LootrAPI.LOG.info("Resized inventory with key '" + newChestData.getKey() + "' in dimension '" + newChestData.getDimension() + "' at location '" + newChestData.getPos() + "' from " + oldContents.size() + " slots to " + newSize + " slots.");
    } else if (newSize < this.contents.size()) {
      throw new IllegalArgumentException("Cannot resize inventory associated with '" + newChestData.getKey() + "' in dimension '" + newChestData.getDimension() + "' at location '" + newChestData.getPos() + "' to a smaller size.");
    }
  }
}