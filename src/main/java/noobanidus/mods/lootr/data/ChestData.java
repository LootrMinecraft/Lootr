package noobanidus.mods.lootr.data;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import noobanidus.mods.lootr.api.LootFiller;
import noobanidus.mods.lootr.api.tile.ILootTile;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChestData extends WorldSavedData {
  private BlockPos pos;
  private Integer dimension;
  private UUID entityId;
  private UUID tileId;
  private UUID customId;
  private Map<UUID, SpecialChestInventory> inventories = new HashMap<>();
  private NonNullList<ItemStack> reference;
  private boolean custom;

  public UUID getEntityId() {
    return entityId;
  }

  public static String ID(UUID id) {
    String idString = id.toString();
    return "lootr/" + idString.charAt(0) + "/" + idString.substring(0, 2) + "/" + idString;
  }

  public ChestData (String ID) {
    super(ID);
  }

  public ChestData(int dimension, UUID id, @Nullable UUID customId, @Nullable NonNullList<ItemStack> base) {
    super(ID(id));
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

  public ChestData(int dimension, UUID id) {
    super(ID(id));
    this.pos = null;
    this.dimension = dimension;
    this.entityId = null;
    this.tileId = id;
    this.reference = null;
    this.custom = false;
    this.customId = null;
  }

  public ChestData(UUID entityId) {
    super(ID(entityId));
    this.pos = null;
    this.dimension = null;
    this.tileId = null;
    this.entityId = entityId;
    this.reference = null;
    this.custom = false;
    this.customId = null;
  }

  public LootFiller customInventory() {
    return (player, inventory, table, seed) -> {
      for (int i = 0; i < reference.size(); i++) {
        inventory.setInventorySlotContents(i, reference.get(i).copy());
      }
    };
  }

  public Map<UUID, SpecialChestInventory> getInventories() {
    return inventories;
  }

  public void setInventories(Map<UUID, SpecialChestInventory> inventories) {
    this.inventories = inventories;
  }

  public boolean clearInventory(UUID uuid) {
    return inventories.remove(uuid) != null;
  }

  @Nullable
  public SpecialChestInventory getInventory(EntityPlayerMP player) {
    return inventories.get(player.getUniqueID());
  }

  public SpecialChestInventory createInventory(EntityPlayerMP player, LootFiller filler, @Nullable TileEntityLockableLoot tile) {
    WorldServer world = (WorldServer) player.world;
    SpecialChestInventory result;
    LootrChestMinecartEntity cart = null;
    long seed = -1;
    ResourceLocation lootTable = null;
    if (entityId != null) {
      Entity initial = world.getEntityFromUuid(entityId);
      if (!(initial instanceof LootrChestMinecartEntity)) {
        return null;
      }
      cart = (LootrChestMinecartEntity) initial;
      NonNullList<ItemStack> items = NonNullList.withSize(cart.getSizeInventory(), ItemStack.EMPTY);
      // Saving this is handled elsewhere
      result = new SpecialChestInventory(this, items, cart.getDisplayName(), pos);
      lootTable = cart.lootTable;
    } else {
/*      if (world.dimension() != dimension) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
          return null;
        }
        world = server.getLevel(dimension);
      }*/

      if (/*world == null || */tile == null) {
        return null;
      }

      lootTable = ((ILootTile) tile).getTable();

      NonNullList<ItemStack> items = NonNullList.withSize(tile.getSizeInventory(), ItemStack.EMPTY);
      result = new SpecialChestInventory(this, items, tile.getDisplayName(), pos);
    }
    filler.fillWithLoot(player, result, lootTable, seed);
    inventories.put(player.getUniqueID(), result);
    setDirty(true);

    world.getPerWorldStorage().saveAllData();
    result.setBlockPos(pos);
    return result;
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    inventories.clear();
    pos = null;
    dimension = null;
    entityId = null;
    tileId = null;
    if (compound.hasKey("position")) {
      pos = BlockPos.fromLong(compound.getLong("position"));
    }
    if (compound.hasKey("dimension")) {
      dimension = compound.getInteger("dimension");
    }
    if (compound.hasUniqueId("entityId")) {
      entityId = compound.getUniqueId("entityId");
    }
    if (compound.hasUniqueId("tileId")) {
      tileId = compound.getUniqueId("tileId");
    }
    if (compound.hasKey("custom")) {
      custom = compound.getBoolean("custom");
    }
    if (compound.hasUniqueId("customId")) {
      customId = compound.getUniqueId("customId");
    }
    if (compound.hasKey("reference") && compound.hasKey("referenceSize")) {
      int size = compound.getInteger("referenceSize");
      reference = NonNullList.withSize(size, ItemStack.EMPTY);
      ItemStackHelper.loadAllItems(compound.getCompoundTag("reference"), reference);
    }
    NBTTagList compounds = compound.getTagList("inventories", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < compounds.tagCount(); i++) {
      NBTTagCompound thisTag = compounds.getCompoundTagAt(i);
      NBTTagCompound items = thisTag.getCompoundTag("chest");
      String name = thisTag.getString("name");
      UUID uuid = thisTag.getUniqueId("uuid");
      inventories.put(uuid, new SpecialChestInventory(this, items, name, pos));
    }
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    if (pos != null) {
      compound.setLong("position", pos.toLong());
    }
    if (dimension != null) {
      compound.setInteger("dimension", dimension);
    }
    if (entityId != null) {
      compound.setUniqueId("entityId", entityId);
    }
    if (tileId != null) {
      compound.setUniqueId("tileId", tileId);
    }
    if (customId != null) {
      compound.setUniqueId("customId", customId);
    }
    compound.setBoolean("custom", custom);
    if (reference != null) {
      compound.setInteger("referenceSize", reference.size());
      compound.setTag("reference", ItemStackHelper.saveAllItems(new NBTTagCompound(), reference, true));
    }
    NBTTagList compounds = new NBTTagList();
    for (Map.Entry<UUID, SpecialChestInventory> entry : inventories.entrySet()) {
      NBTTagCompound thisTag = new NBTTagCompound();
      thisTag.setUniqueId("uuid", entry.getKey());
      thisTag.setTag("chest", entry.getValue().writeItems());
      thisTag.setString("name", entry.getValue().writeName());
      compounds.appendTag(thisTag);
    }
    compound.setTag("inventories", compounds);

    return compound;
  }

  public void clear() {
    inventories.clear();
  }

  public static ChestData unwrap(ChestData data, int dimension, BlockPos position) {
    data.pos = position;
    data.dimension = dimension;
    return data;
  }
}
