package noobanidus.mods.lootr.common.data;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.saveddata.SavedData;
import noobanidus.mods.lootr.common.api.data.IKeyedData;
import noobanidus.mods.lootr.common.api.data.ILootrData;
import noobanidus.mods.lootr.common.api.data.ILootrSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class Section extends SavedData implements ILootrSection {
  public static final Function<Identifier, Codec<Section>> CODEC = (Identifier identifier) -> LootrInventoryStore.CODEC.listOf()
      .xmap(stores -> new Section(identifier, stores), section -> new ArrayList<>(section.data.values()));

  private final Map<UUID, LootrInventoryStore> data = new Object2ObjectOpenHashMap<>();
  private final Identifier identifier;

  private Section(Identifier identifier, List<LootrInventoryStore> data) {
    this(identifier);
    data.forEach(store -> this.data.put(store.getData().getDataId(), store));
  }

  public Section(Identifier identifier) {
    this.identifier = identifier;
  }

  public boolean canContain(IKeyedData data) {
    return this.identifier.equals(data.getDataIdentifier());
  }

  public LootrInventoryStore getStore(ILootrData data) {
    if (!canContain(data)) {
      throw new IllegalArgumentException("Data with id " + data.getDataIdentifier() + " cannot be stored in section with id " + this.identifier);
    }
    LootrInventoryStore store = this.data.get(data.getDataId());
    if (store == null) {
      store = new LootrInventoryStore(data);
      this.data.put(data.getDataId(), store);
    }
    store.setSection(this);
    store.update(data);
    return store;
  }

  public Iterable<LootrInventoryStore> getStores() {
    return data.values();
  }

  @Override
  public void markSectionChanged() {
    setDirty(true);
  }
}
