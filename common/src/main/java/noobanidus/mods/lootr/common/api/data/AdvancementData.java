package noobanidus.mods.lootr.common.api.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AdvancementData extends SavedData {
  public static final SavedData.Factory<AdvancementData> FACTORY = new SavedData.Factory<>(AdvancementData::new, AdvancementData::load, null);
  private final Set<UUIDPair> data = new HashSet<>();

  public AdvancementData() {
  }

  public static AdvancementData load(CompoundTag compound, HolderLookup.Provider provider) {
    AdvancementData data = new AdvancementData();
    data.data.clear();
    ListTag incoming = compound.getList("data", Tag.TAG_COMPOUND);
    for (int i = 0; i < incoming.size(); i++) {
      data.data.add(UUIDPair.fromNBT(provider, incoming.getCompound(i)));
    }
    return data;
  }

  public boolean contains(UUID first, UUID second) {
    return contains(new UUIDPair(first, second));
  }

  public boolean contains(UUIDPair pair) {
    return !data.isEmpty() && data.contains(pair);
  }

  public void add(UUID first, UUID second) {
    add(new UUIDPair(first, second));
  }

  public void add(UUIDPair pair) {
    data.add(pair);
    setDirty();
  }

  @Override
  public CompoundTag save(CompoundTag pCompound, HolderLookup.Provider provider) {
    ListTag result = new ListTag();
    for (UUIDPair pair : this.data) {
      result.add(pair.serializeNBT(provider));
    }
    pCompound.put("data", result);
    return pCompound;
  }

  public static class UUIDPair {
    @NotNull
    private UUID first;
    private UUID second;

    protected UUIDPair() {
    }

    public UUIDPair(@NotNull UUID first, @NotNull UUID second) {
      this.first = first;
      this.second = second;
    }

    public static UUIDPair fromNBT(HolderLookup.Provider provider, CompoundTag tag) {
      UUIDPair pair = new UUIDPair();
      pair.deserializeNBT(provider, tag);
      return pair;
    }

    @NotNull
    public UUID getFirst() {
      return first;
    }

    @NotNull
    public UUID getSecond() {
      return second;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      UUIDPair uuidPair = (UUIDPair) o;

      if (!first.equals(uuidPair.first)) return false;
      return second.equals(uuidPair.second);
    }

    @Override
    public int hashCode() {
      int result = first.hashCode();
      result = 31 * result + second.hashCode();
      return result;
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
      CompoundTag result = new CompoundTag();
      result.putUUID("first", getFirst());
      result.putUUID("second", getSecond());
      return result;
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
      this.first = nbt.getUUID("first");
      this.second = nbt.getUUID("second");
    }
  }
}
