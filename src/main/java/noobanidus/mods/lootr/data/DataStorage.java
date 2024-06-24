package noobanidus.mods.lootr.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.api.LootFiller;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class DataStorage {
  public static final String ADVANCEMENTS = "lootr/Lootr-AdvancementData";
  public static final String SCORES = "lootr/Lootr-ScoreData";
  public static final String DECAYS = "lootr/Lootr-DecayData";
  public static final String REFRESHES = "lootr/Lootr-RefreshData";

  @Nullable
  public static DimensionDataStorage getDataStorage() {
    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
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

  // Deprecated: use getContainerData instead.
  @Deprecated(since = "1.20.0", forRemoval = true)
  public static ChestData getInstanceUuid(ServerLevel world, BlockPos pos, UUID id) {
    return getContainerData(world, pos, id);
  }

  public static ChestData getContainerData(ServerLevel world, BlockPos pos, UUID id) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot fetch chest data for " + world.dimension() + " at " + pos.toString() + " with ID " + id.toString() + " and cannot continue.");
      return null;
    }
    int size;
    BlockEntity be = world.getBlockEntity(pos);
    if (be == null) {
      LootrAPI.LOG.error("The block entity with id '" + id.toString() + "' in '" + world.dimension() + "' at '" + pos + "' is null.");
    }
    if (be instanceof Container bce) {
      size = bce.getContainerSize();
    } else {
      LootrAPI.LOG.error("We have no heuristic to determine the size of '" + id.toString() + "' in '" + world.dimension() + "' at '" + pos + "'. Defaulting to 27.");
      size = 27;
    }
    return ChestData.unwrap(manager.computeIfAbsent(new SavedData.Factory<>(ChestData.id(world.dimension(), pos, id), ChestData.loadWrapper(id, world.dimension(), pos)), ChestData.ID(id)), id, world.dimension(), pos, size);
  }

  // Deprecated: use getEntityData instead.
  @Deprecated(since = "1.20.0", forRemoval = true)
  public static ChestData getInstance(ServerLevel world, BlockPos pos, UUID id) {
    return getEntityData(world, pos, id);
  }

  public static ChestData getEntityData(ServerLevel world, BlockPos pos, UUID id) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot fetch chest data for " + world.dimension() + " at " + pos.toString() + " with ID " + id.toString() + " and cannot continue.");
      return null;
    }
    Entity entity = world.getEntity(id);
    int size;
    if (entity == null) {
      LootrAPI.LOG.error("The entity with id '" + id + "' in '" + world.dimension() + "' at '" + pos + "' is null.");
    }
    if (entity instanceof Container container) {
      size = container.getContainerSize();
    } else {
      LootrAPI.LOG.error("We have no heuristic to determine the size of entity '" + id + "' in '" + world.dimension() + "' at '" + pos + "'. Defaulting to 27.");
      size = 27;
    }
    return ChestData.unwrap(manager.computeIfAbsent(new SavedData.Factory<>(ChestData.entity(world.dimension(), pos, id), ChestData.loadWrapper(id, world.dimension(), pos)), ChestData.ID(id)), id, world.dimension(), pos, size);
  }

  // Deprecated: use getReferenceContainerData instead.
  @Deprecated(since = "1.20.0", forRemoval = true)
  public static ChestData getInstanceInventory(ServerLevel world, BlockPos pos, UUID id, NonNullList<ItemStack> base) {
    return getReferenceContainerData(world, pos, id, base);
  }

  public static ChestData getReferenceContainerData(ServerLevel world, BlockPos pos, UUID id, NonNullList<ItemStack> base) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    if (manager == null) {
      LootrAPI.LOG.error("DataStorage is null at this stage; Lootr cannot fetch chest data for " + world.dimension() + " at " + pos.toString() + " with ID " + id.toString() + " and cannot continue.");
      return null;
    }
    return ChestData.unwrap(manager.computeIfAbsent(new SavedData.Factory<>(ChestData.ref_id(world.dimension(), pos, id, base), ChestData.loadWrapper(id, world.dimension(), pos)), ChestData.ID(id)), id, world.dimension(), pos, base.size());
  }

  @Nullable
  public static SpecialChestInventory getInventory(Level level, UUID uuid, BlockPos pos, ServerPlayer player, IntSupplier sizeSupplier, Supplier<Component> displaySupplier, LootFiller filler, Supplier<ResourceKey<LootTable>> tableSupplier, LongSupplier seedSupplier) {
    if (level.isClientSide() || !(level instanceof ServerLevel)) {
      return null;
    }

    ChestData data = getContainerData((ServerLevel) level, pos, uuid);
    if (data == null) {
      // Error messages are already generated by `getContainerData`
      return null;
    }
    SpecialChestInventory inventory = data.getInventory(player);
    if (inventory == null) {
      inventory = data.createInventory(player, filler, sizeSupplier, displaySupplier, tableSupplier, seedSupplier);
    }

    return inventory;
  }

  @Nullable
  public static SpecialChestInventory getInventory(Level level, UUID uuid, BlockPos pos, ServerPlayer player, BaseContainerBlockEntity blockEntity, LootFiller filler, Supplier<ResourceKey<LootTable>> tableSupplier, LongSupplier seedSupplier) {
    if (level.isClientSide() || !(level instanceof ServerLevel)) {
      return null;
    }

    ChestData data = getContainerData((ServerLevel) level, pos, uuid);
    if (data == null) {
      // Error messages are already generated by `getContainerData`
      return null;
    }
    SpecialChestInventory inventory = data.getInventory(player);
    if (inventory == null) {
      inventory = data.createInventory(player, filler, blockEntity, tableSupplier, seedSupplier);
    }

    return inventory;
  }

  @Nullable
  public static SpecialChestInventory getInventory(Level world, UUID uuid, BlockPos pos, ServerPlayer player, RandomizableContainerBlockEntity tile, LootFiller filler) {
    if (world.isClientSide || !(world instanceof ServerLevel)) {
      return null;
    }

    ChestData data = getContainerData((ServerLevel) world, pos, uuid);
    if (data == null) {
      // Error messages are already generated by `getContainerData`
      return null;
    }
    SpecialChestInventory inventory = data.getInventory(player);
    if (inventory == null) {
      inventory = data.createInventory(player, filler, tile);
    }

    return inventory;
  }

  @Nullable
  public static SpecialChestInventory getInventory(Level world, UUID uuid, NonNullList<ItemStack> base, ServerPlayer player, BlockPos pos, RandomizableContainerBlockEntity tile) {
    if (world.isClientSide || !(world instanceof ServerLevel)) {
      return null;
    }
    ChestData data = getReferenceContainerData((ServerLevel) world, pos, uuid, base);
    if (data == null) {
      // Error messages are already generated by `getReferenceContainerData`
      return null;
    }
    SpecialChestInventory inventory = data.getInventory(player);
    if (inventory == null) {
      inventory = data.createInventory(player, data.customInventory(), tile);
    }

    return inventory;
  }

  // TODO: This is non-optimal and can result in poorly loaded containers.
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
      ChestData chestData = data.get(new SavedData.Factory<>(() -> {
        throw new UnsupportedOperationException("Cannot create ChestData here");
      }, ChestData::load), id);
      if (chestData != null) {
        if (chestData.clearInventory(uuid)) {
          cleared++;
          chestData.setDirty();
        }
      }
    }
    LootrAPI.LOG.info("Cleared " + cleared + " inventories for play UUID " + uuid.toString());
    return cleared != 0;
  }

  @Nullable
  public static SpecialChestInventory getInventory(Level world, LootrChestMinecartEntity cart, ServerPlayer player, LootFiller filler) {
    if (world.isClientSide || !(world instanceof ServerLevel)) {
      return null;
    }

    ChestData data = getEntityData((ServerLevel) world, cart.blockPosition(), cart.getUUID());
    if (data == null) {
      // Error messages are already generated by `getEntityData`
      return null;
    }
    SpecialChestInventory inventory = data.getInventory(player);
    if (inventory == null) {
      inventory = data.createInventory(player, filler, null);
    }

    return inventory;
  }

  public static void refreshInventory(Level level, BlockPos pos, UUID uuid, ServerPlayer player) {
    if (level.isClientSide() || !(level instanceof ServerLevel)) {
      return;
    }

    ChestData data = getContainerData((ServerLevel) level, pos, uuid);
    if (data == null) {
      // Error messages are already generated by `getContainerData`
      return;
    }
    data.clear();
    data.setDirty();
  }

  public static void refreshInventory(Level world, BlockPos pos, UUID uuid, NonNullList<ItemStack> base, ServerPlayer player) {
    if (world.isClientSide() || !(world instanceof ServerLevel)) {
      return;
    }
    ChestData data = getReferenceContainerData((ServerLevel) world, pos, uuid, base);
    if (data == null) {
      // Error messages are already generated by `getContainerReferenceData`
      return;
    }
    data.clear();
    data.setDirty();
  }

  public static void refreshInventory(Level world, LootrChestMinecartEntity cart, ServerPlayer player) {
    if (world.isClientSide() || !(world instanceof ServerLevel)) {
      return;
    }

    ChestData data = getEntityData((ServerLevel) world, cart.blockPosition(), cart.getUUID());
    if (data == null) {
      // Error messages are already generated by `getEntityData`
      return;
    }
    data.clear();
    data.setDirty();
  }
}
