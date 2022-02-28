package net.zestyblaze.lootr.data;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.UUID;

public class TickingData extends SavedData {
    private final Object2IntMap<UUID> tickMap = new Object2IntOpenHashMap<>();

    public TickingData() {
        tickMap.defaultReturnValue(-1);
    }

    public boolean isComplete(UUID id) {
        return tickMap.getInt(id) == 0;
    }

    public int getValue(UUID id) {
        return tickMap.getInt(id);
    }

    public boolean setValue(UUID id, int decayAmount) {
        return tickMap.put(id, decayAmount) == -1;
    }

    public int remove(UUID id) {
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

    public static TickingData load(CompoundTag pCompound) {
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

    @Override
    public CompoundTag save(CompoundTag compound) {
        ListTag decayList = new ListTag();
        for (Object2IntMap.Entry<UUID> entry : tickMap.object2IntEntrySet()) {
            CompoundTag thisTag = new CompoundTag();
            thisTag.putUUID("id", entry.getKey());
            thisTag.putInt("value", entry.getIntValue());
            decayList.add(thisTag);
        }
        compound.put("result", decayList);
        return compound;
    }
}
