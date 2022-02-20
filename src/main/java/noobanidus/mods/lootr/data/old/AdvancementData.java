package noobanidus.mods.lootr.data.old;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import noobanidus.mods.lootr.data.Statistics;

public class AdvancementData extends WorldSavedData {
  private final Set<UUIDPair> data = new HashSet<>();

  public AdvancementData(String id) {
    super(id);
  }

  public boolean contains(UUID first, UUID second) {
    return contains(new UUIDPair(first, second));
  }

  public boolean contains(UUIDPair pair) {
    return data.contains(pair);
  }

  public void add(UUID first, UUID second) {
    add(new UUIDPair(first, second));
  }
  
  public void migrateAwards(Function<UUID, Statistics> data) {
	  for(UUIDPair pair : this.data) {
		  data.apply(pair.getFirst()).award(pair.getSecond());
	  }
  }
  
  public void migrateScore(Function<UUID, Statistics> data) {
	  for(UUIDPair pair : this.data) {
		  data.apply(pair.getFirst()).score(pair.getSecond());
	  }
  }

  public void add(UUIDPair pair) {
    data.add(pair);
  }

  @Override
  public void load(CompoundNBT compound) {
    this.data.clear();
    ListNBT data = compound.getList("data", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < data.size(); i++) {
      this.data.add(UUIDPair.fromNBT(data.getCompound(i)));
    }
  }

  @Override
  public CompoundNBT save(CompoundNBT pCompound) {
    ListNBT result = new ListNBT();
    for (UUIDPair pair : this.data) {
      result.add(pair.serializeNBT());
    }
    pCompound.put("data", result);
    return pCompound;
  }

  public static class UUIDPair implements INBTSerializable<CompoundNBT> {
    private UUID first;
    private UUID second;

    protected UUIDPair() {
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
    public CompoundNBT serializeNBT() {
      CompoundNBT result = new CompoundNBT();
      result.putUUID("first", getFirst());
      result.putUUID("second", getSecond());
      return result;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
      this.first = nbt.getUUID("first");
      this.second = nbt.getUUID("second");
    }

    public static UUIDPair fromNBT(CompoundNBT tag) {
      UUIDPair pair = new UUIDPair();
      pair.deserializeNBT(tag);
      return pair;
    }
  }
}
