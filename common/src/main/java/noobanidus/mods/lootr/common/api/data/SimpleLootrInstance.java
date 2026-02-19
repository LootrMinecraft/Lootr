package noobanidus.mods.lootr.common.api.data;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.NBTConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class SimpleLootrInstance {
  protected final NonNullList<ItemStack> emptyItemList;
  protected NonNullList<ItemStack> customInventory = null;
  protected boolean isCustomInventory = false;
  protected final Set<UUID> clientOpeners = new ObjectOpenHashSet<>();
  protected UUID infoId = null;
  protected boolean hasBeenOpened = false;
  protected String cachedId;
  protected boolean clientOpened = false;
  protected boolean savingToItem = false;

  protected boolean providesOwnUuid = false;

  protected final Supplier<Set<UUID>> visualOpenersSupplier;

  public SimpleLootrInstance(Supplier<Set<UUID>> visualOpenersSupplier, int size) {
    this.emptyItemList = NonNullList.withSize(size, ItemStack.EMPTY);
    this.visualOpenersSupplier = visualOpenersSupplier;
  }

  public NonNullList<ItemStack> getEmptyItemList() {
    return emptyItemList;
  }

  @Nullable
  public NonNullList<ItemStack> getCustomInventory () {
    return customInventory;
  }

  public boolean isCustomInventory () {
    if (isCustomInventory) {
      return true;
    } else {
      if (customInventory != null && !customInventory.isEmpty()) {
        return true;
      }
    }

    return false;
  }

  public void setCustomInventory (NonNullList<ItemStack> customInventory) {
    NonNullList<ItemStack> copy = NonNullList.withSize(customInventory.size(), ItemStack.EMPTY);
    for (int i = 0; i < customInventory.size(); i++) {
      copy.set(i, customInventory.get(i).copy());
    }
    this.customInventory = copy;
    this.isCustomInventory = true;
  }

  public Set<UUID> getClientOpeners() {
    return clientOpeners;
  }

  public boolean isClientOpened() {
    return clientOpened;
  }

  public void setClientOpened(boolean opened) {
    this.clientOpened = opened;
  }

  public @NotNull UUID getInfoUUID() {
    if (providesOwnUuid) {
      throw new IllegalStateException("This instance provides its own UUID but hasn't overriden `getInfoUUID`: " + this);
    }
    if (this.infoId == null) {
      this.infoId = UUID.randomUUID();
    }
    return this.infoId;
  }

  public String getInfoKey() {
    if (cachedId == null) {
      cachedId = ILootrInfo.generateInfoKey(getInfoUUID());
    }
    return cachedId;
  }

  public boolean hasBeenOpened() {
    return hasBeenOpened;
  }

  public int getInfoContainerSize() {
    return emptyItemList.size();
  }

  public void setHasBeenOpened() {
    this.hasBeenOpened = true;
  }

  public boolean isSavingToItem() {
    return savingToItem;
  }

  public void setSavingToItem(boolean saving) {
    this.savingToItem = saving;
  }

  public void loadAdditional(CompoundTag compound, HolderLookup.Provider provder) {
    if (!providesOwnUuid) {
      if (compound.hasUUID(NBTConstants.INSTANCE_ID)) {
        this.infoId = compound.getUUID(NBTConstants.INSTANCE_ID);
      }
    }
    if (compound.contains(NBTConstants.HAS_BEEN_OPENED, Tag.TAG_BYTE)) {
      this.hasBeenOpened = compound.getBoolean(NBTConstants.HAS_BEEN_OPENED);
    }
    if (this.infoId == null && !providesOwnUuid) {
      getInfoUUID();
    }
    clientOpeners.clear();
    if (compound.contains(NBTConstants.OPENERS)) {
      ListTag list = compound.getList(NBTConstants.OPENERS, CompoundTag.TAG_INT_ARRAY);
      for (Tag thisTag : list) {
        clientOpeners.add(NbtUtils.loadUUID(thisTag));
      }
    }

    if (compound.contains(NBTConstants.CUSTOM_INVENTORY) && compound.contains(NBTConstants.CUSTOM_SIZE)) {
      int size = Math.max(compound.getInt(NBTConstants.CUSTOM_SIZE), getInfoContainerSize());
      this.customInventory = NonNullList.withSize(size, ItemStack.EMPTY);
      ContainerHelper.loadAllItems(compound.getCompound(NBTConstants.CUSTOM_INVENTORY), customInventory, provder);
      this.isCustomInventory = true;
    }
  }

  public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider, boolean isClientSide) {
    if (!LootrAPI.shouldDiscard() && !isSavingToItem() && !providesOwnUuid) {
      compound.putUUID(NBTConstants.INSTANCE_ID, getInfoUUID());
    }
    compound.putBoolean(NBTConstants.HAS_BEEN_OPENED, this.hasBeenOpened);
    if (isClientSide) { // level != null && level.isClientSide()) { ?????? This logic seems inverted.
      if (!clientOpeners.isEmpty()) {
        ListTag list = new ListTag();
        for (UUID opener : clientOpeners) {
          list.add(NbtUtils.createUUID(opener));
        }
        compound.put(NBTConstants.OPENERS, list);
      }
    }

    if (isCustomInventory) {
      if (customInventory != null && !customInventory.isEmpty()) {
        CompoundTag itemTag = new CompoundTag();
        ContainerHelper.saveAllItems(itemTag, customInventory, provider);
        compound.put(NBTConstants.CUSTOM_INVENTORY, itemTag);
        compound.putInt(NBTConstants.CUSTOM_SIZE, customInventory.size());
      }
    }
  }

  public void fillUpdateTag(CompoundTag result, HolderLookup.Provider provider, boolean isClientSide) {
    saveAdditional(result, provider, isClientSide);
    if (!isClientSide) {
      Set<UUID> currentOpeners = visualOpenersSupplier.get();
      if (currentOpeners != null) {
        ListTag list = new ListTag();
        for (UUID opener : Sets.intersection(currentOpeners, LootrAPI.getPlayerIds())) {
          list.add(NbtUtils.createUUID(opener));
        }
        if (!list.isEmpty()) {
          result.put(NBTConstants.OPENERS, list);
        }
      }
    } else {
      LootrAPI.LOG.error("Tried to fillUpdateTag on the client side for SimpleLootrInstance: {}", this);
    }
  }
}
