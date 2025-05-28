package noobanidus.mods.lootr.common.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AdvancementData extends SavedData {
  public static final Codec<AdvancementData> CODEC = Codec.list(UUIDPair.MAP_CODEC.codec())
      .<Set<UUIDPair>>xmap(HashSet::new, ArrayList::new).xmap(AdvancementData::new, o -> o.data);

  private final Set<UUIDPair> data;

  public AdvancementData() {
    this.data = new HashSet<>();
  }

  public AdvancementData(Set<UUIDPair> uuidPairs) {
    this.data = uuidPairs;
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

  public record UUIDPair(@NotNull UUID first, UUID second) {
    public static final MapCodec<UUIDPair> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        UUIDUtil.CODEC.fieldOf("first").forGetter(UUIDPair::first),
        UUIDUtil.CODEC.fieldOf("second").forGetter(UUIDPair::second)
    ).apply(instance, UUIDPair::new));

    public UUIDPair(@NotNull UUID first, @NotNull UUID second) {
      this.first = first;
      this.second = second;
    }
  }
}
