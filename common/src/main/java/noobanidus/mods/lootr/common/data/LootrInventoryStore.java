package noobanidus.mods.lootr.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerPlayer;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.*;
import noobanidus.mods.lootr.common.api.data.base.BaseLootrData;
import noobanidus.mods.lootr.common.api.interfaces.inventory.ILootrInventory;
import noobanidus.mods.lootr.common.api.filler.ILootFiller;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

// TODO: Pull quite a bit of stuff up
public class LootrInventoryStore implements ILootrInventoryStore {
  @SuppressWarnings("unchecked")
  public static final Codec<LootrInventoryStore> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.BOOL.fieldOf("hasInventories").forGetter(LootrInventoryStore::hasInventories),
      ILootrData.CODEC.fieldOf("info").forGetter(data -> data.info),
      Codec.unboundedMap(Codec.STRING.xmap(UUID::fromString, UUID::toString), (Codec<LootrInventory>)(Object) ILootrInventory.CODEC).fieldOf("inventories").forGetter(data -> data.inventories),
      UUIDUtil.CODEC_LINKED_SET.fieldOf("openers").forGetter(data -> data.openers),
      UUIDUtil.CODEC_LINKED_SET.fieldOf("actualOpeners").forGetter(data -> data.actualOpeners),
      Codec.LONG.fieldOf("decayTime").forGetter(data -> data.decayTime),
      Codec.LONG.fieldOf("refreshTime").forGetter(data -> data.refreshTime)
  ).apply(instance, LootrInventoryStore::new));

  private long decayTime = -1;
  private long refreshTime = -1;

  private boolean hasInventories;
  private ILootrData info;
  private final Map<UUID, LootrInventory> inventories = new HashMap<>();
  private final Set<UUID> openers = new ObjectOpenHashSet<>();
  private final Set<UUID> actualOpeners = new ObjectOpenHashSet<>();

  private ILootrSection section;

  public LootrInventoryStore(ILootrData info) {
    this(info, false);
  }

  public LootrInventoryStore(ILootrData info, boolean noCopy) {
    if (noCopy) {
      this.info = info;
    } else {
      this.info = BaseLootrData.copy(info);
    }
  }

  private LootrInventoryStore(boolean hasInventories, ILootrData info, Map<UUID, LootrInventory> map, Set<UUID> openers, Set<UUID> actualOpeners, long decayTime, long refreshTime) {
    this.hasInventories = hasInventories;
    this.info = info;
    this.inventories.putAll(map);
    for (var inv : this.inventories.values()) {
      inv.setLootrSavedData(this);
    }
    this.openers.addAll(openers);
    this.actualOpeners.addAll(actualOpeners);
    this.decayTime = decayTime;
    this.refreshTime = refreshTime;
  }

  @Override
  public ILootrData getData() {
    return info;
  }

  @Override
  public Set<UUID> getVisualOpeners() {
    return openers;
  }

  @Override
  public boolean addVisualOpener(UUID uuid) {
    boolean result = ILootrInventoryStore.super.addVisualOpener(uuid);
    if (result) {
      markInstanceChanged();
    }
    return result;
  }

  @Override
  public boolean removeVisualOpener(UUID uuid) {
    boolean result = ILootrInventoryStore.super.removeVisualOpener(uuid);
    if (result) {
      markInstanceChanged();
    }
    return result;
  }

  @Override
  public boolean addActualOpener(UUID uuid) {
    boolean result = ILootrInventoryStore.super.addActualOpener(uuid);
    if (result) {
      markInstanceChanged();
    }
    return result;
  }

  private void removeOpener (UUID uuid) {
    Set<UUID> visualOpeners = getVisualOpeners();
    if (visualOpeners != null) {
      if (visualOpeners.remove(uuid)) {
        markInstanceChanged();
      }
    }
  }

  @Override
  public Set<UUID> getActualOpeners() {
    return actualOpeners;
  }

  @Override
  public void markInstanceChanged() {
    markSectionChanged();
  }

  @Override
  public void markSectionChanged() {
    if (this.section != null) {
      this.section.markSectionChanged();
    } else {
      LootrAPI.LOG.error("Attempted to mark section changed for inventory store without section {}", this.getData().getDataId());
    }
  }

  @Override
  @Nullable
  public LootrInventory getInventory(UUID id) {
    LootrInventory inventory = inventories.get(id);
    if (inventory != null) {
      inventory.setInventoryStore(this);
    }
    return inventory;
  }

  @Override
  public LootrInventory createInventory(ILootrContainerInstance provider, ServerPlayer player, ILootFiller filler) {
    if (provider.canPlayerOpen(player)) {
      LootrInventory result = new LootrInventory(provider.buildInitialInventory());
      result.setLootrSavedData(this);
      if (!LootrAPI.isFakePlayer(player)) {
        filler.unpackLootTable(provider, player, result);
      }
      inventories.put(player.getUUID(), result);
      hasInventories = true;
      markInstanceChanged();
      return result;
    } else {
      provider.informPlayerCannotOpen(player);
      return null;
    }
  }

  @Override
  public void setSection(ILootrSection section) {
    this.section = section;
  }

  @Override
  public boolean isRefreshing() {
    return refreshTime != -1;
  }

  @Override
  public boolean isDecaying() {
    return decayTime != -1;
  }

  @Override
  public boolean isDecayed() {
    if (decayTime == -1) {
      return false;
    }

    return LootrAPI.getGameTime() >= decayTime;
  }

  @Override
  public boolean isRefreshed () {
    if (refreshTime == -1) {
      return false;
    }

    return LootrAPI.getGameTime() >= refreshTime;
  }

  @Override
  public void beginDecay() {
    decayTime = LootrAPI.getGameTime() + LootrAPI.getDecayValue();
    markInstanceChanged();
  }

  @Override
  public void beginRefresh () {
    refreshTime = LootrAPI.getGameTime() + LootrAPI.getRefreshValue();
    markInstanceChanged();
  }

  @Override
  public int remainingDecayTime() {
    if (decayTime == -1) {
      return -1;
    }

    return (int) (decayTime - LootrAPI.getGameTime());
  }

  @Override
  public int remainingRefreshTime() {
    if (refreshTime == -1) {
      return -1;
    }

    return (int) (refreshTime - LootrAPI.getGameTime());
  }

  @Override
  public void update(ILootrData info) {
    BaseLootrData infoCopy = BaseLootrData.copy(info);
    if (!infoCopy.equals(this.info)) {
      markInstanceChanged();
      this.info = info;
    }
  }

  @Override
  public void performRefresh() {
    var openers2 = getVisualOpeners();
    if (openers2 != null) {
      openers2.clear();
    }
    inventories.clear();
    hasInventories = false;
    refreshTime = -1;
    markInstanceChanged();
  }

  public boolean hasInventories() {
    return hasInventories;
  }

  public boolean canBeCulled () {
    if (!inventories.isEmpty()) {
      return false;
    }

    return !hasInventories();
  }

  @Override
  public boolean clearInventories(UUID id) {
    if (inventories.remove(id) != null) {
      removeOpener(id);
      markInstanceChanged();
      return true;
    }

    return false;
  }
}
