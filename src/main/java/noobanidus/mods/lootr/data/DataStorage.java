package noobanidus.mods.lootr.data;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
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
import java.util.stream.Stream;

public class DataStorage {
  public static final String ID = "Lootr-AdvancementData";
  public static final String SCORED = "Lootr-ScoreData";
  public static final String DECAY = "Lootr-DecayData";
  public static final String REFRESH = "Lootr-RefreshData";

  public static boolean isAwarded(UUID player, UUID tileId) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    AdvancementData data = manager.computeIfAbsent(() -> new AdvancementData(ID), ID);

    return data.contains(player, tileId);
  }

  public static void award(UUID player, UUID tileId) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    AdvancementData data = manager.computeIfAbsent(() -> new AdvancementData(ID), ID);
    data.add(player, tileId);
    data.setDirty();
    manager.save();
  }

  public static boolean isScored(UUID player, UUID tileId) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    AdvancementData data = manager.computeIfAbsent(() -> new AdvancementData(SCORED), SCORED);

    return data.contains(player, tileId);
  }

  public static void score(UUID player, UUID tileId) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    AdvancementData data = manager.computeIfAbsent(() -> new AdvancementData(SCORED), SCORED);
    data.add(player, tileId);
    data.setDirty();
    manager.save();
  }

  public static int getDecayValue(UUID id) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    TickingData data = manager.computeIfAbsent(() -> new TickingData(DECAY), DECAY);
    return data.getValue(id);
  }

  public static boolean isDecayed(UUID id) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    TickingData data = manager.computeIfAbsent(() -> new TickingData(DECAY), DECAY);
    return data.isDone(id);
  }

  public static void setDecaying(UUID id, int decay) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    TickingData data = manager.computeIfAbsent(() -> new TickingData(DECAY), DECAY);
    data.setValue(id, decay);
    data.setDirty();
    manager.save();
  }

  public static void removeDecayed(UUID id) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    TickingData data = manager.computeIfAbsent(() -> new TickingData(DECAY), DECAY);
    if (data.removeDone(id) != -1) {
      data.setDirty();
      manager.save();
    }
  }

  public static void doDecay() {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    TickingData data = manager.computeIfAbsent(() -> new TickingData(DECAY), DECAY);
    if (data.tick()) {
      data.setDirty();
      manager.save();
    }
  }

  public static int getRefreshValue(UUID id) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    TickingData data = manager.computeIfAbsent(() -> new TickingData(REFRESH), REFRESH);
    return data.getValue(id);
  }

  public static boolean isRefreshed(UUID id) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    TickingData data = manager.computeIfAbsent(() -> new TickingData(REFRESH), REFRESH);
    return data.isDone(id);
  }

  public static void setRefreshing(UUID id, int decay) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    TickingData data = manager.computeIfAbsent(() -> new TickingData(REFRESH), REFRESH);
    data.setValue(id, decay);
    data.setDirty();
    manager.save();
  }

  public static void removeRefreshed(UUID id) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    TickingData data = manager.computeIfAbsent(() -> new TickingData(REFRESH), REFRESH);
    if (data.removeDone(id) != -1) {
      data.setDirty();
      manager.save();
    }
  }

  public static void doRefresh() {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    TickingData data = manager.computeIfAbsent(() -> new TickingData(REFRESH), REFRESH);
    if (data.tick()) {
      data.setDirty();
      manager.save();
    }
  }

  public static ServerWorld getServerWorld() {
    return ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD);
  }

  public static ChestData getInstancePos(ServerWorld world, BlockPos pos) {
    RegistryKey<World> dimension = world.dimension();
    return getServerWorld().getDataStorage().get(() -> new ChestData(dimension, pos), ChestData.OLD_ID(dimension, pos));
  }

  public static ChestData getInstanceUuid(ServerWorld world, UUID id) {
    RegistryKey<World> dimension = world.dimension();
    return getServerWorld().getDataStorage().computeIfAbsent(() -> new ChestData(dimension, id), ChestData.ID(dimension, id));
  }

  public static ChestData getInstance(ServerWorld world, UUID id) {
    return getServerWorld().getDataStorage().computeIfAbsent(() -> new ChestData(id), ChestData.ENTITY(id));
  }

  public static ChestData getInstanceInventory(ServerWorld world, UUID id, @Nullable UUID customId, @Nullable NonNullList<ItemStack> base) {
    RegistryKey<World> dimension = world.dimension();
    return getServerWorld().getDataStorage().computeIfAbsent(() -> new ChestData(dimension, id, customId, base), ChestData.REF_ID(dimension, id));
  }

  @Nullable
  public static SpecialChestInventory getInventory(World world, UUID uuid, BlockPos pos, ServerPlayerEntity player, LockableLootTileEntity tile, LootFiller filler) {
    if (world.isClientSide || !(world instanceof ServerWorld)) {
      return null;
    }

    ChestData data = getInstanceUuid((ServerWorld) world, uuid);
    ChestData oldData = getInstancePos((ServerWorld) world, pos);
    if (oldData != null) {
      Map<UUID, SpecialChestInventory> inventories = data.getInventories();
      inventories.putAll(oldData.getInventories());
      data.setInventories(inventories);
      oldData.clear();
      oldData.setDirty();
    }
    SpecialChestInventory inventory = data.getInventory(player, pos);
    if (inventory == null) {
      inventory = data.createInventory(player, filler, tile);
      inventory.setBlockPos(pos);
    }

    return inventory;
  }

  @Nullable
  public static void refreshInventory(World world, UUID uuid, ServerPlayerEntity player) {
    if (world.isClientSide || !(world instanceof ServerWorld)) {
      return;
    }

    ChestData data = getInstanceUuid((ServerWorld) world, uuid);
    data.clearInventory(player.getUUID());
    data.setDirty();
  }

  @Nullable
  public static SpecialChestInventory getInventory(World world, UUID uuid, NonNullList<ItemStack> base, ServerPlayerEntity player, BlockPos pos, LockableLootTileEntity tile) {
    if (world.isClientSide || !(world instanceof ServerWorld)) {
      return null;
    }
    ChestData data = getInstanceInventory((ServerWorld) world, uuid, null, base);
    SpecialChestInventory inventory = data.getInventory(player, pos);
    if (inventory == null) {
      inventory = data.createInventory(player, data.customInventory(), tile);
      inventory.setBlockPos(pos);
    }

    return inventory;
  }

  @Nullable
  public static void refreshInventory(World world, UUID uuid, NonNullList<ItemStack> base, ServerPlayerEntity player) {
    if (world.isClientSide || !(world instanceof ServerWorld)) {
      return;
    }
    ChestData data = getInstanceInventory((ServerWorld) world, uuid, null, base);
    data.clearInventory(player.getUUID());
    data.setDirty();
  }

  public static boolean clearInventories(ServerPlayerEntity player) {
    return clearInventories(player.getUUID());
  }

  public static boolean clearInventories(UUID uuid) {
    ServerWorld world = getServerWorld();
    DimensionSavedDataManager data = world.getDataStorage();
    Path dataPath = world.getServer().getWorldPath(new FolderName("data"));

    List<String> ids = new ArrayList<>();
    try (Stream<Path> paths = Files.walk(dataPath)) {
      paths.forEach(o -> {
        if (Files.isRegularFile(o)) {
          String name = o.getFileName().toString();
          if (name.startsWith("Lootr-") && !name.endsWith("Data.dat")) {
            ids.add(name.replace(".dat", ""));
          }
        }
      });
    } catch (IOException e) {
      return false;
    }

    int cleared = 0;
    for (String id : ids) {
      ChestData chestData = data.get(() -> null, id);
      if (chestData != null) {
        if (chestData.clearInventory(uuid)) {
          cleared++;
          chestData.setDirty();
        }
      }
    }
    data.save();
    Lootr.LOG.info("Cleared " + cleared + " inventories for play UUID " + uuid.toString());
    return cleared != 0;
  }

  @Nullable
  public static SpecialChestInventory getInventory(World world, LootrChestMinecartEntity cart, ServerPlayerEntity player, LootFiller filler) {
    if (world.isClientSide || !(world instanceof ServerWorld)) {
      return null;
    }

    ChestData data = getInstance((ServerWorld) world, cart.getUUID());
    SpecialChestInventory inventory = data.getInventory(player, null);
    if (inventory == null) {
      inventory = data.createInventory(player, filler, null);
    }

    return inventory;
  }

  @Nullable
  public static void refreshInventory(World world, LootrChestMinecartEntity cart, ServerPlayerEntity player) {
    if (world.isClientSide || !(world instanceof ServerWorld)) {
      return;
    }

    ChestData data = getInstance((ServerWorld) world, cart.getUUID());
    data.clearInventory(player.getUUID());
    data.setDirty();
  }

  public static void wipeInventory(ServerWorld world, BlockPos pos) {
    ServerWorld serverWorld = getServerWorld();
    RegistryKey<World> dimension = world.dimension();
    DimensionSavedDataManager manager = serverWorld.getDataStorage();
    String id = ChestData.OLD_ID(dimension, pos);
    if (!manager.cache.containsKey(id)) {
      return;
    }
    ChestData data = manager.get(() -> null, id);
    if (data != null) {
      data.clear();
      data.setDirty();
    }
  }

  public static void deleteLootChest(ServerWorld world, BlockPos pos) {
    if (world.isClientSide()) {
      return;
    }
    wipeInventory(world, pos);
    getServerWorld().getDataStorage().save();
  }
}
