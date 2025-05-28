package noobanidus.mods.lootr.common.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.List;
import java.util.UUID;

public class TickingData extends SavedData {
  private final Object2IntMap<UUID> tickMap = new Object2IntOpenHashMap<>();

  public static final Codec<TickingData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
          UUIDIntEntry.ENTRY_CODEC.listOf().fieldOf("entries").forGetter(data -> data.tickMap.object2IntEntrySet().stream().map(e -> new UUIDIntEntry(e.getKey(), e.getIntValue())).toList())
  ).apply(instance, TickingData::new));
  public static final SavedDataType<TickingData> TYPE_DECAYS = new SavedDataType<>("lootr_decays", TickingData::new, TickingData.CODEC, null);
  public static final SavedDataType<TickingData> TYPE_REFRESHES = new SavedDataType<>("lootr_refreshes", TickingData::new, TickingData.CODEC, null);

  public TickingData() {
    tickMap.defaultReturnValue(-1);
  }

  private TickingData(List<UUIDIntEntry> list) {
    this();
    list.forEach(e -> tickMap.put(e.uuid, e.value));
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

  private record UUIDIntEntry(UUID uuid, int value) {
    private static final Codec<UUIDIntEntry> ENTRY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("id").forGetter(UUIDIntEntry::uuid),
            Codec.INT.fieldOf("value").forGetter(UUIDIntEntry::value)
    ).apply(instance, UUIDIntEntry::new));
  }

  // Codec for each entry
}
