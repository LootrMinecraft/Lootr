package noobanidus.mods.lootr.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import noobanidus.mods.lootr.api.data.LootFiller;
import noobanidus.mods.lootr.api.data.BaseLootrInfo;
import noobanidus.mods.lootr.api.data.ILootrInfo;
import noobanidus.mods.lootr.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.api.data.ILootrSavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class LootrSavedData extends SavedData implements ILootrSavedData {
  private final ILootrInfo info;
  private final Map<UUID, LootrInventory> inventories = new HashMap<>();

  protected LootrSavedData(ILootrInfo info) {
    this.info = BaseLootrInfo.copy(info);
  }

  protected LootrSavedData(ILootrInfo info, boolean noCopy) {
    this.info = info;
  }

  public static Supplier<LootrSavedData> fromInfo(ILootrInfo info) {
    return () -> new LootrSavedData(info);
  }

  public static LootrSavedData load(CompoundTag compound, HolderLookup.Provider provider) {
    ILootrInfo info = ILootrInfo.loadInfoFromTag(compound, provider);
    LootrSavedData data = new LootrSavedData(info, true);
    data.inventories.clear();

    ListTag compounds = compound.getList("inventories", Tag.TAG_COMPOUND);

    for (int i = 0; i < compounds.size(); i++) {
      CompoundTag thisTag = compounds.getCompound(i);
      CompoundTag itemTag = thisTag.getCompound("chest");
      NonNullList<ItemStack> items = info.buildInitialInventory();
      ContainerHelper.loadAllItems(itemTag, items, provider);
      UUID uuid = thisTag.getUUID("uuid");
      data.inventories.put(uuid, new LootrInventory(data, items));
    }
    return data;
  }

  @Override
  public ILootrInfo getRedirect() {
    return info;
  }

  public boolean clearInventory(UUID uuid) {
    return inventories.remove(uuid) != null;
  }

  @Override
  public void markChanged() {
    setDirty();
  }

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

    LootrInventory result = new LootrInventory(this, provider.buildInitialInventory());
    filler.unpackLootTable(provider, player, result);
    inventories.put(player.getUUID(), result);
    setDirty();
    return result;
  }

  @Override
  public CompoundTag save(CompoundTag compound, HolderLookup.Provider provider) {
    this.info.saveInfoToTag(compound, provider);

    ListTag compounds = new ListTag();
    for (Map.Entry<UUID, LootrInventory> entry : inventories.entrySet()) {
      CompoundTag thisTag = new CompoundTag();
      thisTag.putUUID("uuid", entry.getKey());
      thisTag.put("chest", entry.getValue().saveToTag(provider));
      compounds.add(thisTag);
    }
    compound.put("inventories", compounds);

    return compound;
  }

  @Override
  public void clearInventories() {
    inventories.clear();
  }

  @Override
  public void save(File pFile, HolderLookup.Provider provider) {
    if (isDirty()) {
      pFile.getParentFile().mkdirs();
    }
    super.save(pFile, provider);
  }

  public class CustomInventoryFiller implements LootFiller {
    @Override
    public void unpackLootTable(@NotNull ILootrInfoProvider provider, @NotNull Player player, Container inventory) {
      if (provider.getInfoReferenceInventory() == null) {
        return;
      }

    }
  }
}
