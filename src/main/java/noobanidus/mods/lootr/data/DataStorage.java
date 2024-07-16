package noobanidus.mods.lootr.data;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.data.AdvancementData;
import noobanidus.mods.lootr.api.data.ILootrInfoProvider;
import noobanidus.mods.lootr.api.data.LootFiller;
import noobanidus.mods.lootr.api.data.TickingData;
import noobanidus.mods.lootr.api.data.inventory.ILootrInventory;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SuppressWarnings("unused")
public class DataStorage {
  public static final String ADVANCEMENTS = "lootr/Lootr-AdvancementData";
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

  public static boolean isAwarded(ILootrInfoProvider provider, ServerPlayer player) {
    return isAwarded(provider.getInfoUUID(), player);
  }

  public static boolean isAwarded (UUID uuid, ServerPlayer player) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot determine if advancement has been awarded.");
      return false;
    }
    AdvancementData data = manager.computeIfAbsent(AdvancementData.FACTORY, ADVANCEMENTS);
    return data.contains(player.getUUID(), uuid);
  }

  public static void award(ILootrInfoProvider provider, ServerPlayer player) {
    award(provider.getInfoUUID(), player);
  }

  public static void award (UUID id, ServerPlayer player) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot award advancement.");
      return;
    }
    AdvancementData data = manager.computeIfAbsent(AdvancementData.FACTORY, ADVANCEMENTS);
    data.add(player.getUUID(), id);
  }

  public static int getDecayValue(ILootrInfoProvider provider) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot determine the decay value for " + provider.getInfoUUID() + ".");
      return -1;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, DECAYS);
    return data.getValue(provider.getInfoUUID());
  }

  public static boolean isDecayed(ILootrInfoProvider provider) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot determine the decay value for " + provider.getInfoUUID() + ".");
      return false;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, DECAYS);
    return data.isComplete(provider.getInfoUUID());
  }

  public static void setDecaying(ILootrInfoProvider provider, int decay) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot set the decay value for " + provider.getInfoUUID() + ".");
      return;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, DECAYS);
    data.setValue(provider.getInfoUUID(), decay);
  }

  public static void removeDecayed(ILootrInfoProvider provider) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr remove the decay value for " + provider.getInfoUUID() + ".");
      return;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, DECAYS);
    data.remove(provider.getInfoUUID());
  }

  public static void doTick() {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot iterate and tick decay.");
      return;
    }
    manager.computeIfAbsent(TickingData.FACTORY, DECAYS).tick();
    manager.computeIfAbsent(TickingData.FACTORY, REFRESHES).tick();
  }

  public static int getRefreshValue(ILootrInfoProvider provider) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot determine the refresh value for " + provider.getInfoUUID() + ".");
      return -1;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, REFRESHES);
    return data.getValue(provider.getInfoUUID());
  }

  public static boolean isRefreshed(ILootrInfoProvider provider) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot determine the refresh value for " + provider.getInfoUUID() + ".");
      return false;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, REFRESHES);
    return data.isComplete(provider.getInfoUUID());
  }

  public static void setRefreshing(ILootrInfoProvider provider, int decay) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot set the refresh value for " + provider.getInfoUUID() + ".");
      return;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, REFRESHES);
    data.setValue(provider.getInfoUUID(), decay);
  }

  public static void removeRefreshed(ILootrInfoProvider provider) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr remove the refresh value for " + provider.getInfoUUID() + ".");
      return;
    }
    TickingData data = manager.computeIfAbsent(TickingData.FACTORY, REFRESHES);
    data.remove(provider.getInfoUUID());
  }

  public static LootrSavedData getData(ILootrInfoProvider provider) {
    // TODO: Refresh data from provider
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot fetch data for " + provider.getInfoDimension() + " at " + provider.getInfoPos() + " with ID " + provider.getInfoUUID() + " and cannot continue.");
      return null;
    }
    return manager.computeIfAbsent(new SavedData.Factory<>(LootrSavedData.fromInfo(provider), LootrSavedData::load), provider.getInfoKey());
  }

  @Nullable
  public static ILootrInventory getInventory(ILootrInfoProvider provider, ServerPlayer player, LootFiller filler) {
    LootrSavedData data = getData(provider);
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
    LootrSavedData data = getData(provider);
    if (data == null) {
      // Error messages are already generated by `getData`
      return;
    }
    data.clearInventories();
    data.setDirty();
  }
}
