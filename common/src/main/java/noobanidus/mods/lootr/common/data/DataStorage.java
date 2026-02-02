package noobanidus.mods.lootr.common.data;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
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
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelResource;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.*;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;
import noobanidus.mods.lootr.common.api.data.inventory.ILootrInventory;
import noobanidus.mods.lootr.common.chunk.LoadedChunks;
import noobanidus.mods.lootr.common.command.IOUtil;
import noobanidus.mods.lootr.common.mixin.accessor.AccessorMixinDimensionDataStorage;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings({"unused", "DataFlowIssue"})
public class DataStorage {
  @Deprecated
  public static final String ADVANCEMENTS_ID = "lootr/Lootr-AdvancementData";
  @Deprecated
  public static final String DECAYS_ID = "lootr/Lootr-DecayData";
  @Deprecated
  public static final String REFRESHES_ID = "lootr/Lootr-RefreshData";

  @SuppressWarnings("deprecation")
  public static final SavedDataType<TickingData> DECAYS = new SavedDataType<>(DECAYS_ID, TickingData::new, TickingData.CODEC, null);
  @SuppressWarnings("deprecation")
  public static final SavedDataType<TickingData> REFRESHES = new SavedDataType<>(REFRESHES_ID, TickingData::new, TickingData.CODEC, null);
  @SuppressWarnings("deprecation")
  public static final SavedDataType<AdvancementData> ADVANCEMENTS = new SavedDataType<>(ADVANCEMENTS_ID, AdvancementData::new, AdvancementData.CODEC, null);

  @ApiStatus.Internal
  @Nullable
  public static DimensionDataStorage getDataStorage() {
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

  @ApiStatus.Internal
  @Deprecated
  public static boolean isAwarded(ILootrInfoProvider provider, ServerPlayer player) {
    return isAwarded(provider.getInfoUUID(), player);
  }

  @ApiStatus.Internal
  @Deprecated
  public static boolean isAwarded(UUID uuid, ServerPlayer player) {
    return false;
  }

  @ApiStatus.Internal
  @Deprecated
  public static void award(ILootrInfoProvider provider, ServerPlayer player) {
    award(provider.getInfoUUID(), player);
  }

  @ApiStatus.Internal
  @Deprecated
  public static void award(UUID id, ServerPlayer player) {
  }

  @SuppressWarnings("deprecation")
  @ApiStatus.Internal
  public static int getDecayValue(ILootrInfoProvider provider) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      if (provider.getInfoLevel() == null || (provider.getInfoLevel() != null && provider.getInfoLevel()
          .isClientSide())) {
        return -1;
      }
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot determine the decay value for {}.", provider.getInfoUUID());
      return -1;
    }
    var server = LootrAPI.getServer();
    TickingData oldData = manager.computeIfAbsent(DECAYS);
    NewTickingData data = NewTickingData.getDecayData();
    data.migrateOldData(server, oldData);
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
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      if (provider.getInfoLevel() == null || (provider.getInfoLevel() != null && provider.getInfoLevel()
          .isClientSide())) {
        return;
      }
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot set the decay value for {}.", provider.getInfoUUID());
      return;
    }
    TickingData oldData = manager.computeIfAbsent(DECAYS);
    NewTickingData data = NewTickingData.getDecayData();
    var server = LootrAPI.getServer();
    data.migrateOldData(server, oldData);
    data.setCompletesIn(server, provider.getInfoUUID(), LootrAPI.getDecayValue());
  }

  @ApiStatus.Internal
  public static void removeDecayed(ILootrInfoProvider provider) {
    // NO-OP
  }

  @ApiStatus.Internal
  @Deprecated
  public static void doTick() {
    // NO-OP
  }

  @ApiStatus.Internal
  public static int getRefreshValue(ILootrInfoProvider provider) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      if (provider.getInfoLevel() == null || (provider.getInfoLevel() != null && provider.getInfoLevel()
          .isClientSide())) {
        return -1;
      }
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot determine the refresh value for {}.", provider.getInfoUUID());
      return -1;
    }
    TickingData oldData = manager.computeIfAbsent(REFRESHES);
    NewTickingData data = NewTickingData.getRefreshData();
    var server = LootrAPI.getServer();
    data.migrateOldData(server, oldData);
    int result = (int) data.howLongUntilComplete(server, provider.getInfoUUID());
    return result;
  }

  @ApiStatus.Internal
  public static boolean isRefreshed(ILootrInfoProvider provider) {
    return getRefreshValue(provider) == 0;
  }

  @ApiStatus.Internal
  public static void setRefreshing(ILootrInfoProvider provider) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      if (provider.getInfoLevel() == null || (provider.getInfoLevel() != null && provider.getInfoLevel()
          .isClientSide())) {
        return;
      }
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot set the refresh value for {}.", provider.getInfoUUID());
      return;
    }
    TickingData data = manager.computeIfAbsent(REFRESHES);
    NewTickingData newData = NewTickingData.getRefreshData();
    var server = LootrAPI.getServer();
    newData.migrateOldData(server, data);
    newData.setCompletesIn(server, provider.getInfoUUID(), LootrAPI.getRefreshValue());
  }

  @ApiStatus.Internal
  public static void removeRefreshed(ILootrInfoProvider provider) {
    // NO-OP
  }

  @ApiStatus.Internal
  public static LootrSavedData getData(ILootrInfoProvider provider) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      if (provider.getInfoLevel() == null || (provider.getInfoLevel() != null && provider.getInfoLevel()
          .isClientSide())) {
        return null;
      }
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot fetch data for {} at {} with ID {} and cannot continue.", provider.getInfoDimension(), provider.getInfoPos(), provider.getInfoUUID());
      return null;
    }
    LootrSavedData result = manager.computeIfAbsent(new SavedDataType<>(provider.getInfoKey(), LootrSavedData.fromInfo(provider), LootrSavedData.CODEC, null));
    result.update(provider);
    return result;
  }

  @Nullable
  public static ILootrInventory getInventory(ILootrInfoProvider provider, ServerPlayer player, LootFiller filler) {
    LootrSavedData data = getData(provider);
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
    DimensionDataStorage data = getDataStorage();
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
    for (String cache : ((AccessorMixinDimensionDataStorage) data).getCache().keySet()) {
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
    DimensionDataStorage data = getDataStorage();
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
      SavedData datum = data.get(new SavedDataType<>(file, () -> LootrDummyData.INSTANCE, (Codec<SavedData>)(Object)LootrSavedData.CODEC, null));
      if (datum == LootrDummyData.INSTANCE) {
        // Failed to load so clear it from the cache
        LootrAPI.LOG.error("Failed to load data for {}, removing from cache.", file);
        ((AccessorMixinDimensionDataStorage) data).getCache().remove(file);
        continue;
      }
      if (!(datum instanceof LootrSavedData lootrSavedData)) {
        LootrAPI.LOG.error("Data for {} is not a LootrSavedData instance.", file);
        ((AccessorMixinDimensionDataStorage) data).getCache().remove(file);
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
          ChunkPos chunkPos = new ChunkPos(lootrSavedData.getInfoPos());
          if (chunkCache.hasChunk(chunkPos.x, chunkPos.z) && LoadedChunks.getLoadedChunks(lootrSavedData.getInfoDimension())
              .contains(chunkPos)) {
            // TODO: This should still be simplified
            ILootrInfoProvider provider = null;
            //noinspection deprecation
            if (lootrSavedData.isEntity()) {
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
    DimensionDataStorage data = getDataStorage();
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
      SavedData datum = data.get(new SavedDataType<>(file, () -> LootrDummyData.INSTANCE, (Codec<SavedData>)(Object)LootrSavedData.CODEC, null));
      if (datum == LootrDummyData.INSTANCE) {
        // Failed to load so clear it from the cache
        LootrAPI.LOG.error("Failed to load data for {}, removing from cache.", file);
        ((AccessorMixinDimensionDataStorage) data).getCache().remove(file);
        continue;
      }
      if (!(datum instanceof LootrSavedData lootrSavedData)) {
        LootrAPI.LOG.error("Data for {} is not a LootrSavedData instance.", file);
        ((AccessorMixinDimensionDataStorage) data).getCache().remove(file);
        continue;
      }
      if (lootrSavedData.canBeCulled()) {
        filesToDelete.add(file);
        ((AccessorMixinDimensionDataStorage) data).getCache().remove(file);
      }
    }

    if (!filesToDelete.isEmpty()) {
      IOUtil.cullSavedDataAsync(server, filesToDelete);
      LootrAPI.LOG.info("Culling {} inventories.", filesToDelete.size());
    }

    return filesToDelete.size();
  }

  @SuppressWarnings("NullableProblems")
  private static class LootrDummyData extends SavedData {
    public static final LootrDummyData INSTANCE = new LootrDummyData();

    public LootrDummyData() {
      super();
    }
  }
}
