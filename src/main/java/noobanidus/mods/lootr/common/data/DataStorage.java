package noobanidus.mods.lootr.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelResource;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.data.AdvancementData;
import noobanidus.mods.lootr.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.api.data.LootFiller;
import noobanidus.mods.lootr.api.data.TickingData;
import noobanidus.mods.lootr.api.data.inventory.ILootrInventory;
import noobanidus.mods.lootr.common.mixins.MixinDimensionDataStorage;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class DataStorage {
  public static final String ADVANCEMENTS = "lootr/Lootr-AdvancementData";
  public static final String DECAYS = "lootr/Lootr-DecayData";
  public static final String REFRESHES = "lootr/Lootr-RefreshData";

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
  public static boolean isAwarded(ILootrInfoProvider provider, ServerPlayer player) {
    return isAwarded(provider.getInfoUUID(), player);
  }

  @ApiStatus.Internal
  public static boolean isAwarded(UUID uuid, ServerPlayer player) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot determine if advancement has been awarded.");
      return false;
    }
    AdvancementData data = manager.computeIfAbsent(AdvancementData.FACTORY, ADVANCEMENTS);
    return data.contains(player.getUUID(), uuid);
  }

  @ApiStatus.Internal
  public static void award(ILootrInfoProvider provider, ServerPlayer player) {
    award(provider.getInfoUUID(), player);
  }

  @ApiStatus.Internal
  public static void award(UUID id, ServerPlayer player) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot award advancement.");
      return;
    }
    AdvancementData data = manager.computeIfAbsent(AdvancementData.FACTORY, ADVANCEMENTS);
    data.add(player.getUUID(), id);
  }

  @ApiStatus.Internal
  public static int getDecayValue(ILootrInfoProvider provider) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot determine the decay value for " + provider.getInfoUUID() + ".");
      return -1;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, DECAYS);
    return data.getValue(provider.getInfoUUID());
  }

  @ApiStatus.Internal
  public static boolean isDecayed(ILootrInfoProvider provider) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot determine the decay value for " + provider.getInfoUUID() + ".");
      return false;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, DECAYS);
    return data.isComplete(provider.getInfoUUID());
  }

  @ApiStatus.Internal
  public static void setDecaying(ILootrInfoProvider provider, int decay) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot set the decay value for " + provider.getInfoUUID() + ".");
      return;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, DECAYS);
    data.setValue(provider.getInfoUUID(), decay);
  }

  @ApiStatus.Internal
  public static void removeDecayed(ILootrInfoProvider provider) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr remove the decay value for " + provider.getInfoUUID() + ".");
      return;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, DECAYS);
    data.remove(provider.getInfoUUID());
  }

  @ApiStatus.Internal
  public static void doTick() {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot iterate and tick decay.");
      return;
    }
    manager.computeIfAbsent(TickingData.FACTORY, DECAYS).tick();
    manager.computeIfAbsent(TickingData.FACTORY, REFRESHES).tick();
  }

  @ApiStatus.Internal
  public static int getRefreshValue(ILootrInfoProvider provider) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot determine the refresh value for " + provider.getInfoUUID() + ".");
      return -1;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, REFRESHES);
    return data.getValue(provider.getInfoUUID());
  }

  @ApiStatus.Internal
  public static boolean isRefreshed(ILootrInfoProvider provider) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot determine the refresh value for " + provider.getInfoUUID() + ".");
      return false;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, REFRESHES);
    return data.isComplete(provider.getInfoUUID());
  }

  @ApiStatus.Internal
  public static void setRefreshing(ILootrInfoProvider provider, int decay) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot set the refresh value for " + provider.getInfoUUID() + ".");
      return;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, REFRESHES);
    data.setValue(provider.getInfoUUID(), decay);
  }

  @ApiStatus.Internal
  public static void removeRefreshed(ILootrInfoProvider provider) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr remove the refresh value for " + provider.getInfoUUID() + ".");
      return;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, REFRESHES);
    data.remove(provider.getInfoUUID());
  }

  @ApiStatus.Internal
  public static LootrSavedData getData(ILootrInfoProvider provider) {
    // TODO: Refresh data from provider
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot fetch data for " + provider.getInfoDimension() + " at " + provider.getInfoPos() + " with ID " + provider.getInfoUUID() + " and cannot continue.");
      return null;
    }
    LootrSavedData result = manager.computeIfAbsent(new SavedData.Factory<>(LootrSavedData.fromInfo(provider), LootrSavedData::load), provider.getInfoKey());
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
      // TODO: Errors?
      return false;
    }

    Path dataPath = server.getWorldPath(new LevelResource("data")).resolve("lootr");
    List<String> files = new ArrayList<>();
    try (Stream<Path> paths = Files.walk(dataPath)) {
      paths.forEach(path -> {
        if (Files.isRegularFile(path)) {
          String fileName = path.getFileName().toString();
          if (fileName.startsWith("Lootr-")) {
            return;
          }
          files.add("lootr/" + fileName.charAt(0) + "/" + fileName.substring(0, 2) + "/" + fileName.replace(".dat", ""));
        }
      });
    } catch (IOException e) {
      return false;
    }

    int count = 0;

    for (String file : files) {
      SavedData datum = data.get(new SavedData.Factory<>(() -> LootrDummyData.INSTANCE, LootrSavedData::load), file);
      if (datum == LootrDummyData.INSTANCE) {
        // Failed to load so clear it from the cache
        LootrAPI.LOG.error("Failed to load data for " + file + ", removing from cache.");
        ((MixinDimensionDataStorage) data).getCache().remove(file);
        continue;
      }
      if (!(datum instanceof LootrSavedData lootrSavedData)) {
        LootrAPI.LOG.error("Data for " + file + " is not a LootrSavedData instance.");
        ((MixinDimensionDataStorage) data).getCache().remove(file);
        continue;
      }

      if (lootrSavedData.clearInventories(id)) {
        count++;
      }
    }

    if (count > 0) {
      data.save();
      LootrAPI.LOG.info("Cleared " + count + " inventories for play UUID " + id.toString());
      return true;
    }

    return false;
  }

  private static class LootrDummyData extends SavedData {
    public static final LootrDummyData INSTANCE = new LootrDummyData();

    public LootrDummyData() {
      super();
    }

    @Override
    public CompoundTag save(CompoundTag p_77763_, HolderLookup.Provider p_323640_) {
      return null;
    }
  }
}
