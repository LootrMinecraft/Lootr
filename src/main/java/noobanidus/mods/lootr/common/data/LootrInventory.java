package noobanidus.mods.lootr.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.common.api.MenuBuilder;
import noobanidus.mods.lootr.common.api.data.ILootrInfo;
import noobanidus.mods.lootr.common.api.data.ILootrSavedData;
import noobanidus.mods.lootr.common.api.data.inventory.ILootrInventory;
import org.jetbrains.annotations.Nullable;

public class LootrInventory implements ILootrInventory {
  private final NonNullList<ItemStack> contents;
  private ILootrSavedData info;
  private MenuBuilder menuBuilder = null;

  public LootrInventory(ILootrSavedData info, NonNullList<ItemStack> contents) {
    this.info = info;
    if (!contents.isEmpty()) {
      this.contents = contents;
    } else {
      this.contents = info.buildInitialInventory();
    }
  }

  public void setMenuBuilder(MenuBuilder builder) {
    this.menuBuilder = builder;
  }

  @Override
  public CompoundTag saveToTag(HolderLookup.Provider provider) {
    CompoundTag result = new CompoundTag();
    ContainerHelper.saveAllItems(result, this.contents, provider);
    return result;
  }

  @Override
  public int getContainerSize() {
    return info.getInfoContainerSize();
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
    info.markChanged();
  }

  @Override
  public boolean stillValid(Player player) {
    if (!player.level().dimension().equals(info.getInfoDimension())) {
      return false;
    }

    Container container = info.getInfoContainer();
    if (container == null) {
      return false;
    }

    if (container instanceof BlockEntity blockEntity) {
      return Container.stillValidBlockEntity(blockEntity, player);
    }

    if (container instanceof ContainerEntity containerEntity) {
      return containerEntity.isChestVehicleStillValid(player);
    }

    return false;
  }

  @Override
  public void clearContent() {
    contents.clear();
    setChanged();
  }

  @Override
  public ILootrInfo getInfo() {
    return this.info;
  }

  @Override
  public void setInfo(ILootrSavedData info) {
    this.info = info;
  }

  @Override
  public Component getDisplayName() {
    return info.getInfoDisplayName();
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
    Container container = info.getInfoContainer();
    if (container != null) {
      container.startOpen(player);
    }
  }

  @Override
  public void stopOpen(Player player) {
    setChanged();
    Container container = info.getInfoContainer();
    if (container != null) {
      container.stopOpen(player);
    }
  }

  @Override
  public NonNullList<ItemStack> getInventoryContents() {
    return this.contents;
  }


}
