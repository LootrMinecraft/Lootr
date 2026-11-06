package noobanidus.mods.lootr.common.data;

import com.mojang.serialization.Dynamic;
import net.minecraft.SharedConstants;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.ILootrData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Imports the per-container saved data files written by Lootr for 1.21.x
 * ({@code data/lootr/<d>/<dd>/<uuid>.dat}) into the section-based storage.
 * <br />
 * Migration happens lazily, the first time a container with no existing store is accessed,
 * and the old file is renamed afterwards so that it is never imported twice.
 */
final class LegacyDataMigrator {
  // Data version of Minecraft 1.21.1, used if the file does not declare one.
  private static final int FALLBACK_DATA_VERSION = 3955;
  private static final String MIGRATED_SUFFIX = ".migrated";

  private LegacyDataMigrator() {
  }

  static Path getLegacyFile(MinecraftServer server, UUID id) {
    String idString = id.toString();
    return server.getWorldPath(LevelResource.ROOT)
        .resolve("data")
        .resolve("lootr")
        .resolve(String.valueOf(idString.charAt(0)))
        .resolve(idString.substring(0, 2))
        .resolve(idString + ".dat");
  }

  /**
   * @return true if a legacy file was found and imported into the store.
   */
  static boolean migrate(LootrInventoryStore store, ILootrData data) {
    MinecraftServer server = LootrAPI.getServer();
    if (server == null) {
      return false;
    }

    // Timers live in separate files, so they are imported regardless of whether the container file still exists.
    migrateTimers(server, store, data.getDataId());

    Path file = getLegacyFile(server, data.getDataId());
    if (!Files.isRegularFile(file)) {
      return false;
    }

    try {
      CompoundTag root = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
      int fromVersion = NbtUtils.getDataVersion(root, FALLBACK_DATA_VERSION);
      int toVersion = SharedConstants.getCurrentVersion().dataVersion().version();
      CompoundTag tag = root.getCompoundOrEmpty("data");
      RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, server.registryAccess());

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
          NonNullList<ItemStack> items = data.buildInitialInventory();
          int slot = 0;
          for (Tag itemTag : byPlayer.getListOrEmpty(key)) {
            if (slot >= items.size()) {
              break;
            }
            if (itemTag instanceof CompoundTag compound && compound.contains("id")) {
              items.set(slot, readStack(compound, ops, fromVersion, toVersion));
            }
            slot++;
          }
          inventories.put(owner, new LootrInventory(items));
        }
      } else if (inventoriesTag instanceof ListTag list) {
        // 1.21.1 layout: [ {uuid: int[], chest: {Items: [{Slot, id, ...}]}} ]
        for (CompoundTag entry : list.compoundStream().toList()) {
          expected++;
          UUID owner = readUuid(entry.get("uuid"));
          if (owner == null) {
            continue;
          }
          NonNullList<ItemStack> items = data.buildInitialInventory();
          for (CompoundTag itemTag : entry.getCompoundOrEmpty("chest").getListOrEmpty("Items").compoundStream().toList()) {
            int slot = itemTag.getByteOr("Slot", (byte) 0) & 255;
            if (slot < items.size()) {
              items.set(slot, readStack(itemTag, ops, fromVersion, toVersion));
            }
          }
          inventories.put(owner, new LootrInventory(items));
        }
      }
      if (expected > 0 && inventories.size() != expected) {
        throw new IllegalStateException("Read " + inventories.size() + " of " + expected + " player inventories");
      }

      Set<UUID> actualOpeners = readUuidSet(tag, "actualOpeners");
      // Visual openers are what the client uses to show a container as "already opened" for a player.
      Set<UUID> visualOpeners = readUuidSet(tag, "openers");

      boolean hasBeenOpened = tag.getBooleanOr("hasBeenOpened", false) || !inventories.isEmpty();
      store.importLegacy(inventories, visualOpeners, actualOpeners, hasBeenOpened);

      Files.move(file, file.resolveSibling(file.getFileName() + MIGRATED_SUFFIX), StandardCopyOption.REPLACE_EXISTING);
      LootrAPI.LOG.info("Migrated legacy Lootr data for container {} ({} inventories).", data.getDataId(), inventories.size());
      return true;
    } catch (IOException | RuntimeException e) {
      LootrAPI.LOG.error("Failed to migrate legacy Lootr data from {}; the file has been left in place.", file, e);
      return false;
    }
  }

  private static void migrateTimers(MinecraftServer server, LootrInventoryStore store, UUID id) {
    try {
      long decay = readTimer(server, "decay", "Lootr-DecayData", id);
      long refresh = readTimer(server, "refresh", "Lootr-RefreshData", id);
      if (decay != -1 || refresh != -1) {
        store.importLegacyTimers(decay, refresh);
        LootrAPI.LOG.info("Migrated legacy Lootr timers for container {} (decay: {}, refresh: {}).", id, decay, refresh);
      }
    } catch (RuntimeException e) {
      LootrAPI.LOG.error("Failed to migrate legacy Lootr timers for container {}.", id, e);
    }
  }

  /**
   * @return the absolute game time at which the timer completes, or -1 if there is none.
   */
  private static long readTimer(MinecraftServer server, String type, String flatName, UUID id) {
    Path base = server.getWorldPath(LevelResource.ROOT).resolve("data").resolve("lootr");
    // 1.21.1 (later builds) and 1.21.11: sectioned files holding an absolute game time.
    Path sectioned = base.resolve("ticking").resolve(type).resolve(id.toString().substring(0, 2) + ".dat");
    long value = findTimerEntry(sectioned, id);
    if (value >= 0) {
      return value;
    }
    // 1.21.1 (older builds) and 1.21.5 to 1.21.10: one flat file holding the ticks remaining.
    value = findTimerEntry(base.resolve(flatName + ".dat"), id);
    if (value >= 0) {
      return LootrAPI.getGameTime() + value;
    }
    return -1;
  }

  private static long findTimerEntry(Path file, UUID id) {
    if (!Files.isRegularFile(file)) {
      return -1;
    }
    try {
      Tag data = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap()).get("data");
      // 1.21.5+ writes the list directly; 1.21.1 wraps it in "result".
      ListTag entries = data instanceof ListTag list ? list : data instanceof CompoundTag compound ? compound.getListOrEmpty("result") : new ListTag();
      for (CompoundTag entry : entries.compoundStream().toList()) {
        if (id.equals(readUuid(entry.get("id")))) {
          return entry.getLongOr("value", -1L);
        }
      }
      return -1;
    } catch (IOException e) {
      LootrAPI.LOG.error("Failed to read legacy Lootr timer file {}.", file, e);
      return -1;
    }
  }

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
