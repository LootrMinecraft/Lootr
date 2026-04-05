package noobanidus.mods.lootr.common.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import noobanidus.mods.lootr.common.api.LootrAPI;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class TickingData {
  private static final TickingData REFRESH_DATA = new TickingData(TickingType.REFRESH);
  private static final TickingData DECAY_DATA = new TickingData(TickingType.DECAY);

  public static TickingData getRefreshData() {
    return REFRESH_DATA;
  }

  public static TickingData getDecayData() {
    return DECAY_DATA;
  }

  private final TickingType type;

  protected TickingData(TickingType type) {
    this.type = type;
  }

  public void setCompletesIn(MinecraftServer server, ILootrData data, long tickTime) {
    Section section = getSection(server, data);
    try {
      section.setCompletesAt(data, server.getWorldData().overworldData().getGameTime() + tickTime);
    } catch (SectionException e) {
      LootrAPI.LOG.error("Unable to set {} ticking data for id {}: section mismatch, expected {}", type.getPrefix(), data.getDataIdentifier(), section.identifier);
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

  @SuppressWarnings("DataFlowIssue")
  private Section getSection(MinecraftServer server, ILootrData id) {
    var level = server.overworld();
    var dataStorage = level.getDataStorage();
    return dataStorage.computeIfAbsent(new SavedDataType<>(id.getDataIdentifier(), () -> new Section(id.getDataIdentifier(), Section.CODEC.apply(id.getDataIdentifier()), null)));
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
    public static final Function<Identifier, Codec<Section>> CODEC = (name) -> TickEntry.CODEC.listOf()
        .xmap((data) -> new Section(name, data), o -> o.getTickMap().object2LongEntrySet().stream()
            .map(e -> new TickEntry(e.getKey(), e.getLongValue())).toList());

    private final Object2LongMap<UUID> tickMap = new Object2LongOpenHashMap<>();
    private final Identifier identifier;

    public Section(Identifier identifier) {
      this.tickMap.defaultReturnValue(-1L);
      this.identifier = identifier;
    }

    private Section(Identifier identifier, List<TickEntry> entries) {
      this(identifier);
      for (TickEntry entry : entries) {
        this.tickMap.put(entry.id(), entry.value());
      }
    }

    private boolean excludes(ILootrData id) {
      return !id.getDataIdentifier().equals(this.identifier);
    }

    public boolean completed(MinecraftServer server, ILootrData id) throws SectionException {
      if (excludes(id)) {
        throw new SectionException();
      }
      long completesAt = completesAt(id);
      if (completesAt == -1L) {
        return false;
      }
      return server.getWorldData().overworldData().getGameTime() >= completesAt;
    }

    public long completesAt(ILootrData id) throws SectionException {
      if (excludes(id)) {
        throw new SectionException();
      }
      return tickMap.getLong(id.getDataId());
    }

    public void setCompletesAt(ILootrData id, long tickTime) throws SectionException {
      if (excludes(id)) {
        throw new SectionException();
      }
      tickMap.put(id.getDataId(), tickTime);
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
