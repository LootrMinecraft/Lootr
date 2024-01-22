package noobanidus.mods.lootr.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
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
  private NonNullList<ItemStack> reference = null;
  private boolean custom = false;
  private boolean entity = false;
  private int size = -1;

  protected ChestData(String key) {
    this.key = key;
  }

  protected ChestData(UUID id) {
    this(ID(id));
  }

  protected ChestData(UUID id, boolean entity) {
    this(ID(id));
    this.entity = entity;
  }

  protected ChestData(UUID id, NonNullList<ItemStack> base) {
    this(id, false);
    this.custom = true;
    this.reference = base;
  }

  public BlockPos getPos() {
    return pos;
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

    if (this.size == -1) {
      this.size = sizeSupplier.getAsInt();
    }

    NonNullList<ItemStack> items = NonNullList.withSize(this.size, ItemStack.EMPTY);
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

    if (this.size == -1) {
      this.size = blockEntity.getContainerSize();
    }

    NonNullList<ItemStack> items = NonNullList.withSize(size, ItemStack.EMPTY);
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
      if (this.size == -1) {
        this.size = cart.getContainerSize();
      }
      NonNullList<ItemStack> items = NonNullList.withSize(this.size, ItemStack.EMPTY);
      result = new SpecialChestInventory(this, items, cart.getDisplayName());
      lootTable = cart.lootTable;
    } else {
      if (tile == null) {
        return null;
      }

      lootTable = ((ILootBlockEntity) tile).getTable();

      if (this.size == -1) {
        this.size = tile.getContainerSize();
      }

      NonNullList<ItemStack> items = NonNullList.withSize(this.size, ItemStack.EMPTY);
      result = new SpecialChestInventory(this, items, tile.getDisplayName());
    }
    filler.unpackLootTable(player, result, lootTable, seed);
    inventories.put(player.getUUID(), result);
    setDirty();
    return result;
  }

  protected static ChestData update(ChestData data, UUID id, ResourceKey<Level> dimension, BlockPos position) {
    // Check for UUID changes, unless null
    if (data.uuid == null) {
      data.uuid = id;
    } else if (data.uuid != id) {
      LootrAPI.LOG.error("ChestData UUID mismatch! Expected: '" + data.uuid + "' but got: '" + id + "' for chest: '" + data.key + "'!");
    }
    // Check for key changes, unless null
    if (data.key == null) {
      data.key = ID(id);
    } else if (!data.key.equals(ID(id))) {
      LootrAPI.LOG.error("ChestData key mismatch! Expected: '" + data.key + "' but got: '" + ID(id) + "' for chest: '" + data.key + "'!");
    }
    if (data.dimension != null && data.dimension != dimension) {
      LootrAPI.LOG.error("ChestData dimension changed! Expected: '" + data.dimension + "' but got: '" + dimension + "' for chest: '" + data.uuid.toString() + "'!");
    }
    // Always update dimension regardless
    data.dimension = dimension;

    // Only update position if non-entity; position is not assured to be correct for entities.
    if (!data.entity) {
      data.pos = position;
    }
    return data;
  }

  public static ChestData load(CompoundTag compound) {
    ChestData data = new ChestData(compound.getString("key"));
    data.inventories.clear();
    data.pos = null;
    data.dimension = null;
    // Shim for pre-migration is irrelevant in this version
    data.pos = NbtUtils.readBlockPos(compound.getCompound("position"));
    // Dimension is now always stored
    data.dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(compound.getString("dimension")));
    // Shim for entityId, tileId, customId, etc, no longer required
    data.uuid = compound.getUUID("uuid");
    // Custom is always stored
    data.custom = compound.getBoolean("custom");
    // Entity is always stored
    data.entity = compound.getBoolean("entity");

    // Reference is optional
    if (compound.contains("reference") && compound.contains("referenceSize")) {
      int size = compound.getInt("referenceSize");
      data.reference = NonNullList.withSize(size, ItemStack.EMPTY);
      ContainerHelper.loadAllItems(compound.getCompound("reference"), data.reference);
    }

    // Size is new and currently an optional read but a required write
    if (compound.contains("size")) {
      data.size = compound.getInt("size");
    }

    // Inventories are non-optional
    ListTag compounds = compound.getList("inventories", Tag.TAG_COMPOUND);
    for (int i = 0; i < compounds.size(); i++) {
      CompoundTag thisTag = compounds.getCompound(i);
      CompoundTag items = thisTag.getCompound("chest");
      String name = thisTag.getString("name");
      if (data.size == -1) {
        int size = -1;
        // No listed size, we'll have to guess
        ListTag itemList = items.getList("Items", 10);
        int maxSlot = 0;
        // Only non-empty slots are stored, so we can't work off the size of the list
        for (int j = 0; j < itemList.size(); j++) {
          CompoundTag item = itemList.getCompound(j);
          int slot = item.getByte("Slot") & 255;
          if (slot > maxSlot) {
            maxSlot = slot;
          }
        }
        // If there's no information we have to guess based on the maximum slot number.
        if (maxSlot == 0) {
          size = 27;
        } else {
          if (maxSlot < 9) {
            size = 9;
          } else if (maxSlot < 18) {
            size = 18;
          } else if (maxSlot < 27) {
            size = 27;
          } else if (maxSlot < 36) {
            size = 36;
          } else if (maxSlot < 45) {
            size = 45;
          } else {
            // This is the maximum size that it can go to.
            size = 54;
          }
        }
        data.size = size;
      }
      UUID uuid = thisTag.getUUID("uuid");
      // TODO: Possibly externalize size to `data`
      data.inventories.put(uuid, new SpecialChestInventory(data, data.size, items, name));
    }
    return data;
  }

  @Override
  public CompoundTag save(CompoundTag compound) {
    if (key != null) {
      compound.putString("key", this.key);
    } else {
      LootrAPI.LOG.error("Attempted to save a data file with no key! How could this happen?" + this);
    }
    if (pos != null) {
      compound.put("position", NbtUtils.writeBlockPos(pos));
    } else {
      // TODO: Block position is not stored if there was no block position
      LootrAPI.LOG.error("Attempted to save a data file with no `position`: '" + key + "'");
    }
    if (dimension != null) {
      compound.putString("dimension", dimension.location().toString());
    } else {
      LootrAPI.LOG.error("Attempted to save a data file with no `dimension`: '" + key + "'");
    }
    compound.putUUID("uuid", uuid);
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
      thisTag.putInt("size", entry.getValue().getContainerSize());
      thisTag.putString("name", entry.getValue().writeName());
      compounds.add(thisTag);
    }
    compound.put("inventories", compounds);

    return compound;
  }

  public void clear() {
    inventories.clear();
    setDirty();
  }

  @Override
  public void save(File pFile) {
    if (isDirty()) {
      pFile.getParentFile().mkdirs();
    }
    super.save(pFile);
  }
}
