package noobanidus.mods.lootr.data;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.TickEvent;

import java.util.UUID;

public class DecayingData extends WorldSavedData {
  private final Object2IntMap<UUID> decayMap = new Object2IntOpenHashMap<>();

  public DecayingData(String id) {
    super(id);
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

  @Override
  public void load(CompoundNBT pCompound) {
    decayMap.clear();
    decayMap.defaultReturnValue(-1);
    ListNBT decayList = pCompound.getList("result", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < decayList.size(); i++) {
      CompoundNBT thisTag = decayList.getCompound(i);
      decayMap.put(thisTag.getUUID("id"), thisTag.getInt("value"));
    }
  }

  @Override
  public CompoundNBT save(CompoundNBT pCompound) {
    ListNBT decayList = new ListNBT();
    for (Object2IntMap.Entry<UUID> entry : decayMap.object2IntEntrySet()) {
      CompoundNBT thisTag = new CompoundNBT();
      thisTag.putUUID("id", entry.getKey());
      thisTag.putInt("value", entry.getIntValue());
      decayList.add(thisTag);
    }
    pCompound.put("result", decayList);
    return pCompound;
  }
}
