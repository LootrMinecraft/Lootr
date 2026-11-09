package noobanidus.mods.lootr.common.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.BuiltInLootrTypes;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.api.data.IKeyedData;
import noobanidus.mods.lootr.common.api.data.ILootrData;
import noobanidus.mods.lootr.common.api.data.base.BaseLootrData;
import noobanidus.mods.lootr.common.api.interfaces.type.ILootrType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * Converts the per-container saved data files written by Lootr for 1.21.x
 * ({@code data/lootr/<d>/<dd>/<uuid>.dat}) into {@link LootrInventoryStore}s for the section-based storage.
 * <br />
 * Nothing in here touches the live world: {@link #convert} turns one file into one new store with no side effects,
 * and {@link LegacyMigrationRunner} decides what to do with the result.
 */
final class LegacyDataMigrator {
  // Data version of Minecraft 1.21.1, used if the file does not declare one.
  private static final int FALLBACK_DATA_VERSION = 3955;

  /**
   * The old {@code LootrBlockType} enum, in ordinal order (1.21.1 saved the ordinal, 1.21.5+ saved the lowercase name).
   */
  private static final List<String> LEGACY_BLOCK_TYPES = List.of("chest", "trapped_chest", "barrel", "shulker", "inventory", "entity");

  private LegacyDataMigrator() {
  }

  /**
   * The timers of every legacy container, keyed by container UUID, as absolute game times.
   */
  record LegacyTimers(Map<UUID, Long> decay, Map<UUID, Long> refresh) {
    static final LegacyTimers EMPTY = new LegacyTimers(Map.of(), Map.of());

    long decayFor(UUID id) {
      return decay.getOrDefault(id, -1L);
    }

    long refreshFor(UUID id) {
      return refresh.getOrDefault(id, -1L);
    }
  }

  /**
   * The result of converting one legacy file.
   */
  record ConvertedContainer(LootrInventoryStore store, int inventoryCount) {
    UUID id() {
      return store.getData().getDataId();
    }

    Identifier sectionId() {
      return store.getData().getDataIdentifier();
    }
  }

  static Path getLegacyRoot(MinecraftServer server) {
    return server.getWorldPath(LevelResource.ROOT).resolve("data").resolve(LootrConstants.LOOTR_DATA_DIRECTORY);
  }

  /**
   * Converts one legacy container file. Has no side effects: the file is not modified and no existing store is touched.
   *
   * @return the converted store, or empty (after logging why) if the file could not be converted.
   */
  static Optional<ConvertedContainer> convert(Path file, HolderLookup.Provider registries, LegacyTimers timers) {
    RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);
    try {
      CompoundTag root = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
      if (!root.contains("data")) {
        LootrAPI.LOG.error("Legacy Lootr file {} has no 'data' tag.", file);
        return Optional.empty();
      }
      int fromVersion = NbtUtils.getDataVersion(root, FALLBACK_DATA_VERSION);
      int toVersion = SharedConstants.getCurrentVersion().dataVersion().version();
      CompoundTag tag = root.getCompoundOrEmpty("data");
      if (!tag.contains("info")) {
        LootrAPI.LOG.error("Legacy Lootr file {} has no 'info' tag.", file);
        return Optional.empty();
      }

      ILootrData info = readInfo(tag.getCompoundOrEmpty("info"), registries, ops, fromVersion, toVersion, file);
      if (info == null) {
        return Optional.empty();
      }

      Map<UUID, LootrInventory> inventories = new HashMap<>();
      int expected = 0;
      Tag inventoriesTag = tag.get("inventories");
      if (inventoriesTag instanceof CompoundTag byPlayer) {
        // 1.21.5+ layout: { "<player uuid>": [ItemStack, ...] }, one entry per slot (empty slots are {}).
        for (String key : byPlayer.keySet()) {
          expected++;
          UUID owner = parseUuid(key);
          if (owner == null) {
            continue;
          }
          inventories.put(owner, new LootrInventory(readSlotList(byPlayer.getListOrEmpty(key), info.buildInitialInventory(), ops, fromVersion, toVersion)));
        }
      } else if (inventoriesTag instanceof ListTag list) {
        // 1.21.1 layout: [ {uuid: int[], chest: {Items: [{Slot, id, ...}]}} ]
        for (Tag entryTag : list) {
          if (!(entryTag instanceof CompoundTag entry)) {
            continue;
          }
          expected++;
          UUID owner = readUuid(entry.get("uuid"));
          if (owner == null) {
            continue;
          }
          inventories.put(owner, new LootrInventory(readSlottedItems(entry.getCompoundOrEmpty("chest").getListOrEmpty("Items"), info.buildInitialInventory(), ops, fromVersion, toVersion)));
        }
      }
      if (inventories.size() != expected) {
        // Better to leave the file alone than to quietly lose a player's inventory.
        LootrAPI.LOG.error("Legacy Lootr file {}: read {} of {} player inventories; not importing it.", file, inventories.size(), expected);
        return Optional.empty();
      }

      Set<UUID> actualOpeners = readUuidSet(tag, "actualOpeners");
      // Visual openers are what the client uses to show a container as "already opened" for a player.
      Set<UUID> visualOpeners = readUuidSet(tag, "openers");

      // Trust the stored flag; it can legitimately be false while inventories exist (after a refresh).
      // Only infer it from the inventories when the field is missing.
      boolean hasBeenOpened = tag.contains("hasBeenOpened") ? tag.getBooleanOr("hasBeenOpened", false) : !inventories.isEmpty();

      UUID id = info.getDataId();
      LootrInventoryStore store = new LootrInventoryStore(hasBeenOpened, info, inventories, visualOpeners, actualOpeners, timers.decayFor(id), timers.refreshFor(id));
      return Optional.of(new ConvertedContainer(store, inventories.size()));
    } catch (IOException | RuntimeException e) {
      LootrAPI.LOG.error("Failed to convert legacy Lootr data from {}.", file, e);
      return Optional.empty();
    }
  }

  private static ILootrData readInfo(CompoundTag info, HolderLookup.Provider registries, RegistryOps<Tag> ops, int fromVersion, int toVersion, Path file) {
    UUID uuid = readUuid(info.get("uuid"));
    if (uuid == null) {
      LootrAPI.LOG.error("Legacy Lootr file {} has no valid container UUID.", file);
      return null;
    }
    ILootrType type = readType(info);
    if (type == null) {
      LootrAPI.LOG.error("Legacy Lootr file {} (container {}) has an unknown container type; skipping it. Info: {}", file, uuid, info);
      return null;
    }
    BlockPos pos = info.get("position") == null ? null : BlockPos.CODEC.parse(NbtOps.INSTANCE, info.get("position")).result().orElse(null);
    Optional<String> dimension = info.getString("dimension");
    Optional<Integer> size = info.getInt("size");
    if (pos == null || dimension.isEmpty() || size.isEmpty()) {
      LootrAPI.LOG.error("Legacy Lootr file {} (container {}) is missing its position, dimension or size.", file, uuid);
      return null;
    }
    Identifier dimensionId = Identifier.tryParse(dimension.get());
    if (dimensionId == null) {
      LootrAPI.LOG.error("Legacy Lootr file {} (container {}) has an invalid dimension '{}'.", file, uuid, dimension.get());
      return null;
    }

    ResourceKey<LootTable> table = null;
    long seed = -1;
    if (info.contains("table")) {
      Identifier tableId = Identifier.tryParse(info.getStringOr("table", ""));
      if (tableId != null) {
        table = ResourceKey.create(Registries.LOOT_TABLE, tableId);
        seed = info.getLongOr("seed", -1L);
      }
    }

    NonNullList<ItemStack> reference = null;
    Tag referenceTag = info.get("reference");
    if (referenceTag instanceof ListTag list) {
      reference = readSlotList(list, NonNullList.withSize(size.get(), ItemStack.EMPTY), ops, fromVersion, toVersion);
    } else if (referenceTag instanceof CompoundTag compound) {
      // 1.21.1: ContainerHelper format with an explicit size.
      int referenceSize = info.getIntOr("referenceSize", size.get());
      reference = readSlottedItems(compound.getListOrEmpty("Items"), NonNullList.withSize(referenceSize, ItemStack.EMPTY), ops, fromVersion, toVersion);
    }
    if (reference != null) {
      type = BuiltInLootrTypes.INVENTORY;
    }

    return new BaseLootrData(type, uuid, IKeyedData.generateInfoIdentifier(uuid), pos, readName(info.get("name"), registries, ops), ResourceKey.create(Registries.DIMENSION, dimensionId), size.get(), reference, table, seed);
  }

  /**
   * 1.21.1 saved an optional {@code newType} string and a numeric {@code blockType}; 1.21.5+ saves {@code blockType} as
   * the lowercase enum name. {@code type} is the old {@code LootrInfoType} and says nothing about the container.
   */
  private static ILootrType readType(CompoundTag info) {
    Optional<String> newType = info.getString("newType");
    if (newType.isPresent()) {
      return LootrAPI.getType(newType.get());
    }
    String legacy = null;
    Tag blockType = info.get("blockType");
    if (blockType instanceof StringTag) {
      legacy = info.getStringOr("blockType", "");
    } else if (blockType != null) {
      int ordinal = info.getIntOr("blockType", -1);
      if (ordinal >= 0 && ordinal < LEGACY_BLOCK_TYPES.size()) {
        legacy = LEGACY_BLOCK_TYPES.get(ordinal);
      }
    }
    if (legacy == null) {
      return null;
    }
    return switch (legacy.toLowerCase(Locale.ROOT)) {
      case "chest" -> BuiltInLootrTypes.CHEST;
      case "trapped_chest" -> BuiltInLootrTypes.TRAPPED_CHEST;
      case "barrel" -> BuiltInLootrTypes.BARREL;
      case "shulker" -> BuiltInLootrTypes.SHULKER_BOX;
      case "inventory" -> BuiltInLootrTypes.INVENTORY;
      case "entity" -> BuiltInLootrTypes.MINECART;
      default -> null;
    };
  }

  private static Component readName(Tag nameTag, HolderLookup.Provider registries, RegistryOps<Tag> ops) {
    if (nameTag == null) {
      return null;
    }
    if (nameTag instanceof StringTag) {
      // 1.21.1 saved the component as a JSON string.
      String json = nameTag.asString().orElse("");
      try {
        JsonElement element = JsonParser.parseString(json);
        return ComponentSerialization.CODEC.parse(RegistryOps.create(JsonOps.INSTANCE, registries), element).result().orElseGet(() -> Component.literal(json));
      } catch (JsonParseException e) {
        return Component.literal(json);
      }
    }
    return ComponentSerialization.CODEC.parse(ops, nameTag).result().orElse(null);
  }

  /**
   * One entry per slot, empty slots are {@code {}}.
   */
  private static NonNullList<ItemStack> readSlotList(ListTag list, NonNullList<ItemStack> items, RegistryOps<Tag> ops, int fromVersion, int toVersion) {
    int slot = 0;
    for (Tag itemTag : list) {
      if (slot >= items.size()) {
        break;
      }
      if (itemTag instanceof CompoundTag compound && compound.contains("id")) {
        items.set(slot, readStack(compound, ops, fromVersion, toVersion));
      }
      slot++;
    }
    return items;
  }

  /**
   * Vanilla container format: a list of items each carrying their own {@code Slot}.
   */
  private static NonNullList<ItemStack> readSlottedItems(ListTag list, NonNullList<ItemStack> items, RegistryOps<Tag> ops, int fromVersion, int toVersion) {
    for (Tag tag : list) {
      if (tag instanceof CompoundTag itemTag) {
        int slot = itemTag.getByteOr("Slot", (byte) 0) & 255;
        if (slot < items.size()) {
          items.set(slot, readStack(itemTag, ops, fromVersion, toVersion));
        }
      }
    }
    return items;
  }

  // region Timers

  /**
   * Reads every timer file once. Sectioned files ({@code ticking/<type>/<xx>.dat}) already hold absolute game times;
   * the flat files ({@code Lootr-DecayData.dat}, {@code Lootr-RefreshData.dat}) hold ticks remaining, which are
   * made absolute using the game time at the moment of migration. Sectioned values win over flat ones.
   */
  static LegacyTimers loadTimers(MinecraftServer server) {
    Path base = getLegacyRoot(server);
    long now = LootrAPI.getGameTime();
    Map<UUID, Long> decay = new HashMap<>();
    Map<UUID, Long> refresh = new HashMap<>();
    loadFlatTimers(base.resolve("Lootr-DecayData.dat"), now, decay);
    loadFlatTimers(base.resolve("Lootr-RefreshData.dat"), now, refresh);
    loadSectionedTimers(base.resolve("ticking").resolve("decay"), decay);
    loadSectionedTimers(base.resolve("ticking").resolve("refresh"), refresh);
    return new LegacyTimers(decay, refresh);
  }

  private static void loadFlatTimers(Path file, long now, Map<UUID, Long> into) {
    readTimerEntries(file, (id, value) -> into.put(id, now + value));
  }

  private static void loadSectionedTimers(Path directory, Map<UUID, Long> into) {
    if (!Files.isDirectory(directory)) {
      return;
    }
    try (Stream<Path> files = Files.list(directory)) {
      files.filter(p -> p.getFileName().toString().endsWith(".dat")).forEach(file -> readTimerEntries(file, into::put));
    } catch (IOException e) {
      LootrAPI.LOG.error("Failed to list legacy Lootr timer directory {}.", directory, e);
    }
  }

  private static void readTimerEntries(Path file, BiConsumer<UUID, Long> consumer) {
    if (!Files.isRegularFile(file)) {
      return;
    }
    try {
      Tag data = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap()).get("data");
      // 1.21.5+ writes the list directly; 1.21.1 wraps it in "result".
      ListTag entries = data instanceof ListTag list ? list : data instanceof CompoundTag compound ? compound.getListOrEmpty("result") : new ListTag();
      for (Tag entryTag : entries) {
        if (entryTag instanceof CompoundTag entry) {
          UUID id = readUuid(entry.get("id"));
          long value = entry.getLongOr("value", -1L);
          if (id != null && value >= 0) {
            consumer.accept(id, value);
          }
        }
      }
    } catch (IOException | RuntimeException e) {
      LootrAPI.LOG.error("Failed to read legacy Lootr timer file {}.", file, e);
    }
  }

  // endregion

  private static UUID readUuid(Tag tag) {
    if (tag == null) {
      return null;
    }
    return UUIDUtil.CODEC.parse(NbtOps.INSTANCE, tag).result().orElse(null);
  }

  private static Set<UUID> readUuidSet(CompoundTag tag, String key) {
    Set<UUID> result = new HashSet<>();
    for (Tag entry : tag.getListOrEmpty(key)) {
      UUID uuid = readUuid(entry);
      if (uuid != null) {
        result.add(uuid);
      }
    }
    return result;
  }

  private static UUID parseUuid(String value) {
    try {
      return UUID.fromString(value);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  private static ItemStack readStack(CompoundTag itemTag, RegistryOps<Tag> ops, int fromVersion, int toVersion) {
    Tag fixed = DataFixers.getDataFixer()
        .update(References.ITEM_STACK, new Dynamic<>(NbtOps.INSTANCE, (Tag) itemTag), fromVersion, toVersion)
        .getValue();
    return ItemStack.OPTIONAL_CODEC.parse(ops, fixed).resultOrPartial(err -> LootrAPI.LOG.warn("Unable to read legacy item: {}", err)).orElse(ItemStack.EMPTY);
  }
}
