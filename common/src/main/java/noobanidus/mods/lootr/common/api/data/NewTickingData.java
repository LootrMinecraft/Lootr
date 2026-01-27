package noobanidus.mods.lootr.common.api.data;

import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import noobanidus.mods.lootr.common.api.LootrAPI;

import java.io.File;
import java.util.UUID;
import java.util.function.Function;

@SuppressWarnings("DataFlowIssue")
public class NewTickingData {
  private static final Function<String, SavedData.Factory<Section>> STRING_FACTORY = (name) -> new SavedData.Factory<>(() -> new Section(name), Section::load, null);

  private static final Object2ObjectMap<UUID, String> CACHED_NAMES = new Object2ObjectOpenHashMap<>();
  private static final Object2ObjectMap<UUID, String> CACHED_FILE_NAMES = new Object2ObjectOpenHashMap<>();

  private static final NewTickingData REFRESH_DATA = new NewTickingData(TickingType.REFRESH);
  private static final NewTickingData DECAY_DATA = new NewTickingData(TickingType.DECAY);

  public static NewTickingData getRefreshData () {
    return REFRESH_DATA;
  }

  public static NewTickingData getDecayData () {
    return DECAY_DATA;
  }

  private final String prefix;
  private final TickingType type;

  @SuppressWarnings("deprecation")
  public void migrateOldData (MinecraftServer server, TickingData oldData) {
    var currentGameTime = server.getWorldData().overworldData().getGameTime();
    for (Object2IntMap.Entry<UUID> entry : oldData.getTickMap().object2IntEntrySet()) {
      UUID id = entry.getKey();
      var completesAt = currentGameTime;
      int oldValue = entry.getIntValue();
      if (oldValue > 0) {
        completesAt += oldValue;
      }
      Section section = getSection(server, id);
      try {
        section.setCompletesAt(id, completesAt);
      } catch (SectionException e) {
        LootrAPI.LOG.error("Unable to migrate {} ticking data for id {}: section mismatch, expected {}", type.getPrefix(), id, section.cachedName);
      }
    }
    oldData.clear();
  }

  protected NewTickingData(TickingType type) {
    this.type = type;
    this.prefix = "lootr/ticking/" + type.getPrefix() + "/";
  }

  public void setCompletesIn (MinecraftServer server, UUID id, long tickTime) {
    Section section = getSection(server, id);
    try {
      section.setCompletesAt(id, server.getWorldData().overworldData().getGameTime() + tickTime);
    } catch (SectionException e) {
      LootrAPI.LOG.error("Unable to set {} ticking data for id {}: section mismatch, expected {}", type.getPrefix(), id, section.cachedName);
    }
  }

  public long howLongUntilComplete (MinecraftServer server, UUID id) {
    Section section = getSection(server, id);
    try {
      long completesAt = section.completesAt(id);
      if (completesAt == -1L) {
        return -1L;
      }
      long currentTime = server.getWorldData().overworldData().getGameTime();
      return Math.max(0L, completesAt - currentTime);
    } catch (SectionException e) {
      LootrAPI.LOG.error("Unable to get {} ticking data for id {}: section mismatch, expected {}", type.getPrefix(),id, section.cachedName);
      return -1L;
    }
  }

  private static String getCached(UUID id) {
    return CACHED_NAMES.computeIfAbsent(id, UUID::toString);
  }

  private static String getBaseFileName(UUID id) {
    return CACHED_FILE_NAMES.computeIfAbsent(id, (UUID uuid) -> {
      var name = getCached(uuid);
      return name.substring(0, 2);
    });
  }

  private String getFileName (UUID id) {
    return prefix + getBaseFileName(id);
  }

  private Section getSection(MinecraftServer server, UUID id) {
    var level = server.overworld();
    var dataStorage = level.getDataStorage();
    return dataStorage.computeIfAbsent(STRING_FACTORY.apply(getBaseFileName(id)), getFileName(id));
  }

  public static class SectionException extends Exception {
  }

  @SuppressWarnings("NullableProblems")
  protected static class Section extends SavedData {
    private final Object2LongMap<UUID> tickMap = new Object2LongOpenHashMap<>();
    private final String cachedName;

    public Section(String cachedName) {
      this.tickMap.defaultReturnValue(-1L);
      this.cachedName = cachedName;
    }

    private boolean excludes(UUID id) {
      return !this.cachedName.equals(getBaseFileName(id));
    }

    public boolean completed(MinecraftServer server, UUID id) throws SectionException {
      if (excludes(id)) {
        throw new SectionException();
      }
      long completesAt = completesAt(id);
      if (completesAt == -1L) {
        return false;
      }
      return server.getWorldData().overworldData().getGameTime() >= completesAt;
    }

    public long completesAt(UUID id) throws SectionException {
      if (excludes(id)) {
        throw new SectionException();
      }
      return tickMap.getLong(id);
    }

    public void setCompletesAt(UUID id, long tickTime) throws SectionException {
      if (excludes(id)) {
        throw new SectionException();
      }
      tickMap.put(id, tickTime);
      setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
      ListTag decayList = new ListTag();
      for (Object2LongMap.Entry<UUID> entry : tickMap.object2LongEntrySet()) {
        CompoundTag thisTag = new CompoundTag();
        thisTag.putUUID("id", entry.getKey());
        thisTag.putLong("value", entry.getLongValue());
        decayList.add(thisTag);
      }
      tag.put("result", decayList);
      tag.putString("cachedName", cachedName);
      return tag;
    }

    public static Section load(CompoundTag pCompound, HolderLookup.Provider provider) {
      String cachedName = pCompound.getString("cachedName");
      Section data = new Section(cachedName);
      data.tickMap.clear();
      data.tickMap.defaultReturnValue(-1);
      ListTag decayList = pCompound.getList("result", 10);
      for (int i = 0; i < decayList.size(); i++) {
        CompoundTag thisTag = decayList.getCompound(i);
        data.tickMap.put(thisTag.getUUID("id"), thisTag.getLong("value"));
      }
      return data;
    }

    @Override
    public void save(File file, HolderLookup.Provider registries) {
      if (isDirty()) {
        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();
      }
      super.save(file, registries);
    }
  }

  public enum TickingType {
    DECAY("decay"),
    REFRESH("refresh");

    private final String prefix;

    TickingType(String prefix) {
      this.prefix = prefix;
    }

    public String getPrefix() {
      return prefix;
    }
  }
}
