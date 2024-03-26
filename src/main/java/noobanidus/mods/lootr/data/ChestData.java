package noobanidus.mods.lootr.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.api.LootFiller;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class ChestData extends SavedData {
  private String key;
  private BlockPos pos;
  private ResourceKey<Level> dimension;
  private UUID uuid;
  private Map<UUID, SpecialChestInventory> inventories = new HashMap<>();
  private NonNullList<ItemStack> reference;
  private boolean custom;
  private boolean entity;
  private int size = -1;

  protected ChestData(String key) {
    this.key = key;
  }

  public BlockPos getPos() {
    return pos;
  }

  public String getKey() {
    return key;
  }

  public ResourceKey<Level> getDimension() {
    return dimension;
  }

  public int getSize() {
    return size;
  }

  private void setSize(int size) {
    if (this.size == size) {
      return;
    }
    if (size < this.size) {
      throw new IllegalArgumentException("Cannot resize inventory associated with '" + getKey() + "' in dimension '" + getDimension() + "' at location '" + getPos() + "' to a smaller size.");
    }
    this.size = size;
    for (SpecialChestInventory inventory : inventories.values()) {
      inventory.resizeInventory(size);
    }
  }

  @Nullable
  public UUID getEntityId() {
    if (entity) {
      return uuid;
    }

    return null;
  }

  @Nullable
  public UUID getTileId() {
    if (!entity) {
      return uuid;
    }

    return null;
  }

  public static String ID(UUID id) {
    String idString = id.toString();
    return "lootr/" + idString.charAt(0) + "/" + idString.substring(0, 2) + "/" + idString;
  }

  public static Supplier<ChestData> ref_id(ResourceKey<Level> dimension, BlockPos pos, UUID id, NonNullList<ItemStack> base) {
    if (id == null) {
      throw new IllegalArgumentException("Can't create ChestData for custom container in dimension '" + dimension + "' at '" + pos + "' with a null id.");
    }
    return () -> {
      ChestData data = new ChestData(ID(id));
      data.pos = pos;
      data.dimension = dimension;
      data.uuid = id;
      data.reference = base;
      data.custom = true;
      data.entity = false;
      if (data.reference == null) {
        throw new IllegalArgumentException("Inventory reference cannot be null.");
      }
      return data;
    };
  }

  public static Supplier<ChestData> id(ResourceKey<Level> dimension, BlockPos pos, UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Can't create ChestData for container in dimension '" + dimension + "' at '" + pos + "' with a null id.");
    }
    return () -> {
      ChestData data = new ChestData(ID(id));
      data.pos = pos;
      data.dimension = dimension;
      data.uuid = id;
      data.reference = null;
      data.custom = false;
      data.entity = false;
      return data;
    };
  }

  public static Supplier<ChestData> entity(ResourceKey<Level> dimension, BlockPos pos, UUID entityId) {
    if (entityId == null) {
      throw new IllegalArgumentException("Can't create ChestData for minecart in dimension '" + dimension + "' at '" + pos + "' with a null entityId.");
    }
    return () -> {
      ChestData data = new ChestData(ID(entityId));
      data.pos = pos;
      data.dimension = dimension;
      data.uuid = entityId;
      data.entity = true;
      data.reference = null;
      data.custom = false;
      return data;
    };
  }

  public LootFiller customInventory() {
    return (player, inventory, table, seed) -> {
      for (int i = 0; i < reference.size(); i++) {
        inventory.setItem(i, reference.get(i).copy());
      }
    };
  }

  public boolean clearInventory(UUID uuid) {
    return inventories.remove(uuid) != null;
  }

  @Nullable
  public SpecialChestInventory getInventory(ServerPlayer player) {
    return inventories.get(player.getUUID());
  }

  public SpecialChestInventory createInventory(ServerPlayer player, LootFiller filler, IntSupplier sizeSupplier, Supplier<Component> displaySupplier, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier) {
    ServerLevel level = (ServerLevel) player.level();
    SpecialChestInventory result;
    if (level.dimension() != dimension) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server == null) {
        return null;
      }
      level = server.getLevel(dimension);
    }

    if (level == null) {
      return null;
    }

    NonNullList<ItemStack> items = NonNullList.withSize(sizeSupplier.getAsInt(), ItemStack.EMPTY);
    result = new SpecialChestInventory(this, items, displaySupplier.get());
    filler.unpackLootTable(player, result, tableSupplier.get(), seedSupplier.getAsLong());
    inventories.put(player.getUUID(), result);
    setDirty();
    return result;
  }

  public SpecialChestInventory createInventory(ServerPlayer player, LootFiller filler, BaseContainerBlockEntity blockEntity, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier) {
    ServerLevel level = (ServerLevel) player.level();
    SpecialChestInventory result;
    if (level.dimension() != dimension) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server == null) {
        return null;
      }
      level = server.getLevel(dimension);
    }

    if (level == null) {
      return null;
    }

    NonNullList<ItemStack> items = NonNullList.withSize(blockEntity.getContainerSize(), ItemStack.EMPTY);
    result = new SpecialChestInventory(this, items, blockEntity.getDisplayName());
    filler.unpackLootTable(player, result, tableSupplier.get(), seedSupplier.getAsLong());
    inventories.put(player.getUUID(), result);
    setDirty();
    return result;
  }

  public SpecialChestInventory createInventory(ServerPlayer player, LootFiller filler, @Nullable RandomizableContainerBlockEntity tile) {
    ServerLevel world = (ServerLevel) player.level();
    SpecialChestInventory result;
    long seed = -1;
    ResourceLocation lootTable;
    if (entity) {
      Entity initial = world.getEntity(uuid);
      if (!(initial instanceof LootrChestMinecartEntity cart)) {
        return null;
      }
      NonNullList<ItemStack> items = NonNullList.withSize(cart.getContainerSize(), ItemStack.EMPTY);
      result = new SpecialChestInventory(this, items, cart.getDisplayName());
      lootTable = cart.lootTable;
    } else {
      if (tile == null) {
        return null;
      }

      lootTable = ((ILootBlockEntity) tile).getTable();

      NonNullList<ItemStack> items = NonNullList.withSize(tile.getContainerSize(), ItemStack.EMPTY);
      result = new SpecialChestInventory(this, items, tile.getDisplayName());
    }
    filler.unpackLootTable(player, result, lootTable, seed);
    inventories.put(player.getUUID(), result);
    setDirty();
    return result;
  }

  public static Function<CompoundTag, ChestData> loadWrapper(UUID id, ResourceKey<Level> dimension, BlockPos position) {
    return (tag) -> {
      ChestData result = ChestData.load(tag);
      result.key = ID(id);
      result.dimension = dimension;
      result.pos = position;
      return result;
    };
  }

  public static ChestData unwrap(ChestData data, UUID id, ResourceKey<Level> dimension, BlockPos position, int size) {
    data.key = ID(id);
    data.dimension = dimension;
    data.pos = position;
    data.setSize(size);
    return data;
  }

  public static ChestData load(CompoundTag compound) {
    ChestData data = new ChestData(compound.getString("key"));
    data.inventories.clear();
    data.pos = null;
    data.dimension = null;
    // Migrated position from `asLong` to `NtUtils::XXXBlockPos`
    if (compound.contains("position", Tag.TAG_LONG)) {
      data.pos = BlockPos.of(compound.getLong("position"));
    } else if (compound.contains("position", Tag.TAG_COMPOUND)) {
      data.pos = NbtUtils.readBlockPos(compound.getCompound("position"));
    }
    if (compound.contains("dimension")) {
      data.dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(compound.getString("dimension")));
    }
    boolean foundNewUUID = false;
    if (compound.hasUUID("uuid")) {
      data.uuid = compound.getUUID("uuid");
      foundNewUUID = true;
    }
    boolean foundEntity = false;
    if (compound.hasUUID("entityId")) {
      if (data.uuid != null /* || foundNewUUID */) { // foundNewUUID is redundant here, if uuid isn't null
        LootrAPI.LOG.error("Loaded an `entityId` from an already-migrated file: '" + data.key + "'");
      }
      data.uuid = compound.getUUID("entityId");
      data.entity = true;
      foundEntity = true;
    }
    if (compound.hasUUID("tileId")) {
      if (data.uuid != null) {
        if (foundEntity && !foundNewUUID) {
          LootrAPI.LOG.error("Loaded a `tileId` from an unmigrated file that also has `entityId`: '" + data.key + "'");
        } else if (foundEntity) {
          LootrAPI.LOG.error("Loaded a `tileId` from an already-migrated file that also had an `entityId`: '" + data.key + "'");
        } else if (foundNewUUID) {
          LootrAPI.LOG.error("Loaded a `tileId` from an already-migrated file: '" + data.key + "'");
        }
      }
      data.uuid = compound.getUUID("tileId");
      data.entity = false;
    }
    if (compound.contains("custom")) {
      data.custom = compound.getBoolean("custom");
    }
    if (compound.contains("entity")) {
      data.entity = compound.getBoolean("entity");
    }
    if (compound.hasUUID("customId")) {
      LootrAPI.LOG.error("Loaded a `customId` from an old file when this field was never used. File was '" + data.key + "'");
      data.uuid = compound.getUUID("customId");
      data.entity = false;
      data.custom = true;
    }
    if (compound.contains("reference") && compound.contains("referenceSize")) {
      int size = compound.getInt("referenceSize");
      data.size = size;
      data.reference = NonNullList.withSize(data.size, ItemStack.EMPTY);
      ContainerHelper.loadAllItems(compound.getCompound("reference"), data.reference);
    }
    if (compound.contains("size", Tag.TAG_INT)) {
      data.size = compound.getInt("size");
    } else if (!compound.contains("referenceSize")) {
      LootrAPI.LOG.error("Loaded a data file with no size: '" + data.key + "' located in dimension '" + data.dimension + "' at '" + data.pos + "'. Sizes will be guessed and updated in future.");
      // The unwrapper should really be filling this in, anyway.
    }
    ListTag compounds = compound.getList("inventories", Tag.TAG_COMPOUND);

    if (data.size == -1) {
      // We have no size saved and the unwrapper will only provide the correct size once the inventories have been deserialized. Thus, we need to make our best guess (the highest slot number of any contained inventory raised to the next highest power of 9) to safely deserialize now.
      int maxSlot = -1;
      for (int i = 0; i < compounds.size(); i++) {
        CompoundTag thisTag = compounds.getCompound(i);
        ListTag items = thisTag.getCompound("chest").getList("Items", 10);
        for (int j = 0; j < items.size(); j++) {
          CompoundTag itemTag = items.getCompound(j);
          int slot = itemTag.getByte("Slot") & 255;
          if (slot > maxSlot) {
            maxSlot = slot;
          }
        }
      }
      data.size = maxSlot % 9 == 0 ? maxSlot : maxSlot + (9 - (maxSlot % 9));
    }

    for (int i = 0; i < compounds.size(); i++) {
      CompoundTag thisTag = compounds.getCompound(i);
      CompoundTag items = thisTag.getCompound("chest");
      String name = thisTag.getString("name");
      UUID uuid = thisTag.getUUID("uuid");
      data.inventories.put(uuid, new SpecialChestInventory(data, items, name));
    }
    return data;
  }

  @Override
  public CompoundTag save(CompoundTag compound) {
    // These values should always be updated before there is a potential for them to be saved; that said, it's possible that they have been manually loaded, ala with the `/lootr clear` command.

    if (key != null) {
      compound.putString("key", this.key);
    } else {
      LootrAPI.LOG.error("Attempted to save a data file with no key! How could this happen?" + this);
    }
    if (pos != null) {
      compound.put("position", NbtUtils.writeBlockPos(pos));
    } else {
      LootrAPI.LOG.error("Attempted to save a data file with no `position`: '" + key + "'");
    }
    if (dimension != null) {
      compound.putString("dimension", dimension.location().toString());
    } else {
      LootrAPI.LOG.error("Attempted to save a data file with no `dimension`: '" + key + "'");
    }
    if (uuid != null) {
      compound.putUUID("uuid", uuid);
    } else {
      throw new IllegalStateException("Attempted to save a data file with no `uuid`: '" + key + "'. Located in dimension '" + dimension + "' at '" + pos + "'. This is an unrecoverable error.");
    }
    compound.putBoolean("custom", custom);
    compound.putBoolean("entity", entity);
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

  @Override
  public void save(File pFile) {
    if (isDirty()) {
      pFile.getParentFile().mkdirs();
    }
    super.save(pFile);
  }
}
