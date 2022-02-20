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
import noobanidus.mods.lootr.data.old.ChestData;
import noobanidus.mods.lootr.data.old.SpecialChestInventory;
import noobanidus.mods.lootr.data.old.TickingData;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

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
  public static final String NEW_DECAY = "Lootr-Decay-Data";
  public static final String REFRESH = "Lootr-RefreshData";
  public static final Map<UUID, PlayerData> DATA = new Object2ObjectLinkedOpenHashMap<>();

  public static boolean isAwarded(UUID player, UUID tileId) {
	PlayerData data = DATA.get(player);
	return data != null && data.getStats().hasAward(tileId);
  }

  public static void award(UUID player, UUID tileId) {
	PlayerData data = DATA.get(player);
	if(data != null) {
		data.getStats().award(tileId);
	}
  }

  public static boolean isScored(UUID player, UUID tileId) {
	PlayerData data = DATA.get(player);
	return data != null && data.getStats().isScored(tileId);
  }

  public static void score(UUID player, UUID tileId) {
	PlayerData data = DATA.get(player);
	if(data != null) {
		data.getStats().score(tileId);
	}
  }
  
  public static int getDecayValue(UUID id, long currentTime) {
	  return ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage().computeIfAbsent(() -> new DecayData(DECAY), DECAY).getDecayTime(id, currentTime);
  }
  
  public static boolean isDecayed(UUID id, long currentTime) {
	  return ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage().computeIfAbsent(() -> new DecayData(DECAY), DECAY).isDecayed(id, currentTime);
  }

  public static void setDecaying(UUID id, long expectedDecayTime) {
	 ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage().computeIfAbsent(() -> new DecayData(DECAY), DECAY).markDecayed(id, expectedDecayTime);
  }
  
  public static void removeDecayed(UUID id) {
	  ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD).getDataStorage().computeIfAbsent(() -> new DecayData(DECAY), DECAY).removeDecay(id);
  }

  public static int getRefreshValue(UUID player, UUID id, long currentTime) {
	PlayerData data = DATA.get(player);
	return data == null ? 0 : data.getTimedData().getRefreshTimeLeft(id, currentTime);
  }

  public static boolean isRefreshed(UUID player, UUID id, long currentTime) {
	PlayerData data = DATA.get(player);
	return data != null && data.getTimedData().isRefreshed(id, currentTime);
  }

  public static void setRefreshing(UUID player, UUID id, long expectedRefreshTime) {
	PlayerData data = DATA.get(player);
	if(data != null) {
		data.getTimedData().markRefresh(id, expectedRefreshTime);
	}
  }

  public static void removeRefreshed(UUID player, UUID id) {
	PlayerData data = DATA.get(player);
	if(data != null) {
		data.getTimedData().removeRefresh(id);
	}
  }

  public static ServerWorld getServerWorld() {
    return ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD);
  }

  public static ContainerData getInstanceUuid(UUID player, ServerWorld world, UUID id) {
	PlayerData data = DATA.get(player);
	if(data == null) {
		return null;
	}
	return data.getOrCreate(id, () -> new ContainerData(world.dimension(), id));
  }

  public static ContainerData getInstance(UUID player, UUID id) {
		PlayerData data = DATA.get(player);
		if(data == null) {
			return null;
		}
		return data.getOrCreate(id, () -> new ContainerData(id));
  }

  public static ContainerData getInstanceInventory(UUID player, ServerWorld world, UUID id, @Nullable UUID customId, @Nullable NonNullList<ItemStack> base) {
	PlayerData data = DATA.get(player);
	if(data == null) {
		return null;
	}
	return data.getOrCreate(id, () -> new ContainerData(world.dimension(), id, customId, base));
  }

  @Nullable
  public static SpecialChestInventory getInventory(World world, UUID uuid, BlockPos pos, ServerPlayerEntity player, LockableLootTileEntity tile, LootFiller filler) {
    if (world.isClientSide || !(world instanceof ServerWorld)) {
      return null;
    }

    ContainerData data = getInstanceUuid(player.getUUID(), (ServerWorld) world, uuid);
    SpecialChestInventory inventory = data.getInventory();
    if (inventory == null) {
      inventory = data.createInventory(player, filler, tile);
      inventory.setBlockPos(pos);
    }
    return inventory;
  }

  public static void refreshInventory(World world, UUID uuid, ServerPlayerEntity player) {
    if (world.isClientSide || !(world instanceof ServerWorld)) {
      return;
    }
    getInstanceUuid(player.getUUID(), (ServerWorld) world, uuid).clear();
  }

  @Nullable
  public static SpecialChestInventory getInventory(World world, UUID uuid, NonNullList<ItemStack> base, ServerPlayerEntity player, BlockPos pos, LockableLootTileEntity tile) {
    if (world.isClientSide || !(world instanceof ServerWorld)) {
      return null;
    }
    ContainerData data = getInstanceInventory(player.getUUID(), (ServerWorld) world, uuid, null, base);
    SpecialChestInventory inventory = data.getInventory();
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
    ContainerData data = getInstanceInventory(player.getUUID(), (ServerWorld) world, uuid, null, base);
    data.clear();
  }

  public static boolean clearInventories(ServerPlayerEntity player) {
	PlayerData data = DATA.get(player.getUUID());
	if(data != null) {
		data.clearPlayerData();
		return true;
	}
	return false;
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

    ContainerData data = getInstance(player.getUUID(), cart.getUUID());
    SpecialChestInventory inventory = data.getInventory();
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
    getInstance(player.getUUID(), cart.getUUID()).clear();;
  }
}
