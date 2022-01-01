package noobanidus.mods.lootr.data;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.TickEvent;

import java.util.UUID;

public class TickingData extends WorldSavedData {
  private final Object2IntMap<UUID> tickMap = new Object2IntOpenHashMap<>();

  public TickingData(String id) {
    super(id);
    tickMap.defaultReturnValue(-1);
  }

  public boolean isDone(UUID id) {
    return tickMap.getInt(id) == 0;
  }

  public int getValue(UUID id) {
    return tickMap.getInt(id);
  }

  public boolean setValue(UUID id, int decayAmount) {
    return tickMap.put(id, decayAmount) == -1;
  }

  public int removeDone(UUID id) {
    return tickMap.removeInt(id);
  }

  public boolean tick() {
    if (tickMap.isEmpty()) {
      return false;
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
      return true;
    }

    return false;
  }

  @Override
  public void load(CompoundNBT pCompound) {
    tickMap.clear();
    tickMap.defaultReturnValue(-1);
    ListNBT decayList = pCompound.getList("result", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < decayList.size(); i++) {
      CompoundNBT thisTag = decayList.getCompound(i);
      tickMap.put(thisTag.getUUID("id"), thisTag.getInt("value"));
    }
  }

  @Override
  public CompoundNBT save(CompoundNBT pCompound) {
    ListNBT decayList = new ListNBT();
    for (Object2IntMap.Entry<UUID> entry : tickMap.object2IntEntrySet()) {
      CompoundNBT thisTag = new CompoundNBT();
      thisTag.putUUID("id", entry.getKey());
      thisTag.putInt("value", entry.getIntValue());
      decayList.add(thisTag);
    }
    pCompound.put("result", decayList);
    return pCompound;
  }
}
