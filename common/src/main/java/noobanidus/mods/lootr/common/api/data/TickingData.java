package noobanidus.mods.lootr.common.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import noobanidus.mods.lootr.common.api.LootrAPI;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class TickingData {
  private static final Object2ObjectMap<UUID, String> CACHED_NAMES = new Object2ObjectOpenHashMap<>();
  private static final Object2ObjectMap<UUID, String> CACHED_FILE_NAMES = new Object2ObjectOpenHashMap<>();

  private static final TickingData REFRESH_DATA = new TickingData(TickingType.REFRESH);
  private static final TickingData DECAY_DATA = new TickingData(TickingType.DECAY);

  public static TickingData getRefreshData() {
    return REFRESH_DATA;
  }

  public static TickingData getDecayData() {
    return DECAY_DATA;
  }

  private final String prefix;
  private final TickingType type;

  protected TickingData(TickingType type) {
    this.type = type;
    this.prefix = "lootr/ticking/" + type.getPrefix() + "/";
  }

  public void setCompletesIn(MinecraftServer server, UUID id, long tickTime) {
    Section section = getSection(server, id);
    try {
      section.setCompletesAt(id, server.getWorldData().overworldData().getGameTime() + tickTime);
    } catch (SectionException e) {
      LootrAPI.LOG.error("Unable to set {} ticking data for id {}: section mismatch, expected {}", type.getPrefix(), id, section.cachedName);
    }
  }

  public void clearTicking(MinecraftServer server, UUID id) {
    Section section = getSection(server, id);
    try {
      section.setCompletesAt(id, -1L);
    } catch (SectionException e) {
      LootrAPI.LOG.error("Unable to clear {} ticking data for id {}: section mismatch, expected {}", type.getPrefix(), id, section.cachedName);
    }
  }

  public long howLongUntilComplete(MinecraftServer server, UUID id) {
    Section section = getSection(server, id);
    try {
      long completesAt = section.completesAt(id);
      if (completesAt == -1L) {
        return -1L;
      }
      long currentTime = server.getWorldData().overworldData().getGameTime();
      return Math.max(0L, completesAt - currentTime);
    } catch (SectionException e) {
      LootrAPI.LOG.error("Unable to get {} ticking data for id {}: section mismatch, expected {}", type.getPrefix(), id, section.cachedName);
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

  private String getFileName(UUID id) {
    return prefix + getBaseFileName(id);
  }

  @SuppressWarnings("DataFlowIssue")
  private Section getSection(MinecraftServer server, UUID id) {
    var level = server.overworld();
    var dataStorage = level.getDataStorage();
    return dataStorage.computeIfAbsent(new SavedDataType<>(LootrAPI.rl(getFileName(id)), () -> new Section(getBaseFileName(id)), Section.CODEC.apply(getBaseFileName(id)), null));
  }

  public static class SectionException extends Exception {
  }

  protected static class Section extends SavedData {
    private record TickEntry(UUID id, long value) {
      public static final Codec<TickEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
          UUIDUtil.CODEC.fieldOf("id").forGetter(TickEntry::id),
          Codec.LONG.fieldOf("value").forGetter(TickEntry::value)
      ).apply(instance, TickEntry::new));
    }

    // The input is the first 2 characters of the UUID
    public static final Function<String, Codec<Section>> CODEC = (name) -> TickEntry.CODEC.listOf()
        .xmap((data) -> new Section(name, data), o -> o.getTickMap().object2LongEntrySet().stream()
            .map(e -> new TickEntry(e.getKey(), e.getLongValue())).toList());

    private final Object2LongMap<UUID> tickMap = new Object2LongOpenHashMap<>();
    private final String cachedName;

    public Section(String cachedName) {
      this.tickMap.defaultReturnValue(-1L);
      this.cachedName = cachedName;
    }

    private Section(String cachedName, List<TickEntry> entries) {
      this(cachedName);
      for (TickEntry entry : entries) {
        this.tickMap.put(entry.id(), entry.value());
      }
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

    private Object2LongMap<UUID> getTickMap() {
      return tickMap;
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
