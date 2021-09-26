package noobanidus.mods.lootr.data;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.FolderName;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.LootFiller;
import noobanidus.mods.lootr.api.LootrLootingEvent;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class NewChestData extends WorldSavedData {
  private BlockPos pos;
  private RegistryKey<World> dimension;
  private UUID entityId;
  private UUID tileId;
  private UUID customId;
  private Map<UUID, SpecialChestInventory> inventories = new HashMap<>();
  private NonNullList<ItemStack> reference;
  private boolean custom;

  public UUID getEntityId() {
    return entityId;
  }

  public static String REF_ID(RegistryKey<World> dimension, UUID id) {
    return "Lootr-custom-" + dimension.location().getPath() + "-" + id.toString();
  }

  public static String OLD_ID(RegistryKey<World> dimension, BlockPos pos) {
    return "Lootr-chests-" + dimension.location().getPath() + "-" + pos.asLong();
  }

  public static String ID(RegistryKey<World> dimension, UUID id) {
    return "Lootr-chests-" + dimension.location().getPath() + "-" + id.toString();
  }

  public static String ENTITY(UUID entityId) {
    return "Lootr-entity-" + entityId.toString();
  }

  public NewChestData(RegistryKey<World> dimension, UUID id, @Nullable UUID customId, @Nullable NonNullList<ItemStack> base) {
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

  public NewChestData(RegistryKey<World> dimension, UUID id) {
    super(ID(dimension, id));
    this.pos = null;
    this.dimension = dimension;
    this.entityId = null;
    this.tileId = id;
    this.reference = null;
    this.custom = false;
    this.customId = null;
  }

  public NewChestData(RegistryKey<World> dimension, BlockPos pos) {
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
  private SpecialChestInventory getInventory(ServerPlayerEntity player, BlockPos pos) {
    SpecialChestInventory result = inventories.get(player.getUUID());
    if (result != null) {
      result.setBlockPos(pos);
    }
    return result;
  }

  private SpecialChestInventory createInventory(ServerPlayerEntity player, LootFiller filler, @Nullable LockableLootTileEntity tile) {
    ServerWorld world = (ServerWorld) player.level;
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
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
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
    LootrLootingEvent.Pre preEvent = new LootrLootingEvent.Pre(player, world, dimension, result, tile, cart);
    if (!MinecraftForge.EVENT_BUS.post(preEvent)) {
      filler.fillWithLoot(player, result, preEvent.getNewTable(), preEvent.getNewSeed());
      LootrLootingEvent.Post postEvent = new LootrLootingEvent.Post(player, world, dimension, result, tile, cart);
      MinecraftForge.EVENT_BUS.post(postEvent);
    }
    inventories.put(player.getUUID(), result);
    setDirty();
    world.getDataStorage().save();
    return result;
  }

  @Override
  public void load(CompoundNBT compound) {
    inventories.clear();
    pos = null;
    dimension = null;
    entityId = null;
    tileId = null;
    if (compound.contains("position")) {
      pos = BlockPos.of(compound.getLong("position"));
    }
    if (compound.contains("dimension")) {
      dimension = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(compound.getString("dimension")));
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
      ItemStackHelper.loadAllItems(compound.getCompound("reference"), reference);
    }
    ListNBT compounds = compound.getList("inventories", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < compounds.size(); i++) {
      CompoundNBT thisTag = compounds.getCompound(i);
      CompoundNBT items = thisTag.getCompound("chest");
      String name = thisTag.getString("name");
      UUID uuid = thisTag.getUUID("uuid");
      inventories.put(uuid, new SpecialChestInventory(this, items, name, pos));
    }
  }

  @Override
  public CompoundNBT save(CompoundNBT compound) {
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
      compound.put("reference", ItemStackHelper.saveAllItems(new CompoundNBT(), reference, true));
    }
    ListNBT compounds = new ListNBT();
    for (Map.Entry<UUID, SpecialChestInventory> entry : inventories.entrySet()) {
      CompoundNBT thisTag = new CompoundNBT();
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

  private static ServerWorld getServerWorld() {
    return ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD);
  }

  private static NewChestData getInstancePos(ServerWorld world, BlockPos pos) {
    RegistryKey<World> dimension = world.dimension();
    return getServerWorld().getDataStorage().get(() -> new NewChestData(dimension, pos), OLD_ID(dimension, pos));
  }

  private static NewChestData getInstanceUuid(ServerWorld world, UUID id) {
    RegistryKey<World> dimension = world.dimension();
    return getServerWorld().getDataStorage().computeIfAbsent(() -> new NewChestData(dimension, id), ID(dimension, id));
  }

  private static NewChestData getInstance(ServerWorld world, UUID id) {
    return getServerWorld().getDataStorage().computeIfAbsent(() -> new NewChestData(id), ENTITY(id));
  }

  private static NewChestData getInstanceInventory(ServerWorld world, UUID id, @Nullable UUID customId, @Nullable NonNullList<ItemStack> base) {
    RegistryKey<World> dimension = world.dimension();
    return getServerWorld().getDataStorage().computeIfAbsent(() -> new NewChestData(dimension, id, customId, base), REF_ID(dimension, id));
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

  public static void deleteLootChest(ServerWorld world, BlockPos pos) {
    if (world.isClientSide()) {
      return;
    }
    NewChestData.wipeInventory(world, pos);
    getServerWorld().getDataStorage().save();
  }
}
