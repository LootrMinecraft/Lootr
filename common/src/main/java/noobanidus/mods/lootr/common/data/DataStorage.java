package noobanidus.mods.lootr.common.data;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.SavedDataStorage;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.ILootrContainerInstance;
import noobanidus.mods.lootr.common.api.filler.ILootFiller;
import noobanidus.mods.lootr.common.api.data.TickingData;
import noobanidus.mods.lootr.common.api.interfaces.inventory.ILootrInventory;
import noobanidus.mods.lootr.common.command.IOUtil;
import noobanidus.mods.lootr.common.mixin.accessor.AccessorMixinSavedDataStorage;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@SuppressWarnings({"unused", "DataFlowIssue"})
public final class DataStorage {
  // Data storage is *always* the 'universal' server-based storage,
  // as there's the possibility that containers will be moved between
  // dimensions.
  @ApiStatus.Internal
  @Nullable
  public static SavedDataStorage getDataStorage() {
    MinecraftServer server = LootrAPI.getServer();
    if (server == null) {
      LootrAPI.LOG.error("MinecraftServer is null at this stage; Lootr cannot fetch data storage.");
      return null;
    }
    return server.getDataStorage();
  }

  @ApiStatus.Internal
  public static LootrInventoryStore getData(ILootrContainerInstance instance) {
    SavedDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      if (instance.getDataLevel() == null || (instance.getDataLevel() != null && instance.getDataLevel()
          .isClientSide())) {
        return null;
      }
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot fetch data for {} at {} with ID {} and cannot continue.", instance.getDataDimension(), instance.getDataPos(), instance.getDataId());
      return null;
    }

    var id = instance.getDataIdentifier();

    Section section = manager.computeIfAbsent(new SavedDataType<>(id, () -> new Section(id), Section.CODEC.apply(id), null));

    // The section automatically sets the data
    return section.getStore(instance);
  }

  @Nullable
  public static ILootrInventory getInventory(ILootrContainerInstance instance, ServerPlayer player, ILootFiller filler) {
    LootrInventoryStore data = getData(instance);
    if (data == null) {
      return null;
    }
    return data.getOrCreateInventory(instance, player, filler);
  }

  @ApiStatus.Internal
  public static boolean clearInventories(Player player) {
    return clearInventories(player.getUUID());
  }

  public static Set<String> getAllLootrFiles() {
    SavedDataStorage data = getDataStorage();
    if (data == null) {
      // Errors are already generated in `getDataStorage`
      return Collections.emptySet();
    }

    MinecraftServer server = LootrAPI.getServer();
    if (server == null) {
      LootrAPI.LOG.error("MinecraftServer is null at this stage; Lootr cannot clear inventories.");
      return Collections.emptySet();
    }

    Path dataPath = server.getWorldPath(new LevelResource("data")).resolve("lootr");

    Set<String> files = new HashSet<>();
    for (String cache : ((AccessorMixinSavedDataStorage) data).getCache().keySet()) {
      if (cache.startsWith("lootr/")) {
        if (cache.startsWith("lootr/Lootr-") || cache.startsWith("lootr/lootr-")) {
          continue;
        }
        files.add(cache);
      }
    }

    try (Stream<Path> paths = Files.walk(dataPath)) {
      paths.forEach(path -> {
        if (Files.isRegularFile(path)) {
          String fileName = path.getFileName().toString();
          if (fileName.startsWith("lootr-") || fileName.startsWith("Lootr-")) {
            return;
          }
          files.add("lootr/" + fileName.charAt(0) + "/" + fileName.substring(0, 2) + "/" + fileName.replace(".dat", ""));
        }
      });
    } catch (IOException e) {
      return files;
    }

    return files;
  }

  @ApiStatus.Internal
  // This is now safe!
  public static boolean clearInventories(UUID id) {
/*    SavedDataStorage data = getDataStorage();
    if (data == null) {
      // Errors are already generated in `getDataStorage`
      return false;
    }

    MinecraftServer server = LootrAPI.getServer();
    if (server == null) {
      LootrAPI.LOG.error("MinecraftServer is null at this stage; Lootr cannot clear inventories.");
      return false;
    }

    Set<String> files = getAllLootrFiles();

    int count = 0;

    for (String file : files) {
      SavedData datum = LootrDummyData.INSTANCE; *//*data.get(new SavedDataType<>(file, () -> null, (Codec<SavedData>) (Object) LootrSavedData.CODEC, null));*//*
      if (datum == LootrDummyData.INSTANCE) {
        // Failed to load so clear it from the cache
        LootrAPI.LOG.error("Failed to load data for {}, removing from cache.", file);
        ((AccessorMixinSavedDataStorage) data).getCache().remove(file);
        continue;
      }
      if (!(datum instanceof LootrContainerData lootrSavedData)) {
        LootrAPI.LOG.error("Data for {} is not a LootrSavedData instance.", file);
        ((AccessorMixinSavedDataStorage) data).getCache().remove(file);
        continue;
      }
      if (!lootrSavedData.hasBeenOpened()) {
        continue;
      }

      if (lootrSavedData.clearInventories(id)) {
        count++;
        ServerLevel level = server.getLevel(lootrSavedData.getInfoDimension());
        if (level != null) {
          ServerChunkCache chunkCache = level.getChunkSource();
          ChunkPos chunkPos = ChunkPos.containing(lootrSavedData.getInfoPos());
          if (chunkCache.hasChunk(chunkPos.x(), chunkPos.z()) && LoadedChunks.getLoadedChunks(lootrSavedData.getInfoDimension())
              .contains(chunkPos)) {
            // TODO: This should still be simplified
            ILootrDataInstance instance = null;
            //noinspection deprecation
            if (lootrSavedData.getInfoType().isEntity()) {
              Entity entity = level.getEntity(lootrSavedData.getInfoUUID());
              if (entity instanceof ILootrEntity cart) {
                instance = cart;
              }
            } else {
              BlockEntity entity = level.getBlockEntity(lootrSavedData.getInfoPos());
              if (LootrAPI.resolveBlockEntity(entity) instanceof ILootrBlockEntity blockEntity) {
                instance = blockEntity;
              }
            }
            if (instance != null) {
              instance.removeVisualOpener(id);
              instance.performClose();
              instance.performUpdate();
            }
          }
        }
      }
    }

    if (count > 0) {
      data.scheduleSave();
      LootrAPI.LOG.info("Cleared {} inventories for player UUID {}", count, id.toString());
      return true;
    }

    return false;*/
    return false;
  }
}
