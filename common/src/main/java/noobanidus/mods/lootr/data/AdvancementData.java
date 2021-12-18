package noobanidus.mods.lootr.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AdvancementData extends SavedData {
  private final Set<UUIDPair> data = new HashSet<>();

  public AdvancementData(String id) {
    super(id);
  }

  public boolean contains (UUID first, UUID second) {
    return contains(new UUIDPair(first, second));
  }

  public boolean contains (UUIDPair pair) {
    return data.contains(pair);
  }

  public void add (UUID first, UUID second) {
    add(new UUIDPair(first, second));
  }

  public void add (UUIDPair pair) {
    data.add(pair);
  }

  @Override
  public void load(CompoundTag compound) {
    this.data.clear();
    ListTag data = compound.getList("data", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < data.size(); i++) {
      this.data.add(UUIDPair.fromNBT(data.getCompound(i)));
    }
  }

  @Override
  public CompoundTag save(CompoundTag pCompound) {
    ListTag result = new ListTag();
    for (UUIDPair pair : this.data) {
      result.add(pair.serializeNBT());
    }
    pCompound.put("data", result);
    return pCompound;
  }

  public static class UUIDPair implements INBTSerializable<CompoundTag> {
    @Nonnull
    private UUID first;
    @Nonnull
    private UUID second;

    protected UUIDPair () {
    }

    public UUIDPair(@Nonnull UUID first, @Nonnull UUID second) {
      this.first = first;
      this.second = second;
    }
    @Nonnull
    public UUID getFirst() {
      return first;
    }

    @Nonnull
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

    @Override
    public CompoundTag serializeNBT() {
      CompoundTag result = new CompoundTag();
      result.putUUID("first", getFirst());
      result.putUUID("second", getSecond());
      return result;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
      this.first = nbt.getUUID("first");
      this.second = nbt.getUUID("second");
    }

    public static UUIDPair fromNBT (CompoundTag tag) {
      UUIDPair pair = new UUIDPair();
      pair.deserializeNBT(tag);
      return pair;
    }
  }
}
