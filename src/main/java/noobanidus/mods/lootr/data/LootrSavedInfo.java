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
import noobanidus.mods.lootr.api.LootFiller;
import noobanidus.mods.lootr.api.info.BaseLootrInfo;
import noobanidus.mods.lootr.api.info.ILootrInfo;
import noobanidus.mods.lootr.api.info.ILootrInfoProvider;
import noobanidus.mods.lootr.api.info.ILootrSavedInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class LootrSavedInfo extends SavedData implements ILootrSavedInfo {
  private final ILootrInfo info;
  private final Map<UUID, LootrInventory> inventories = new HashMap<>();

  protected LootrSavedInfo(ILootrInfo info) {
    this.info = BaseLootrInfo.copy(info);
  }

  protected LootrSavedInfo(ILootrInfo info, boolean noCopy) {
    this.info = info;
  }

  public static Supplier<LootrSavedInfo> fromInfo(ILootrInfo info) {
    return () -> new LootrSavedInfo(info);
  }

/*  public static Supplier<ChestData> ref_id(ResourceKey<Level> dimension, BlockPos pos, UUID id, NonNullList<ItemStack> base) {
    if (id == null) {
      throw new IllegalArgumentException("Can't create ChestData for custom container in dimension '" + dimension + "' at '" + pos + "' with a null id.");
    }
    return () -> {
      ChestData data = new ChestData(ID(id));
      data.pos = pos;
      data.dimension = dimension;
      data.uuid = id;
      data.reference = base;
      data.custom = true;
      data.entity = false;
      if (data.reference == null) {
        throw new IllegalArgumentException("Inventory reference cannot be null.");
      }
      return data;
    };
  }*/

/*  public static Supplier<ChestData> id(ResourceKey<Level> dimension, BlockPos pos, UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Can't create ChestData for container in dimension '" + dimension + "' at '" + pos + "' with a null id.");
    }
    return () -> {
      ChestData data = new ChestData(ID(id));
      data.pos = pos;
      data.dimension = dimension;
      data.uuid = id;
      data.reference = null;
      data.custom = false;
      data.entity = false;
      return data;
    };
  }*/

/*  public static Supplier<ChestData> entity(ResourceKey<Level> dimension, BlockPos pos, UUID entityId) {
    if (entityId == null) {
      throw new IllegalArgumentException("Can't create ChestData for minecart in dimension '" + dimension + "' at '" + pos + "' with a null entityId.");
    }
    return () -> {
      ChestData data = new ChestData(ID(entityId));
      data.pos = pos;
      data.dimension = dimension;
      data.uuid = entityId;
      data.entity = true;
      data.reference = null;
      data.custom = false;
      return data;
    };
  }*/

/*
  public static BiFunction<CompoundTag, HolderLookup.Provider, ChestData> loadWrapper(UUID id, ResourceKey<Level> dimension, BlockPos position) {
    return (tag, provider) -> {
      ChestData result = ChestData.load(tag, provider);
      result.key = ID(id);
      result.dimension = dimension;
      result.pos = position;
      return result;
    };
  }
*/

/*  public static ChestData unwrap(ChestData data, UUID id, ResourceKey<Level> dimension, BlockPos position, int size) {
    data.key = ID(id);
    data.dimension = dimension;
    data.pos = position;
    data.setSize(size);
    return data;
  }*/

  public static LootrSavedInfo load(CompoundTag compound, HolderLookup.Provider provider) {
    ILootrInfo info = ILootrInfo.loadInfoFromTag(compound, provider);
    LootrSavedInfo data = new LootrSavedInfo(info, true);
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

  public CustomInventoryFiller customInventory() {
    return new CustomInventoryFiller();
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
      for (int i = 0; i < provider.getInfoReferenceInventory().size(); i++) {
        inventory.setItem(i, provider.getInfoReferenceInventory().get(i).copy());
      }
    }
  }
}
