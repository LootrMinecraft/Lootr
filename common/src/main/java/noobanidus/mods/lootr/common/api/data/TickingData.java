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
  private record TickEntry(UUID id, int value) {
    public static final Codec<TickEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(UUIDUtil.CODEC.fieldOf("id")
            .forGetter(TickEntry::id),
        Codec.INT.fieldOf("value").forGetter(TickEntry::value)).apply(instance, TickEntry::new));
  }

  public static final Codec<TickingData> CODEC = TickEntry.CODEC.listOf().xmap(
      TickingData::new, o -> o.getTickMap().object2IntEntrySet().stream()
          .map(entry -> new TickEntry(entry.getKey(), entry.getIntValue()))
          .toList());

  private final Object2IntMap<UUID> tickMap = new Object2IntOpenHashMap<>();

  public TickingData() {
    this.tickMap.defaultReturnValue(-1);
  }

  private TickingData(SavedData.Context context) {
    this();
  }

  private TickingData(List<TickEntry> entries) {
    this();
    for (TickEntry entry : entries) {
      tickMap.put(entry.id(), entry.value());
    }
  }

  private Object2IntMap<UUID> getTickMap() {
    return tickMap;
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
}
