package noobanidus.mods.lootr.data.old;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

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
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.api.LootFiller;
import noobanidus.mods.lootr.api.tile.ILootTile;
import noobanidus.mods.lootr.data.ContainerData;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

public class ChestData extends WorldSavedData {
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

  public ChestData(RegistryKey<World> dimension, UUID id, @Nullable UUID customId, @Nullable NonNullList<ItemStack> base) {
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

  public ChestData(RegistryKey<World> dimension, UUID id) {
    super(ID(dimension, id));
    this.pos = null;
    this.dimension = dimension;
    this.entityId = null;
    this.tileId = id;
    this.reference = null;
    this.custom = false;
    this.customId = null;
  }

  public ChestData(RegistryKey<World> dimension, BlockPos pos) {
    super(OLD_ID(dimension, pos));
    this.pos = pos;
    this.dimension = dimension;
    this.entityId = null;
    this.tileId = null;
    this.reference = null;
    this.custom = false;
    this.customId = null;
  }
  
  public ChestData(String id)
  { 
	  super(id);
  }
  
  public ChestData(UUID entityId) {
    super(ENTITY(entityId));
    this.pos = null;
    this.dimension = null;
    this.tileId = null;
    this.entityId = entityId;
    this.reference = null;
    this.custom = false;
    this.customId = null;
  }
  
  UUID getInternalId()
  {
	  if(entityId != null) return entityId;
	  if(tileId != null) return tileId;
	  if(customId != null) return customId;
	  return null;
  } 
  
  public void migrate(BiFunction<UUID, UUID, ContainerData> getter)
  {
	  UUID id = getInternalId();
	  if(id == null) return;
	  for(Map.Entry<UUID, SpecialChestInventory> entry : inventories.entrySet())
	  {
		  CompoundNBT data = saveMigrated(entry.getKey());
		  if(data.isEmpty()) continue;
		  getter.apply(entry.getKey(), id).load(data);
	  }
  }
  
  public CompoundNBT saveMigrated(UUID id) {
	  CompoundNBT compound = new CompoundNBT();
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
	  if(custom) {
		  compound.putBoolean("custom", custom);
	  }
	  if (reference != null) {
		  compound.putInt("referenceSize", reference.size());
	      compound.put("reference", ItemStackHelper.saveAllItems(new CompoundNBT(), reference, true));
	  }
	  SpecialChestInventory inventory = inventories.get(id);
	  if(inventory != null)
	  {
		  CompoundNBT data = new CompoundNBT();
		  data.put("chest", inventory.writeItems());
		  data.putString("name", inventory.writeName());
		  compound.put("inventory", data);
	  }
	  return compound;
  }

  public LootFiller customInventory() {
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

  public boolean clearInventory(UUID uuid) {
    return inventories.remove(uuid) != null;
  }

  @Nullable
  public SpecialChestInventory getInventory(ServerPlayerEntity player, BlockPos pos) {
    SpecialChestInventory result = inventories.get(player.getUUID());
    if (result != null) {
      result.setBlockPos(pos);
    }
    return result;
  }

  public SpecialChestInventory createInventory(ServerPlayerEntity player, LootFiller filler, @Nullable LockableLootTileEntity tile) {
    ServerWorld world = (ServerWorld) player.level;
    SpecialChestInventory result;
    LootrChestMinecartEntity cart = null;
    long seed = -1;
    ResourceLocation lootTable = null;
    if (entityId != null) {
      Entity initial = world.getEntity(entityId);
      if (!(initial instanceof LootrChestMinecartEntity)) {
        return null;
      }
      cart = (LootrChestMinecartEntity) initial;
      NonNullList<ItemStack> items = NonNullList.withSize(cart.getContainerSize(), ItemStack.EMPTY);
      // Saving this is handled elsewhere
      result = new SpecialChestInventory(this, items, cart.getDisplayName(), pos);
      lootTable = cart.lootTable;
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

      lootTable = ((ILootTile) tile).getTable();

      NonNullList<ItemStack> items = NonNullList.withSize(tile.getContainerSize(), ItemStack.EMPTY);
      // Saving this is handled elsewhere
      result = new SpecialChestInventory(this, items, tile.getDisplayName(), pos);
    }
    filler.fillWithLoot(player, result, lootTable, seed);
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

}
