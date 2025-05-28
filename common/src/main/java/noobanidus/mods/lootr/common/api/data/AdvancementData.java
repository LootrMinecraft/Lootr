package noobanidus.mods.lootr.common.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AdvancementData extends SavedData {
  public static final Codec<AdvancementData> CODEC = RecordCodecBuilder.create(
          instance -> instance.group(
                          UUIDPair.CODEC.listOf().fieldOf("pairs").forGetter(data -> data.data.stream().toList())
                  )
                  .apply(instance, AdvancementData::new)
  );
  public static final SavedDataType<AdvancementData> TYPE = new SavedDataType<>("lootr_advancement_data", AdvancementData::new, AdvancementData.CODEC, null);

  private final Set<UUIDPair> data = new HashSet<>();

  public AdvancementData() {
  }

  public AdvancementData(List<UUIDPair> list) {
    this();
    this.data.addAll(list);
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

  public record UUIDPair(UUID first, UUID second) {
    public static final Codec<UUIDPair> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("first").forGetter(UUIDPair::first),
            UUIDUtil.CODEC.fieldOf("second").forGetter(UUIDPair::second)
    ).apply(instance, UUIDPair::new));
  }
}
