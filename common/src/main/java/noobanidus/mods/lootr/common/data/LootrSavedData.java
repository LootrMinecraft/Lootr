package noobanidus.mods.lootr.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import noobanidus.mods.lootr.common.api.ILootrType;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.*;
import noobanidus.mods.lootr.common.api.data.inventory.ILootrInventory;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class LootrSavedData extends SavedData implements ILootrSavedData {
  public static final Codec<LootrSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.BOOL.fieldOf("hasBeenOpened").forGetter(LootrSavedData::hasBeenOpened),
      ILootrInfo.CODEC.fieldOf("info").forGetter(data -> data.info),
      Codec.unboundedMap(Codec.STRING.xmap(UUID::fromString, UUID::toString), (Codec<LootrInventory>)(Object) ILootrInventory.CODEC).fieldOf("inventories").forGetter(data -> data.inventories),
      UUIDUtil.CODEC_LINKED_SET.fieldOf("openers").forGetter(data -> data.openers),
      UUIDUtil.CODEC_LINKED_SET.fieldOf("actualOpeners").forGetter(data -> data.actualOpeners)
  ).apply(instance, LootrSavedData::new));

  private boolean hasBeenOpened;
  private ILootrInfo info;
  private final Map<UUID, LootrInventory> inventories = new HashMap<>();
  private final Set<UUID> openers = new ObjectLinkedOpenHashSet<>();
  private final Set<UUID> actualOpeners = new ObjectLinkedOpenHashSet<>();

  protected LootrSavedData(ILootrInfo info) {
    this(info, false);
  }

  protected LootrSavedData(ILootrInfo info, boolean noCopy) {
    if (noCopy) {
      this.info = info;
    } else {
      this.info = BaseLootrInfo.copy(info);
    }
  }

  private LootrSavedData(boolean hasBeenOpened, ILootrInfo info, Map<UUID, LootrInventory> map, Set<UUID> openers, Set<UUID> actualOpeners) {
    this.hasBeenOpened = hasBeenOpened;
    this.info = info;
    this.inventories.putAll(map);
    for (var inv : this.inventories.values()) {
      inv.setLootrSavedData(this);
    }
    this.openers.addAll(openers);
    this.actualOpeners.addAll(actualOpeners);
  }

  public static Supplier<LootrSavedData> fromInfo(ILootrInfo info) {
    return () -> new LootrSavedData(info);
  }

  @Override
  public ILootrInfo getRedirect() {
    return info;
  }

  @Override
  public Set<UUID> getVisualOpeners() {
    return openers;
  }

  @Override
  public boolean addVisualOpener(UUID uuid) {
    boolean result = ILootrSavedData.super.addVisualOpener(uuid);
    if (result) {
      setDirty();
    }
    return result;
  }

  @Override
  public boolean removeVisualOpener(UUID uuid) {
    boolean result = ILootrSavedData.super.removeVisualOpener(uuid);
    if (result) {
      setDirty();
    }
    return result;
  }

  @Override
  public boolean addActualOpener(UUID uuid) {
    boolean result = ILootrSavedData.super.addActualOpener(uuid);
    if (result) {
      setDirty();
    }
    return result;
  }

  private void removeOpener (UUID uuid) {
    Set<UUID> visualOpeners = getVisualOpeners();
    if (visualOpeners != null) {
      if (visualOpeners.remove(uuid)) {
        setDirty();
      }
    }
  }

  @Override
  public Set<UUID> getActualOpeners() {
    return actualOpeners;
  }

  @Override
  public void markChanged() {
    setDirty();
  }

  @Override
  public void markDataChanged() {
    markChanged();
  }

  @Override
  @Nullable
  public LootrInventory getInventory(UUID id) {
    LootrInventory inventory = inventories.get(id);
    if (inventory != null) {
      inventory.setInfo(this);
    }
    return inventory;
  }

  @Override
  public LootrInventory createInventory(ILootrInfoProvider provider, ServerPlayer player, LootFiller filler) {
    if (provider.canPlayerOpen(player)) {
      LootrInventory result = new LootrInventory(provider.buildInitialInventory());
      result.setLootrSavedData(this);
      if (!LootrAPI.isFakePlayer(player)) {
        filler.unpackLootTable(provider, player, result);
      }
      inventories.put(player.getUUID(), result);
      hasBeenOpened = true;
      setDirty();
      return result;
    } else {
      provider.informPlayerCannotOpen(player);
      return null;
    }
  }

  @Override
  public void update(ILootrInfo info) {
    BaseLootrInfo infoCopy = BaseLootrInfo.copy(info);
    if (!infoCopy.equals(this.info)) {
      markChanged();
      this.info = info;
    }
  }

  @Override
  public void refresh() {
    inventories.clear();
    hasBeenOpened = false;
    markChanged();
  }

  // TODO: Is there disparity between the usage of "hasBeenOpened" in ILootrSavedData
  // versus "hasBeenOpened" in ILootrInfoProvider? There's no synchronization between them.
  // The main reason it exists in the provider is to prevent tick events from causing
  // data to be created and then saved, which was apparently causing TPS lag for someone.
  // It's also used to ignore specific saved data files when clearing via command.

  // This is triggered in createInventory and reset in refresh.
  @Override
  public boolean hasBeenOpened() {
    return hasBeenOpened;
  }

  public boolean canBeCulled () {
    if (!inventories.isEmpty()) {
      return false;
    }

    return !hasBeenOpened();
  }

  @Override
  public boolean isPhysicallyOpen() {
    return false;
  }

  @Override
  public boolean clearInventories(UUID id) {
    if (inventories.remove(id) != null) {
      removeOpener(id);
      setDirty();
      return true;
    }

    return false;
  }
}
