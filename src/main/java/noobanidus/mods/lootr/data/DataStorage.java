package noobanidus.mods.lootr.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraft.world.level.storage.LevelResource;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.LootFiller;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.LongSupplier;
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

  public static DimensionDataStorage getDataStorage () {
    return ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage();
  }

  public static boolean isAwarded(UUID player, UUID tileId) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    AdvancementData data = manager.computeIfAbsent(AdvancementData::load, AdvancementData::new, ID);
    return data.contains(player, tileId);
  }

  public static void award(UUID player, UUID tileId) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    AdvancementData data = manager.computeIfAbsent(AdvancementData::load, AdvancementData::new, ID);
    data.add(player, tileId);
    data.setDirty();
  }

  public static boolean isScored(UUID player, UUID tileId) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    AdvancementData data = manager.computeIfAbsent(AdvancementData::load, AdvancementData::new, SCORED);
    return data.contains(player, tileId);
  }

  public static void score(UUID player, UUID tileId) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    AdvancementData data = manager.computeIfAbsent(AdvancementData::load, AdvancementData::new, SCORED);
    data.add(player, tileId);
    data.setDirty();
  }

  public static int getDecayValue (UUID id) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    TickingData data = manager.computeIfAbsent(TickingData::load, TickingData::new, DECAY);
    return data.getValue(id);
  }

  public static boolean isDecayed(UUID id) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    TickingData data = manager.computeIfAbsent(TickingData::load, TickingData::new, DECAY);
    return data.isComplete(id);
  }

  public static void setDecaying (UUID id, int decay) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    TickingData data = manager.computeIfAbsent(TickingData::load, TickingData::new, DECAY);
    data.setValue(id, decay);
    data.setDirty();
  }

  public static void removeDecayed (UUID id) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    TickingData data = manager.computeIfAbsent(TickingData::load, TickingData::new, DECAY);
    if (data.remove(id) != -1) {
      data.setDirty();
    }
  }

  public static void doDecay () {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    TickingData data = manager.computeIfAbsent(TickingData::load, TickingData::new, DECAY);
    if (data.tick()) {
      data.setDirty();
    }
  }

  public static int getRefreshValue (UUID id) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    TickingData data = manager.computeIfAbsent(TickingData::load, TickingData::new, REFRESH);
    return data.getValue(id);
  }

  public static boolean isRefreshed(UUID id) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    TickingData data = manager.computeIfAbsent(TickingData::load, TickingData::new, REFRESH);
    return data.isComplete(id);
  }

  public static void setRefreshing (UUID id, int decay) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    TickingData data = manager.computeIfAbsent(TickingData::load, TickingData::new, REFRESH);
    data.setValue(id, decay);
    data.setDirty();
  }

  public static void removeRefreshed (UUID id) {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    TickingData data = manager.computeIfAbsent(TickingData::load, TickingData::new, REFRESH);
    if (data.remove(id) != -1) {
      data.setDirty();
    }
  }

  public static void doRefresh () {
    DimensionDataStorage manager = DataStorage.getDataStorage();
    TickingData data = manager.computeIfAbsent(TickingData::load, TickingData::new, REFRESH);
    if (data.tick()) {
      data.setDirty();
    }
  }

  public static ChestData getInstanceUuid(ServerLevel world, UUID id) {
    ResourceKey<Level> dimension = world.dimension();
    return getDataStorage().computeIfAbsent(ChestData::load, ChestData.id(dimension, id), ChestData.ID(dimension, id));
  }

  public static ChestData getInstance(ServerLevel world, UUID id) {
    return getDataStorage().computeIfAbsent(ChestData::load, ChestData.entity(id), ChestData.ENTITY(id));
  }

  public static ChestData getInstanceInventory(ServerLevel world, UUID id, @Nullable UUID customId, @Nullable NonNullList<ItemStack> base) {
    ResourceKey<Level> dimension = world.dimension();
    return getDataStorage().computeIfAbsent(ChestData::load, ChestData.ref_id(dimension, id, customId, base), ChestData.REF_ID(dimension, id));
  }

  @Nullable
  public static SpecialChestInventory getInventory (Level level, UUID uuid, BlockPos pos, ServerPlayer player, RandomizableContainerBlockEntity blockEntity, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier) {
    if (level.isClientSide() || !(level instanceof ServerLevel)) {
      return null;
    }

    ChestData data = getInstanceUuid((ServerLevel) level, uuid);
    SpecialChestInventory inventory = data.getInventory(player, pos);
    if (inventory == null) {
      inventory = data.createInventory(player, filler, blockEntity, tableSupplier, seedSupplier);
      inventory.setBlockPos(pos);
    }

    return inventory;
  }

  @Nullable
  public static SpecialChestInventory getInventory(Level world, UUID uuid, BlockPos pos, ServerPlayer player, RandomizableContainerBlockEntity tile, LootFiller filler) {
    if (world.isClientSide || !(world instanceof ServerLevel)) {
      return null;
    }

    ChestData data = getInstanceUuid((ServerLevel) world, uuid);
    SpecialChestInventory inventory = data.getInventory(player, pos);
    if (inventory == null) {
      inventory = data.createInventory(player, filler, tile);
      inventory.setBlockPos(pos);
    }

    return inventory;
  }

  @Nullable
  public static SpecialChestInventory getInventory(Level world, UUID uuid, NonNullList<ItemStack> base, ServerPlayer player, BlockPos pos, RandomizableContainerBlockEntity tile) {
    if (world.isClientSide || !(world instanceof ServerLevel)) {
      return null;
    }
    ChestData data = getInstanceInventory((ServerLevel) world, uuid, null, base);
    SpecialChestInventory inventory = data.getInventory(player, pos);
    if (inventory == null) {
      inventory = data.createInventory(player, data.customInventory(), tile);
      inventory.setBlockPos(pos);
    }

    return inventory;
  }

  public static boolean clearInventories(ServerPlayer player) {
    return clearInventories(player.getUUID());
  }

  public static boolean clearInventories(UUID uuid) {
    ServerLevel world = ServerLifecycleHooks.getCurrentServer().overworld();
    DimensionDataStorage data = world.getDataStorage();
    Path dataPath = world.getServer().getWorldPath(new LevelResource("data"));

    List<String> ids = new ArrayList<>();
    try (Stream<Path> paths = Files.walk(dataPath)) {
      paths.forEach(o -> {
        if (Files.isRegularFile(o)) {
          String name = o.getFileName().toString();
          if (name.startsWith("Lootr-")) {
            ids.add(name.replace(".dat", ""));
          }
        }
      });
    } catch (IOException e) {
      return false;
    }

    int cleared = 0;
    for (String id : ids) {
      ChestData chestData = data.get(ChestData::load, id);
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
  public static SpecialChestInventory getInventory(Level world, LootrChestMinecartEntity cart, ServerPlayer player, LootFiller filler) {
    if (world.isClientSide || !(world instanceof ServerLevel)) {
      return null;
    }

    ChestData data = getInstance((ServerLevel) world, cart.getUUID());
    SpecialChestInventory inventory = data.getInventory(player, null);
    if (inventory == null) {
      inventory = data.createInventory(player, filler, null);
    }

    return inventory;
  }

  public static void refreshInventory(Level level, UUID uuid, ServerPlayer player) {
    if (level.isClientSide() || !(level instanceof ServerLevel)) {
      return;
    }

    ChestData data = getInstanceUuid((ServerLevel) level, uuid);
    data.clear();
    data.setDirty();
  }

  public static void refreshInventory(Level world, UUID uuid, NonNullList<ItemStack> base, ServerPlayer player) {
    if (world.isClientSide() || !(world instanceof ServerLevel)) {
      return;
    }
    ChestData data = getInstanceInventory((ServerLevel) world, uuid, null, base);
    data.clear();
    data.setDirty();
  }

  public static void refreshInventory(Level world, LootrChestMinecartEntity cart, ServerPlayer player) {
    if (world.isClientSide() || !(world instanceof ServerLevel)) {
      return;
    }

    ChestData data = getInstance((ServerLevel) world, cart.getUUID());
    data.clear();
    data.setDirty();
  }
}
