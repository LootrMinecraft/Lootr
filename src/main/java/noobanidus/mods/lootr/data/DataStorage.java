package noobanidus.mods.lootr.data;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.server.FMLServerHandler;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.LootFiller;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class DataStorage {
  public static final String ID_OLD = "Lootr-AdvancementData";
  public static final String SCORED_OLD = "Lootr-ScoreData";
  public static final String DECAY_OLD = "Lootr-DecayData";
  public static final String REFRESH_OLD = "Lootr-RefreshData";

  public static final String ID = "lootr/" + ID_OLD;
  public static final String SCORED = "lootr/" + SCORED_OLD;
  public static final String DECAY = "lootr/" + DECAY_OLD;
  public static final String REFRESH = "lootr/" + REFRESH_OLD;

  private static <T extends WorldSavedData> T computeIfAbsentManager(MapStorage manager, Supplier<T> supplier, String key) {
    T newData = supplier.get();
    T value = (T)manager.getOrLoadData(newData.getClass(), key);
    if(value == null) {
      manager.setData(key, newData);
      return newData;
    } else
      return value;
  }

  public static int getDecayValue(UUID id) {
    MapStorage manager = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getMapStorage();
    TickingData data = computeIfAbsentManager(manager, () -> new TickingData(DECAY), DECAY);
    return data.getValue(id);
  }

  public static boolean isDecayed(UUID id) {
    MapStorage manager = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getMapStorage();
    TickingData data = computeIfAbsentManager(manager, () -> new TickingData(DECAY), DECAY);
    return data.isDone(id);
  }

  public static void setDecaying(UUID id, int decay) {
    MapStorage manager = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getMapStorage();
    TickingData data = computeIfAbsentManager(manager, () -> new TickingData(DECAY), DECAY);
    data.setValue(id, decay);
    data.markDirty();
  }

  public static void removeDecayed(UUID id) {
    MapStorage manager = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getMapStorage();
    TickingData data = computeIfAbsentManager(manager, () -> new TickingData(DECAY), DECAY);
    if (data.removeDone(id) != -1) {
      data.markDirty();
    }
  }

  public static void doDecay() {
    MapStorage manager = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getMapStorage();
    TickingData data = computeIfAbsentManager(manager, () -> new TickingData(DECAY), DECAY);
    if (data.tick()) {
      data.markDirty();
    }
  }

  public static int getRefreshValue(UUID id) {
    MapStorage manager = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getMapStorage();
    TickingData data = computeIfAbsentManager(manager, () -> new TickingData(REFRESH), REFRESH);
    return data.getValue(id);
  }

  public static boolean isRefreshed(UUID id) {
    MapStorage manager = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getMapStorage();
    TickingData data = computeIfAbsentManager(manager, () -> new TickingData(REFRESH), REFRESH);
    return data.isDone(id);
  }

  public static void setRefreshing(UUID id, int decay) {
    MapStorage manager = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getMapStorage();
    TickingData data = computeIfAbsentManager(manager, () -> new TickingData(REFRESH), REFRESH);
    data.setValue(id, decay);
    data.markDirty();
  }

  public static void removeRefreshed(UUID id) {
    MapStorage manager = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getMapStorage();
    TickingData data = computeIfAbsentManager(manager, () -> new TickingData(REFRESH), REFRESH);
    if (data.removeDone(id) != -1) {
      data.markDirty();
    }
  }

  public static void doRefresh() {
    MapStorage manager = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getMapStorage();
    TickingData data = computeIfAbsentManager(manager, () -> new TickingData(REFRESH), REFRESH);
    if (data.tick()) {
      data.markDirty();
    }
  }

  public static WorldServer getWorldServer() {
    return FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);
  }

  public static ChestData getInstanceUuid(WorldServer world, UUID id, BlockPos position) {
    DimensionType dimension = world.provider.getDimensionType();
    return ChestData.unwrap(computeIfAbsentManager(getWorldServer().getMapStorage(),  () -> new ChestData(dimension, id), ChestData.ID(id)), dimension, position);
  }

  public static ChestData getInstance(WorldServer world, UUID id, BlockPos pos) {
    return ChestData.unwrap(computeIfAbsentManager(getWorldServer().getMapStorage(),  () -> new ChestData(id), ChestData.ID(id)), world.provider.getDimensionType(), pos);
  }

  public static ChestData getInstanceInventory(WorldServer world, UUID id, @Nullable UUID customId, @Nullable NonNullList<ItemStack> base, BlockPos pos) {
    DimensionType dimension = world.provider.getDimensionType();
    return ChestData.unwrap(computeIfAbsentManager(getWorldServer().getMapStorage(),  () -> new ChestData(dimension, id, customId, base), ChestData.ID(id)), world.provider.getDimensionType(), pos);
  }

  public static boolean clearInventories(UUID uuid) {
    WorldServer world = getWorldServer();
    MapStorage data = world.getMapStorage();
    Path dataPath = world.getSaveHandler().getWorldDirectory().toPath().resolve("data").resolve("lootr");

    List<String> ids = new ArrayList<>();
    // TODO: Improve
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
      ChestData chestData = (ChestData)data.getOrLoadData(ChestData.class, id);
      if (chestData != null) {
        if (chestData.clearInventory(uuid)) {
          cleared++;
          chestData.markDirty();
        }
      }
    }
    Lootr.LOG.info("Cleared " + cleared + " inventories for play UUID " + uuid.toString());
    return cleared != 0;
  }

  @Nullable
  public static SpecialChestInventory getInventory(World world, UUID uuid, BlockPos pos, EntityPlayerMP player, TileEntityLockableLoot tile, LootFiller filler) {
    if (world.isRemote || !(world instanceof WorldServer)) {
      return null;
    }

    ChestData data = getInstanceUuid((WorldServer) world, uuid, pos);
    SpecialChestInventory inventory = data.getInventory(player);
    if (inventory == null) {
      inventory = data.createInventory(player, filler, tile);
    }
    inventory.setBlockPos(pos);

    return inventory;
  }

  public static void refreshInventory(World world, UUID uuid, EntityPlayerMP player, BlockPos pos) {
    if (world.isRemote || !(world instanceof WorldServer)) {
      return;
    }

    ChestData data = getInstanceUuid((WorldServer) world, uuid, pos);
    data.clear();
    data.markDirty();
  }

  @Nullable
  public static SpecialChestInventory getInventory(World world, UUID uuid, NonNullList<ItemStack> base, EntityPlayerMP player, BlockPos pos, TileEntityLockableLoot tile) {
    if (world.isRemote || !(world instanceof WorldServer)) {
      return null;
    }
    ChestData data = getInstanceInventory((WorldServer) world, uuid, null, base, pos);
    SpecialChestInventory inventory = data.getInventory(player);
    if (inventory == null) {
      inventory = data.createInventory(player, data.customInventory(), tile);
    }

    inventory.setBlockPos(pos);

    return inventory;
  }

  @Nullable
  public static void refreshInventory(World world, UUID uuid, NonNullList<ItemStack> base, EntityPlayerMP player, BlockPos pos) {
    if (world.isRemote || !(world instanceof WorldServer)) {
      return;
    }
    ChestData data = getInstanceInventory((WorldServer) world, uuid, null, base, pos);
    data.clear();
    data.markDirty();
  }

  @Nullable
  public static SpecialChestInventory getInventory(World world, LootrChestMinecartEntity cart, EntityPlayerMP player, LootFiller filler, BlockPos position) {
    if (world.isRemote || !(world instanceof WorldServer)) {
      return null;
    }

    ChestData data = getInstance((WorldServer) world, cart.getUniqueID(), position);
    SpecialChestInventory inventory = data.getInventory(player);
    if (inventory == null) {
      inventory = data.createInventory(player, filler, null);
    }

    inventory.setBlockPos(position);

    return inventory;
  }

  @Nullable
  public static void refreshInventory(World world, LootrChestMinecartEntity cart, EntityPlayerMP player, BlockPos pos) {
    if (world.isRemote || !(world instanceof WorldServer)) {
      return;
    }

    ChestData data = getInstance((WorldServer) world, cart.getUniqueID(), pos);
    data.clear();
    data.markDirty();
  }
}
