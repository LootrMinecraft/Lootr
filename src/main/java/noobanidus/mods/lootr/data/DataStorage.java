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
import net.minecraftforge.event.TickEvent;
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

  public static boolean isAwarded(UUID player, UUID tileId) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    AdvancementData data = manager.computeIfAbsent(() -> new AdvancementData(ID), ID);

    return data.contains(player, tileId);
  }

  public static void award (UUID player, UUID tileId) {
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

  public static void score (UUID player, UUID tileId) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    AdvancementData data = manager.computeIfAbsent(() -> new AdvancementData(SCORED), SCORED);
    data.add(player, tileId);
    data.setDirty();
    manager.save();
  }

  public static int getDecayValue(UUID id) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    DecayingData data = manager.computeIfAbsent(() -> new DecayingData(DECAY), DECAY);
    return data.getDecay(id);
  }

  public static boolean isDecayed(UUID id) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    DecayingData data = manager.computeIfAbsent(() -> new DecayingData(DECAY), DECAY);
    return data.isDecayed(id);
  }

  public static boolean isDecaying (UUID id) {
    return getDecayValue(id) > 0;
  }

  public static void setDecaying (UUID id, int decay) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    DecayingData data = manager.computeIfAbsent(() -> new DecayingData(DECAY), DECAY);
    data.setDecay(id, decay);
    data.setDirty();
    manager.save();
  }

  public static void removeDecayed(UUID id) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    DecayingData data = manager.computeIfAbsent(() -> new DecayingData(DECAY), DECAY);
    if (data.removeDecayed(id) != -1) {
      data.setDirty();
      manager.save();
    }
  }

  public static void doDecay (TickEvent.ServerTickEvent event) {
    DimensionSavedDataManager manager = ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage();
    DecayingData data = manager.computeIfAbsent(() -> new DecayingData(DECAY), DECAY);
    if (data.tickDecay(event)) {
      data.setDirty();
      manager.save();
    }
  }

  public static ServerWorld getServerWorld() {
    return ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD);
  }

  public static NewChestData getInstancePos(ServerWorld world, BlockPos pos) {
    RegistryKey<World> dimension = world.dimension();
    return getServerWorld().getDataStorage().get(() -> new NewChestData(dimension, pos), NewChestData.OLD_ID(dimension, pos));
  }

  public static NewChestData getInstanceUuid(ServerWorld world, UUID id) {
    RegistryKey<World> dimension = world.dimension();
    return getServerWorld().getDataStorage().computeIfAbsent(() -> new NewChestData(dimension, id), NewChestData.ID(dimension, id));
  }

  public static NewChestData getInstance(ServerWorld world, UUID id) {
    return getServerWorld().getDataStorage().computeIfAbsent(() -> new NewChestData(id), NewChestData.ENTITY(id));
  }

  public static NewChestData getInstanceInventory(ServerWorld world, UUID id, @Nullable UUID customId, @Nullable NonNullList<ItemStack> base) {
    RegistryKey<World> dimension = world.dimension();
    return getServerWorld().getDataStorage().computeIfAbsent(() -> new NewChestData(dimension, id, customId, base), NewChestData.REF_ID(dimension, id));
  }

  @Nullable
  public static SpecialChestInventory getInventory(World world, UUID uuid, BlockPos pos, ServerPlayerEntity player, LockableLootTileEntity tile, LootFiller filler) {
    if (world.isClientSide || !(world instanceof ServerWorld)) {
      return null;
    }

    NewChestData data = getInstanceUuid((ServerWorld) world, uuid);
    NewChestData oldData = getInstancePos((ServerWorld) world, pos);
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
  public static SpecialChestInventory getInventory(World world, UUID uuid, NonNullList<ItemStack> base, ServerPlayerEntity player, BlockPos pos, LockableLootTileEntity tile) {
    if (world.isClientSide || !(world instanceof ServerWorld)) {
      return null;
    }
    NewChestData data = getInstanceInventory((ServerWorld) world, uuid, null, base);
    SpecialChestInventory inventory = data.getInventory(player, pos);
    if (inventory == null) {
      inventory = data.createInventory(player, data.customInventory(), tile);
      inventory.setBlockPos(pos);
    }

    return inventory;
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
      NewChestData chestData = data.get(() -> null, id);
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

    NewChestData data = getInstance((ServerWorld) world, cart.getUUID());
    SpecialChestInventory inventory = data.getInventory(player, null);
    if (inventory == null) {
      inventory = data.createInventory(player, filler, null);
    }

    return inventory;
  }

  public static void wipeInventory(ServerWorld world, BlockPos pos) {
    ServerWorld serverWorld = getServerWorld();
    RegistryKey<World> dimension = world.dimension();
    DimensionSavedDataManager manager = serverWorld.getDataStorage();
    String id = NewChestData.OLD_ID(dimension, pos);
    if (!manager.cache.containsKey(id)) {
      return;
    }
    NewChestData data = manager.get(() -> null, id);
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
