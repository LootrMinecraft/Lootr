package noobanidus.mods.lootr.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

  public void add(UUIDPair pair) {
    data.add(pair);
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    this.data.clear();
    NBTTagList data = compound.getTagList("data", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < data.tagCount(); i++) {
      this.data.add(UUIDPair.fromNBT(data.getCompoundTagAt(i)));
    }
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound pCompound) {
    NBTTagList result = new NBTTagList();
    for (UUIDPair pair : this.data) {
      result.appendTag(pair.serializeNBT());
    }
    pCompound.setTag("data", result);
    return pCompound;
  }

  public static class UUIDPair implements INBTSerializable<NBTTagCompound> {
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
    public NBTTagCompound serializeNBT() {
      NBTTagCompound result = new NBTTagCompound();
      result.setUniqueId("first", getFirst());
      result.setUniqueId("second", getSecond());
      return result;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
      this.first = nbt.getUniqueId("first");
      this.second = nbt.getUniqueId("second");
    }

    public static UUIDPair fromNBT(NBTTagCompound tag) {
      UUIDPair pair = new UUIDPair();
      pair.deserializeNBT(tag);
      return pair;
    }
  }
}
