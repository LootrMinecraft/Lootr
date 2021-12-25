package noobanidus.mods.lootr.data;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent;

import java.util.UUID;

public class DecayingData extends SavedData {
  private final Object2IntMap<UUID> decayMap = new Object2IntOpenHashMap<>();

  public DecayingData() {
    decayMap.defaultReturnValue(-1);
  }

  public boolean isDecayed(UUID id) {
    return decayMap.getInt(id) == 0;
  }

  public int getDecay(UUID id) {
    return decayMap.getInt(id);
  }

  public boolean setDecay(UUID id, int decayAmount) {
    return decayMap.put(id, decayAmount) == -1;
  }

  public int removeDecayed(UUID id) {
    return decayMap.removeInt(id);
  }

  public boolean tickDecay(TickEvent.ServerTickEvent event) {
    if (decayMap.isEmpty()) {
      return false;
    }

    Object2IntMap<UUID> newMap = new Object2IntOpenHashMap<>();
    newMap.defaultReturnValue(-1);

    boolean changed = false;

    for (Object2IntMap.Entry<UUID> entry : decayMap.object2IntEntrySet()) {
      int value = entry.getIntValue();
      if (value > 0) {
        value--;
        changed = true;
      }
      newMap.put(entry.getKey(), value);
    }

    if (changed) {
      decayMap.clear();
      decayMap.putAll(newMap);
      return true;
    }

    return false;
  }

  public static DecayingData load(CompoundTag pCompound) {
    DecayingData data = new DecayingData();
    data.decayMap.clear();
    data.decayMap.defaultReturnValue(-1);
    ListTag decayList = pCompound.getList("result", Tag.TAG_COMPOUND);
    for (int i = 0; i < decayList.size(); i++) {
      CompoundTag thisTag = decayList.getCompound(i);
      data.decayMap.put(thisTag.getUUID("id"), thisTag.getInt("value"));
    }
    return data;
  }

  @Override
  public CompoundTag save(CompoundTag pCompound) {
    ListTag decayList = new ListTag();
    for (Object2IntMap.Entry<UUID> entry : decayMap.object2IntEntrySet()) {
      CompoundTag thisTag = new CompoundTag();
      thisTag.putUUID("id", entry.getKey());
      thisTag.putInt("value", entry.getIntValue());
      decayList.add(thisTag);
    }
    pCompound.put("result", decayList);
    return pCompound;
  }
}
