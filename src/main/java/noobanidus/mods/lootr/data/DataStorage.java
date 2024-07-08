package noobanidus.mods.lootr.data;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import noobanidus.mods.lootr.api.LootFiller;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.info.ILootrInfoProvider;
import noobanidus.mods.lootr.api.inventory.ILootrInventory;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SuppressWarnings("unused")
public class DataStorage {
  public static final String ADVANCEMENTS = "lootr/Lootr-AdvancementData";
  public static final String SCORES = "lootr/Lootr-ScoreData";
  public static final String DECAYS = "lootr/Lootr-DecayData";
  public static final String REFRESHES = "lootr/Lootr-RefreshData";

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

  public static boolean isAwarded(UUID player, UUID tileId) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot determine if advancement has been awarded.");
      return false;
    }
    AdvancementData data = manager.computeIfAbsent(AdvancementData.FACTORY, ADVANCEMENTS);
    return data.contains(player, tileId);
  }

  public static void award(UUID player, UUID tileId) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot award advancement.");
      return;
    }
    AdvancementData data = manager.computeIfAbsent(AdvancementData.FACTORY, ADVANCEMENTS);
    data.add(player, tileId);
    data.setDirty();
  }

  public static boolean isScored(UUID player, UUID tileId) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot determine if block entity has been scored.");
      return false;
    }
    AdvancementData data = manager.computeIfAbsent(AdvancementData.FACTORY, SCORES);
    return data.contains(player, tileId);
  }

  public static void score(UUID player, UUID tileId) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot score block entities at this time.");
      return;
    }
    AdvancementData data = manager.computeIfAbsent(AdvancementData.FACTORY, SCORES);
    data.add(player, tileId);
    data.setDirty();
  }

  public static int getDecayValue(UUID id) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot determine the decay value for " + id.toString() + ".");
      return -1;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, DECAYS);
    return data.getValue(id);
  }

  public static boolean isDecayed(UUID id) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot determine the decay value for " + id.toString() + ".");
      return false;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, DECAYS);
    return data.isComplete(id);
  }

  public static void setDecaying(UUID id, int decay) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot set the decay value for " + id.toString() + ".");
      return;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, DECAYS);
    data.setValue(id, decay);
    data.setDirty();
  }

  public static void removeDecayed(UUID id) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr remove the decay value for " + id.toString() + ".");
      return;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, DECAYS);
    if (data.remove(id) != -1) {
      data.setDirty();
    }
  }

  public static void doDecay() {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot iterate and tick decay.");
      return;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, DECAYS);
    if (data.tick()) {
      data.setDirty();
    }
  }

  public static int getRefreshValue(UUID id) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot determine the refresh value for " + id.toString() + ".");
      return -1;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, REFRESHES);
    return data.getValue(id);
  }

  public static boolean isRefreshed(UUID id) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot determine the refresh value for " + id.toString() + ".");
      return false;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, REFRESHES);
    return data.isComplete(id);
  }

  public static void setRefreshing(UUID id, int decay) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot set the refresh value for " + id.toString() + ".");
      return;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, REFRESHES);
    data.setValue(id, decay);
    data.setDirty();
  }

  public static void removeRefreshed(UUID id) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr remove the refresh value for " + id.toString() + ".");
      return;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, REFRESHES);
    if (data.remove(id) != -1) {
      data.setDirty();
    }
  }

  public static void doRefresh() {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot iterate and tick refresh.");
      return;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, REFRESHES);
    if (data.tick()) {
      data.setDirty();
    }
  }

  public static LootrSavedInfo getData(ILootrInfoProvider provider) {
    // TODO: Refresh data from provider
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot fetch data for " + provider.getInfoDimension() + " at " + provider.getInfoPos() + " with ID " + provider.getInfoUUID() + " and cannot continue.");
      return null;
    }
    return manager.computeIfAbsent(new SavedData.Factory<>(LootrSavedInfo.fromInfo(provider), LootrSavedInfo::load), provider.getInfoKey());
  }

  @Nullable
  public static ILootrInventory getInventory(ILootrInfoProvider provider, ServerPlayer player, LootFiller filler) {
    LootrSavedInfo data = getData(provider);
    if (data == null) {
      // Error messages are already generated by `getData`
      return null;
    }
    ILootrInventory inventory = data.getInventory(player);
    if (inventory == null) {
      inventory = data.createInventory(provider, player, filler);
    }

    return inventory;
  }

/*  // TODO: This is non-optimal and can result in poorly loaded containers.
  public static boolean clearInventories(UUID uuid) {
    DimensionDataStorage data = getDataStorage();
    if (data == null) {
      // Errors are already generated in `getDataStorage`
      return false;
    }
    // Server being null is already handled in `getDataStorage`
    @SuppressWarnings("resource") ServerLevel world = ServerLifecycleHooks.getCurrentServer().overworld();
    // This can actually be null on occasion.
    //noinspection ConstantValue
    if (world == null) {
      LootrAPI.LOG.error("Overworld is null while attempting to clear inventories for '" + uuid.toString() + "'; Lootr cannot clear inventories.");
      return false;
    }
    Path dataPath = world.getServer().getWorldPath(new LevelResource("data")).resolve("lootr");

    List<String> ids = new ArrayList<>();
    try (Stream<Path> paths = Files.walk(dataPath)) {
      paths.forEach(o -> {
        if (Files.isRegularFile(o)) {
          String fileName = o.getFileName().toString();
          if (fileName.startsWith("Lootr-")) {
            return;
          }
          ids.add("lootr/" + fileName.charAt(0) + "/" + fileName.substring(0, 2) + "/" + fileName.replace(".dat", ""));
        }
      });
    } catch (IOException e) {
      return false;
    }

    int cleared = 0;
    for (String id : ids) {
      OldChestData chestData = data.get(new SavedData.Factory<>(() -> {
        throw new UnsupportedOperationException("Cannot create ChestData here");
      }, OldChestData::load), id);
      if (chestData != null) {
        if (chestData.clearInventory(uuid)) {
          cleared++;
          chestData.setDirty();
        }
      }
    }
    LootrAPI.LOG.info("Cleared " + cleared + " inventories for play UUID " + uuid.toString());
    return cleared != 0;
  }*/

  public static void refreshInventory(ILootrInfoProvider provider) {
    LootrSavedInfo data = getData(provider);
    if (data == null) {
      // Error messages are already generated by `getData`
      return;
    }
    data.clearInventories();
    data.setDirty();
  }
}
