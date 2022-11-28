package noobanidus.mods.lootr.data;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class TickingData extends WorldSavedData {
  private final Object2IntMap<UUID> tickMap = new Object2IntOpenHashMap<>();

  public TickingData(String id) {
    super(id);
    tickMap.defaultReturnValue(-1);
  }

  public boolean isDone(UUID id) {
    int val = tickMap.getInt(id);
    return val == 0 || val == 1;
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
  public void readFromNBT(NBTTagCompound pCompound) {
    tickMap.clear();
    tickMap.defaultReturnValue(-1);
    NBTTagList decayList = pCompound.getTagList("result", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < decayList.tagCount(); i++) {
      NBTTagCompound thisTag = decayList.getCompoundTagAt(i);
      tickMap.put(thisTag.getUniqueId("id"), thisTag.getInteger("value"));
    }
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound pCompound) {
    NBTTagList decayList = new NBTTagList();
    for (Object2IntMap.Entry<UUID> entry : tickMap.object2IntEntrySet()) {
      NBTTagCompound thisTag = new NBTTagCompound();
      thisTag.setUniqueId("id", entry.getKey());
      thisTag.setInteger("value", entry.getIntValue());
      decayList.appendTag(thisTag);
    }
    pCompound.setTag("result", decayList);
    return pCompound;
  }
}
