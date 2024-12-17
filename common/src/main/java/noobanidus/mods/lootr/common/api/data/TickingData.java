package noobanidus.mods.lootr.common.api.data;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;

import java.io.File;
import java.util.UUID;

public class TickingData extends SavedData {
  public static final SavedData.Factory<TickingData> FACTORY = new Factory<>(TickingData::new, TickingData::load, null);
  private final Object2IntMap<UUID> tickMap = new Object2IntOpenHashMap<>();

  public TickingData() {
    tickMap.defaultReturnValue(-1);
  }

  public static TickingData load(CompoundTag pCompound, HolderLookup.Provider provider) {
    TickingData data = new TickingData();
    data.tickMap.clear();
    data.tickMap.defaultReturnValue(-1);
    ListTag decayList = pCompound.getList("result", Tag.TAG_COMPOUND);
    for (int i = 0; i < decayList.size(); i++) {
      CompoundTag thisTag = decayList.getCompound(i);
      data.tickMap.put(thisTag.getUUID("id"), thisTag.getInt("value"));
    }
    return data;
  }

  public boolean isComplete(UUID id) {
    return tickMap.getInt(id) == 0 || tickMap.getInt(id) == 1;
  }

  public int getValue(UUID id) {
    return tickMap.getInt(id);
  }

  public void setValue(UUID id, int decayAmount) {
    if (tickMap.put(id, decayAmount) == -1) {
      setDirty();
    }
  }

  public void remove(UUID id) {
    if (tickMap.removeInt(id) != -1) {
      setDirty();
    }
  }

  public void tick() {
    if (tickMap.isEmpty()) {
      return;
    }

    Object2IntMap<UUID> newMap = new Object2IntOpenHashMap<>();
    newMap.defaultReturnValue(-1);

    boolean changed = false;

    for (Object2IntMap.Entry<UUID> entry : tickMap.object2IntEntrySet()) {
      int value = entry.getIntValue();
      if (value > 0) {
        value--;
        changed = true;
      }
      newMap.put(entry.getKey(), value);
    }

    if (changed) {
      tickMap.clear();
      tickMap.putAll(newMap);
      setDirty();
    }
  }

  @Override
  public CompoundTag save(CompoundTag pCompound, HolderLookup.Provider provider) {
    ListTag decayList = new ListTag();
    for (Object2IntMap.Entry<UUID> entry : tickMap.object2IntEntrySet()) {
      CompoundTag thisTag = new CompoundTag();
      thisTag.putUUID("id", entry.getKey());
      thisTag.putInt("value", entry.getIntValue());
      decayList.add(thisTag);
    }
    pCompound.put("result", decayList);
    return pCompound;
  }
}
