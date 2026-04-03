package noobanidus.mods.lootr.common.data;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.SavedDataStorage;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.common.api.data.LootFiller;
import noobanidus.mods.lootr.common.api.data.TickingData;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;
import noobanidus.mods.lootr.common.api.data.inventory.ILootrInventory;
import noobanidus.mods.lootr.common.chunk.LoadedChunks;
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
public class DataStorage {
  @ApiStatus.Internal
  @Nullable
  public static SavedDataStorage getDataStorage() {
    MinecraftServer server = LootrAPI.getServer();
    if (server == null) {
      LootrAPI.LOG.error("MinecraftServer is null at this stage; Lootr cannot fetch data storage.");
      return null;
    }
    ServerLevel overworld = server.overworld();
    // Sometimes `overworld` returns null. I have no idea why.
    //noinspection ConstantValue
    if (overworld == null) {
      LootrAPI.LOG.error("The Overworld is null at this stage; Lootr cannot fetch data storage.");
      return null;
    }
    return overworld.getDataStorage();
  }

  @SuppressWarnings("deprecation")
  @ApiStatus.Internal
  public static int getDecayValue(ILootrInfoProvider provider) {
    SavedDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      if (provider.getInfoLevel() == null || (provider.getInfoLevel() != null && provider.getInfoLevel()
          .isClientSide())) {
        return -1;
      }
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot determine the decay value for {}.", provider.getInfoUUID());
      return -1;
    }
    var server = LootrAPI.getServer();
    TickingData data = TickingData.getDecayData();
    // Safe to down-cast as the value should always be quite small
    return (int) data.howLongUntilComplete(server, provider.getInfoUUID());
  }

  @ApiStatus.Internal
  public static boolean isDecayed(ILootrInfoProvider provider) {
    return getDecayValue(provider) == 0;
  }

  @SuppressWarnings("deprecation")
  @ApiStatus.Internal
  public static void setDecaying(ILootrInfoProvider provider) {
    SavedDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      if (provider.getInfoLevel() == null || (provider.getInfoLevel() != null && provider.getInfoLevel()
          .isClientSide())) {
        return;
      }
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot set the decay value for {}.", provider.getInfoUUID());
      return;
    }
    TickingData data = TickingData.getDecayData();
    var server = LootrAPI.getServer();
    data.setCompletesIn(server, provider.getInfoUUID(), LootrAPI.getDecayValue());
  }

  @ApiStatus.Internal
  public static int getRefreshValue(ILootrInfoProvider provider) {
    SavedDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      if (provider.getInfoLevel() == null || (provider.getInfoLevel() != null && provider.getInfoLevel()
          .isClientSide())) {
        return -1;
      }
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot determine the refresh value for {}.", provider.getInfoUUID());
      return -1;
    }
    TickingData data = TickingData.getRefreshData();
    var server = LootrAPI.getServer();
    int result = (int) data.howLongUntilComplete(server, provider.getInfoUUID());
    return result;
  }

  @ApiStatus.Internal
  public static boolean isRefreshed(ILootrInfoProvider provider) {
    return getRefreshValue(provider) == 0;
  }

  @ApiStatus.Internal
  public static void setRefreshing(ILootrInfoProvider provider) {
    SavedDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      if (provider.getInfoLevel() == null || (provider.getInfoLevel() != null && provider.getInfoLevel()
          .isClientSide())) {
        return;
      }
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot set the refresh value for {}.", provider.getInfoUUID());
      return;
    }
    TickingData newData = TickingData.getRefreshData();
    var server = LootrAPI.getServer();
    newData.setCompletesIn(server, provider.getInfoUUID(), LootrAPI.getRefreshValue());
  }

  @ApiStatus.Internal
  public static void removeRefreshed(ILootrInfoProvider provider) {
    SavedDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      if (provider.getInfoLevel() == null || (provider.getInfoLevel() != null && provider.getInfoLevel()
          .isClientSide())) {
        return;
      }
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot set the refresh value for {}.", provider.getInfoUUID());
      return;
    }
    TickingData newData = TickingData.getRefreshData();
    var server = LootrAPI.getServer();
    newData.clearTicking(server, provider.getInfoUUID());
  }

  @ApiStatus.Internal
  public static LootrContainerData getData(ILootrInfoProvider provider) {
    SavedDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      if (provider.getInfoLevel() == null || (provider.getInfoLevel() != null && provider.getInfoLevel()
          .isClientSide())) {
        return null;
      }
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot fetch data for {} at {} with ID {} and cannot continue.", provider.getInfoDimension(), provider.getInfoPos(), provider.getInfoUUID());
      return null;
    }
    LootrContainerData result = manager.computeIfAbsent(new SavedDataType<>(LootrAPI.rl(provider.getInfoKey()), LootrContainerData.fromInfo(provider), LootrContainerData.CODEC, null));
    result.update(provider);
    return result;
  }

  @Nullable
  public static ILootrInventory getInventory(ILootrInfoProvider provider, ServerPlayer player, LootFiller filler) {
    LootrContainerData data = getData(provider);
    if (data == null) {
      // Error messages are already generated by `getData`
      return null;
    }
    return data.getOrCreateInventory(provider, player, filler);
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
    SavedDataStorage data = getDataStorage();
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
      SavedData datum = LootrDummyData.INSTANCE; /*data.get(new SavedDataType<>(file, () -> null, (Codec<SavedData>) (Object) LootrSavedData.CODEC, null));*/
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
            ILootrInfoProvider provider = null;
            //noinspection deprecation
            if (lootrSavedData.getInfoType().isEntity()) {
              Entity entity = level.getEntity(lootrSavedData.getInfoUUID());
              if (entity instanceof ILootrEntity cart) {
                provider = cart;
              }
            } else {
              BlockEntity entity = level.getBlockEntity(lootrSavedData.getInfoPos());
              if (LootrAPI.resolveBlockEntity(entity) instanceof ILootrBlockEntity blockEntity) {
                provider = blockEntity;
              }
            }
            if (provider != null) {
              provider.removeVisualOpener(id);
              provider.performClose();
              provider.performUpdate();
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

    return false;
  }

  @ApiStatus.Internal
  // This is now safe!
  public static int cullInventories() {
    SavedDataStorage data = getDataStorage();
    if (data == null) {
      // Errors are already generated in `getDataStorage`
      return 0;
    }

    MinecraftServer server = LootrAPI.getServer();
    if (server == null) {
      LootrAPI.LOG.error("MinecraftServer is null at this stage; Lootr cannot clear inventories.");
      return 0;
    }

    Set<String> files = getAllLootrFiles();

    Set<String> filesToDelete = new HashSet<>();

    for (String file : files) {
      SavedData datum = null; /*data.get(new SavedDataType<LootrSavedData>(file, () -> null, LootrSavedData.CODEC, null));*/
      if (datum == null) {
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
      if (lootrSavedData.canBeCulled()) {
        filesToDelete.add(file);
        ((AccessorMixinSavedDataStorage) data).getCache().remove(file);
      }
    }

    if (!filesToDelete.isEmpty()) {
      IOUtil.cullSavedDataAsync(server, filesToDelete);
      LootrAPI.LOG.info("Culling {} inventories.", filesToDelete.size());
    }

    return filesToDelete.size();
  }

  private static class LootrDummyData extends SavedData {
    public static final LootrDummyData INSTANCE = new LootrDummyData();

    public LootrDummyData() {
      super();
    }
  }
}
