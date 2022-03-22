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
  public static final String ID_OLD = "Lootr-AdvancementData";
  public static final String SCORED_OLD = "Lootr-ScoreData";
  public static final String DECAY_OLD = "Lootr-DecayData";
  public static final String REFRESH_OLD = "Lootr-RefreshData";

  public static final String ID = "lootr/" + ID_OLD;
  public static final String SCORED = "lootr/" + SCORED_OLD;
  public static final String DECAY = "lootr/" + DECAY_OLD;
  public static final String REFRESH = "lootr/" + REFRESH_OLD;

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
  }

  public static void removeDecayed(UUID id) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    TickingData data = manager.computeIfAbsent(() -> new TickingData(DECAY), DECAY);
    if (data.removeDone(id) != -1) {
      data.setDirty();
    }
  }

  public static void doDecay() {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    TickingData data = manager.computeIfAbsent(() -> new TickingData(DECAY), DECAY);
    if (data.tick()) {
      data.setDirty();
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
  }

  public static void removeRefreshed(UUID id) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    TickingData data = manager.computeIfAbsent(() -> new TickingData(REFRESH), REFRESH);
    if (data.removeDone(id) != -1) {
      data.setDirty();
    }
  }

  public static void doRefresh() {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    TickingData data = manager.computeIfAbsent(() -> new TickingData(REFRESH), REFRESH);
    if (data.tick()) {
      data.setDirty();
    }
  }

  public static ServerWorld getServerWorld() {
    return ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD);
  }

  public static ChestData getInstanceUuid(ServerWorld world, UUID id, BlockPos position) {
    RegistryKey<World> dimension = world.dimension();
    return ChestData.unwrap(getServerWorld().getDataStorage().computeIfAbsent(() -> new ChestData(dimension, id), ChestData.ID(id)), dimension, position);
  }

  public static ChestData getInstance(ServerWorld world, UUID id, BlockPos pos) {
    return ChestData.unwrap(getServerWorld().getDataStorage().computeIfAbsent(() -> new ChestData(id), ChestData.ID(id)), world.dimension(), pos);
  }

  public static ChestData getInstanceInventory(ServerWorld world, UUID id, @Nullable UUID customId, @Nullable NonNullList<ItemStack> base, BlockPos pos) {
    RegistryKey<World> dimension = world.dimension();
    return ChestData.unwrap(getServerWorld().getDataStorage().computeIfAbsent(() -> new ChestData(dimension, id, customId, base), ChestData.ID(id)), world.dimension(), pos);
  }

  @Nullable
  public static SpecialChestInventory getInventory(World world, UUID uuid, BlockPos pos, ServerPlayerEntity player, LockableLootTileEntity tile, LootFiller filler) {
    if (world.isClientSide || !(world instanceof ServerWorld)) {
      return null;
    }

    ChestData data = getInstanceUuid((ServerWorld) world, uuid, pos);
    SpecialChestInventory inventory = data.getInventory(player);
    if (inventory == null) {
      inventory = data.createInventory(player, filler, tile);
    }

    return inventory;
  }

  public static void refreshInventory(World world, UUID uuid, ServerPlayerEntity player, BlockPos pos) {
    if (world.isClientSide || !(world instanceof ServerWorld)) {
      return;
    }

    ChestData data = getInstanceUuid((ServerWorld) world, uuid, pos);
    data.clear();
    data.setDirty();
  }

  @Nullable
  public static SpecialChestInventory getInventory(World world, UUID uuid, NonNullList<ItemStack> base, ServerPlayerEntity player, BlockPos pos, LockableLootTileEntity tile) {
    if (world.isClientSide || !(world instanceof ServerWorld)) {
      return null;
    }
    ChestData data = getInstanceInventory((ServerWorld) world, uuid, null, base, pos);
    SpecialChestInventory inventory = data.getInventory(player);
    if (inventory == null) {
      inventory = data.createInventory(player, data.customInventory(), tile);
    }

    return inventory;
  }

  @Nullable
  public static void refreshInventory(World world, UUID uuid, NonNullList<ItemStack> base, ServerPlayerEntity player, BlockPos pos) {
    if (world.isClientSide || !(world instanceof ServerWorld)) {
      return;
    }
    ChestData data = getInstanceInventory((ServerWorld) world, uuid, null, base, pos);
    data.clear();
    data.setDirty();
  }

  public static boolean clearInventories(UUID uuid) {
    ServerWorld world = getServerWorld();
    DimensionSavedDataManager data = world.getDataStorage();
    Path dataPath = world.getServer().getWorldPath(new FolderName("data")).resolve("lootr");

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
      ChestData chestData = data.get(() -> new ChestData(id), id);
      if (chestData != null) {
        if (chestData.clearInventory(uuid)) {
          cleared++;
          chestData.setDirty();
        }
      }
    }
    Lootr.LOG.info("Cleared " + cleared + " inventories for play UUID " + uuid.toString());
    return cleared != 0;
  }

  @Nullable
  public static SpecialChestInventory getInventory(World world, LootrChestMinecartEntity cart, ServerPlayerEntity player, LootFiller filler, BlockPos position) {
    if (world.isClientSide || !(world instanceof ServerWorld)) {
      return null;
    }

    ChestData data = getInstance((ServerWorld) world, cart.getUUID(), position);
    SpecialChestInventory inventory = data.getInventory(player);
    if (inventory == null) {
      inventory = data.createInventory(player, filler, null);
    }

    return inventory;
  }

  @Nullable
  public static void refreshInventory(World world, LootrChestMinecartEntity cart, ServerPlayerEntity player, BlockPos pos) {
    if (world.isClientSide || !(world instanceof ServerWorld)) {
      return;
    }

    ChestData data = getInstance((ServerWorld) world, cart.getUUID(), pos);
    data.clear();
    data.setDirty();
  }
}
