package noobanidus.mods.lootr.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.TickEvent;
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
    DimensionDataStorage manager = ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD).getDataStorage();
    AdvancementData data = manager.computeIfAbsent(AdvancementData::load, AdvancementData::new, ID);
    return data.contains(player, tileId);
  }

  public static void award(UUID player, UUID tileId) {
    DimensionDataStorage manager = ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD).getDataStorage();
    AdvancementData data = manager.computeIfAbsent(AdvancementData::load, AdvancementData::new, ID);
    data.add(player, tileId);
    data.setDirty();
    manager.save();
  }

  public static boolean isScored(UUID player, UUID tileId) {
    DimensionDataStorage manager = ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD).getDataStorage();
    AdvancementData data = manager.computeIfAbsent(AdvancementData::load, AdvancementData::new, SCORED);
    return data.contains(player, tileId);
  }

  public static void score(UUID player, UUID tileId) {
    DimensionDataStorage manager = ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD).getDataStorage();
    AdvancementData data = manager.computeIfAbsent(AdvancementData::load, AdvancementData::new, SCORED);
    data.add(player, tileId);
    data.setDirty();
    manager.save();
  }

  public static int decayed (UUID id) {
    DimensionDataStorage manager = ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD).getDataStorage();
    DecayingData data = manager.computeIfAbsent(DecayingData::load, DecayingData::new, DECAY);
    return data.decay(id);
  }

  public static boolean hasDecayed (UUID id) {
    DimensionDataStorage manager = ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD).getDataStorage();
    DecayingData data = manager.computeIfAbsent(DecayingData::load, DecayingData::new, DECAY);
    return data.hasDecayed(id);
  }

  public static void setDecaying (UUID id, int decay) {
    DimensionDataStorage manager = ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD).getDataStorage();
    DecayingData data = manager.computeIfAbsent(DecayingData::load, DecayingData::new, DECAY);
    data.decay(id, decay);
    data.setDirty();
    manager.save();
  }

  public static void decay (UUID id) {
    DimensionDataStorage manager = ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD).getDataStorage();
    DecayingData data = manager.computeIfAbsent(DecayingData::load, DecayingData::new, DECAY);
    if (data.decayed(id) != -1) {
      data.setDirty();
      manager.save();
    }
  }

  public static void doDecay (TickEvent.ServerTickEvent event) {
    DimensionDataStorage manager = ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD).getDataStorage();
    DecayingData data = manager.computeIfAbsent(DecayingData::load, DecayingData::new, DECAY);
    if (data.doDecay(event)) {
      data.setDirty();
      manager.save();
    }
  }

  public static ServerLevel getServerLevel() {
    return ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD);
  }

  public static NewChestData getInstanceUuid(ServerLevel world, UUID id) {
    ResourceKey<Level> dimension = world.dimension();
    return getServerLevel().getDataStorage().computeIfAbsent(NewChestData::load, NewChestData.id(dimension, id), ID(dimension, id));
  }

  public static NewChestData getInstance(ServerLevel world, UUID id) {
    return getServerLevel().getDataStorage().computeIfAbsent(NewChestData::load, NewChestData.entity(id), ENTITY(id));
  }

  public static NewChestData getInstanceInventory(ServerLevel world, UUID id, @Nullable UUID customId, @Nullable NonNullList<ItemStack> base) {
    ResourceKey<Level> dimension = world.dimension();
    return getServerLevel().getDataStorage().computeIfAbsent(NewChestData::load, NewChestData.ref_id(dimension, id, customId, base), REF_ID(dimension, id));
  }

  @Nullable
  public static SpecialChestInventory getInventory(Level world, UUID uuid, BlockPos pos, ServerPlayer player, RandomizableContainerBlockEntity tile, LootFiller filler) {
    if (world.isClientSide || !(world instanceof ServerLevel)) {
      return null;
    }

    NewChestData data = getInstanceUuid((ServerLevel) world, uuid);
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
    NewChestData data = getInstanceInventory((ServerLevel) world, uuid, null, base);
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
    ServerLevel world = getServerLevel();
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
      NewChestData chestData = data.get(NewChestData::load, id);
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
  public static SpecialChestInventory getInventory(Level world, LootrChestMinecartEntity cart, ServerPlayer player, LootFiller filler) {
    if (world.isClientSide || !(world instanceof ServerLevel)) {
      return null;
    }

    NewChestData data = getInstance((ServerLevel) world, cart.getUUID());
    SpecialChestInventory inventory = data.getInventory(player, null);
    if (inventory == null) {
      inventory = data.createInventory(player, filler, null);
    }

    return inventory;
  }

  public static String REF_ID(ResourceKey<Level> dimension, UUID id) {
    return "Lootr-custom-" + dimension.location().getPath() + "-" + id.toString();
  }

  public static String OLD_ID(ResourceKey<Level> dimension, BlockPos pos) {
    return "Lootr-chests-" + dimension.location().getPath() + "-" + pos.asLong();
  }

  public static String ID(ResourceKey<Level> dimension, UUID id) {
    return "Lootr-chests-" + dimension.location().getPath() + "-" + id.toString();
  }

  public static String ENTITY(UUID entityId) {
    return "Lootr-entity-" + entityId.toString();
  }
}
