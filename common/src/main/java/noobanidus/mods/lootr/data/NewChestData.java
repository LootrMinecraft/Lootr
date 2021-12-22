package noobanidus.mods.lootr.data;

import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.saveddata.SavedData;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.LootFiller;
import noobanidus.mods.lootr.api.ServerAccess;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class NewChestData extends SavedData {
  private BlockPos pos;
  private ResourceKey<Level> dimension;
  private UUID entityId;
  private UUID tileId;
  private UUID customId;
  private Map<UUID, SpecialChestInventory> inventories = new HashMap<>();
  private NonNullList<ItemStack> reference;
  private boolean custom;

  public UUID getEntityId() {
    return entityId;
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

  public NewChestData(ResourceKey<Level> dimension, UUID id, @Nullable UUID customId, @Nullable NonNullList<ItemStack> base) {
    super(REF_ID(dimension, id));
    this.pos = null;
    this.dimension = dimension;
    this.entityId = null;
    this.tileId = id;
    this.reference = base;
    this.custom = true;
    this.customId = customId;
    if (customId == null && base == null) {
      throw new IllegalArgumentException("Both customId and inventory reference cannot be null.");
    }
  }

  public NewChestData(ResourceKey<Level> dimension, UUID id) {
    super(ID(dimension, id));
    this.pos = null;
    this.dimension = dimension;
    this.entityId = null;
    this.tileId = id;
    this.reference = null;
    this.custom = false;
    this.customId = null;
  }

  public NewChestData(ResourceKey<Level> dimension, BlockPos pos) {
    super(OLD_ID(dimension, pos));
    this.pos = pos;
    this.dimension = dimension;
    this.entityId = null;
    this.tileId = null;
    this.reference = null;
    this.custom = false;
    this.customId = null;
  }

  public NewChestData(UUID entityId) {
    super(ENTITY(entityId));
    this.pos = null;
    this.dimension = null;
    this.tileId = null;
    this.entityId = entityId;
    this.reference = null;
    this.custom = false;
    this.customId = null;
  }

  private LootFiller customInventory() {
    return (player, inventory, table, seed) -> {
      for (int i = 0; i < reference.size(); i++) {
        inventory.setItem(i, reference.get(i).copy());
      }
    };
  }

  public Map<UUID, SpecialChestInventory> getInventories() {
    return inventories;
  }

  public void setInventories(Map<UUID, SpecialChestInventory> inventories) {
    this.inventories = inventories;
  }

  private boolean clearInventory(UUID uuid) {
    return inventories.remove(uuid) != null;
  }

  @Nullable
  private SpecialChestInventory getInventory(ServerPlayer player, BlockPos pos) {
    SpecialChestInventory result = inventories.get(player.getUUID());
    if (result != null) {
      result.setBlockPos(pos);
    }
    return result;
  }

  private SpecialChestInventory createInventory(ServerPlayer player, LootFiller filler, @Nullable RandomizableContainerBlockEntity tile) {
    ServerLevel world = (ServerLevel) player.level;
    SpecialChestInventory result;
    LootrChestMinecartEntity cart = null;
    long seed = -1;
    if (entityId != null) {
      Entity initial = world.getEntity(entityId);
      if (!(initial instanceof LootrChestMinecartEntity)) {
        return null;
      }
      cart = (LootrChestMinecartEntity) initial;
      NonNullList<ItemStack> items = NonNullList.withSize(cart.getContainerSize(), ItemStack.EMPTY);
      // Saving this is handled elsewhere
      result = new SpecialChestInventory(this, items, cart.getDisplayName(), pos);
    } else {
      if (world.dimension() != dimension) {
        MinecraftServer server = ServerAccess.getServer();
        if (server == null) {
          return null;
        }
        world = server.getLevel(dimension);
      }

      if (world == null || tile == null) {
        return null;
      }

      NonNullList<ItemStack> items = NonNullList.withSize(tile.getContainerSize(), ItemStack.EMPTY);
      // Saving this is handled elsewhere
      result = new SpecialChestInventory(this, items, tile.getDisplayName(), pos);
    }
    filler.fillWithLoot(player, result, preEvent.getNewTable(), preEvent.getNewSeed());
    inventories.put(player.getUUID(), result);
    setDirty();
    world.getDataStorage().save();
    return result;
  }

  @Override
  public void load(CompoundTag compound) {
    inventories.clear();
    pos = null;
    dimension = null;
    entityId = null;
    tileId = null;
    if (compound.contains("position")) {
      pos = BlockPos.of(compound.getLong("position"));
    }
    if (compound.contains("dimension")) {
      dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(compound.getString("dimension")));
    }
    if (compound.hasUUID("entityId")) {
      entityId = compound.getUUID("entityId");
    }
    if (compound.hasUUID("tileId")) {
      tileId = compound.getUUID("tileId");
    }
    if (compound.contains("custom")) {
      custom = compound.getBoolean("custom");
    }
    if (compound.hasUUID("customId")) {
      customId = compound.getUUID("customId");
    }
    if (compound.contains("reference") && compound.contains("referenceSize")) {
      int size = compound.getInt("referenceSize");
      reference = NonNullList.withSize(size, ItemStack.EMPTY);
      ContainerHelper.loadAllItems(compound.getCompound("reference"), reference);
    }
    ListTag compounds = compound.getList("inventories", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < compounds.size(); i++) {
      CompoundTag thisTag = compounds.getCompound(i);
      CompoundTag items = thisTag.getCompound("chest");
      String name = thisTag.getString("name");
      UUID uuid = thisTag.getUUID("uuid");
      inventories.put(uuid, new SpecialChestInventory(this, items, name, pos));
    }
  }

  @Override
  public CompoundTag save(CompoundTag compound) {
    if (pos != null) {
      compound.putLong("position", pos.asLong());
    }
    if (dimension != null) {
      compound.putString("dimension", dimension.location().toString());
    }
    if (entityId != null) {
      compound.putUUID("entityId", entityId);
    }
    if (tileId != null) {
      compound.putUUID("tileId", tileId);
    }
    if (customId != null) {
      compound.putUUID("customId", customId);
    }
    compound.putBoolean("custom", custom);
    if (reference != null) {
      compound.putInt("referenceSize", reference.size());
      compound.put("reference", ContainerHelper.saveAllItems(new CompoundTag(), reference, true));
    }
    ListTag compounds = new ListTag();
    for (Map.Entry<UUID, SpecialChestInventory> entry : inventories.entrySet()) {
      CompoundTag thisTag = new CompoundTag();
      thisTag.putUUID("uuid", entry.getKey());
      thisTag.put("chest", entry.getValue().writeItems());
      thisTag.putString("name", entry.getValue().writeName());
      compounds.add(thisTag);
    }
    compound.put("inventories", compounds);

    return compound;
  }

  public void clear() {
    inventories.clear();
  }

  private static ServerLevel getServerWorld() {
    return ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD);
  }

  private static NewChestData getInstancePos(ServerLevel world, BlockPos pos) {
    ResourceKey<Level> dimension = world.dimension();
    return getServerWorld().getDataStorage().get(() -> new NewChestData(dimension, pos), OLD_ID(dimension, pos));
  }

  private static NewChestData getInstanceUuid(ServerLevel world, UUID id) {
    ResourceKey<Level> dimension = world.dimension();
    return getServerWorld().getDataStorage().computeIfAbsent(() -> new NewChestData(dimension, id), ID(dimension, id));
  }

  private static NewChestData getInstance(ServerLevel world, UUID id) {
    return getServerWorld().getDataStorage().computeIfAbsent(() -> new NewChestData(id), ENTITY(id));
  }

  private static NewChestData getInstanceInventory(ServerLevel world, UUID id, @Nullable UUID customId, @Nullable NonNullList<ItemStack> base) {
    ResourceKey<Level> dimension = world.dimension();
    return getServerWorld().getDataStorage().computeIfAbsent(() -> new NewChestData(dimension, id, customId, base), REF_ID(dimension, id));
  }

  @Nullable
  public static SpecialChestInventory getInventory(Level world, UUID uuid, BlockPos pos, ServerPlayer player, RandomizableContainerBlockEntity tile, LootFiller filler) {
    if (world.isClientSide || !(world instanceof ServerLevel)) {
      return null;
    }

    NewChestData data = getInstanceUuid((ServerLevel) world, uuid);
    NewChestData oldData = getInstancePos((ServerLevel) world, pos);
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
    ServerLevel world = getServerWorld();
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

  public static void wipeInventory(ServerLevel world, BlockPos pos) {
    ServerLevel serverWorld = getServerWorld();
    ResourceKey<Level> dimension = world.dimension();
    DimensionDataStorage manager = serverWorld.getDataStorage();
    String id = OLD_ID(dimension, pos);
    if (!manager.cache.containsKey(id)) {
      return;
    }
    NewChestData data = manager.get(() -> null, id);
    if (data != null) {
      data.clear();
      data.setDirty();
    }
  }

  public static void deleteLootChest(ServerLevel world, BlockPos pos) {
    if (world.isClientSide()) {
      return;
    }
    NewChestData.wipeInventory(world, pos);
    getServerWorld().getDataStorage().save();
  }
}
