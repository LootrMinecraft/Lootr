package noobanidus.mods.lootr.common.api.data;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.NBTConstants;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class SimpleLootrInstance {
  private final NonNullList<ItemStack> items;
  private final Set<UUID> clientOpeners = new ObjectOpenHashSet<>();
  protected UUID infoId = null;
  protected boolean hasBeenOpened = false;
  private String cachedId;
  protected boolean clientOpened = false;
  private boolean savingToItem = false;

  private final Supplier<Set<UUID>> visualOpenersSupplier;

  // What in the recursive, self-referential is this
  public SimpleLootrInstance(Supplier<Set<UUID>> visualOpenersSupplier, int size) {
    this.items = NonNullList.withSize(size, ItemStack.EMPTY);
    this.visualOpenersSupplier = visualOpenersSupplier;
  }

  public NonNullList<ItemStack> getItems() {
    return items;
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
    return items.size();
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
    if (compound.hasUUID(NBTConstants.ENTITY_ID)) {
      this.infoId = compound.getUUID(NBTConstants.ENTITY_ID);
    }
    if (compound.contains(NBTConstants.HAS_BEEN_OPENED, Tag.TAG_BYTE)) {
      this.hasBeenOpened = compound.getBoolean(NBTConstants.HAS_BEEN_OPENED);
    }
    if (this.infoId == null) {
      getInfoUUID();
    }
    clientOpeners.clear();
    if (compound.contains(NBTConstants.OPENERS)) {
      ListTag list = compound.getList(NBTConstants.OPENERS, CompoundTag.TAG_INT_ARRAY);
      for (Tag thisTag : list) {
        clientOpeners.add(NbtUtils.loadUUID(thisTag));
      }
    }
  }

  public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider, boolean isClientSide) {
    if (!LootrAPI.shouldDiscard() && !savingToItem) {
      compound.putUUID(NBTConstants.ENTITY_ID, getInfoUUID());
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
  }

  public void fillUpdateTag(CompoundTag result, HolderLookup.Provider provider, boolean isClientSide) {
    saveAdditional(result, provider, isClientSide);
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
  }
}
