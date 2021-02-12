package noobanidus.mods.lootr.data;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.tiles.ILootTile;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewChestData extends WorldSavedData {
  private BlockPos pos;
  private RegistryKey<World> dimension;
  private UUID entityId;
  private UUID tileId;
  private UUID customId;
  private Map<UUID, SpecialChestInventory> inventories = new HashMap<>();
  private NonNullList<ItemStack> reference;
  private boolean custom;

  public static String REF_ID (RegistryKey<World> dimension, UUID id) {
    return "Lootr-custom-" + dimension.getLocation().getPath() + "-" + id.toString();
  }

  public static String OLD_ID(RegistryKey<World> dimension, BlockPos pos) {
    return "Lootr-chests-" + dimension.getLocation().getPath() + "-" + pos.toLong();
  }

  public static String ID (RegistryKey<World> dimension, UUID id) {
    return "Lootr-chests-" + dimension.getLocation().getPath() + "-" + id.toString();
  }

  public static String ENTITY (UUID entityId) {
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

  private ILootTile.LootFiller customInventory () {
    return (player, inventory) -> {
      for (int i = 0; i < reference.size(); i++) {
        inventory.setInventorySlotContents(i, reference.get(i));
      }
    };
  }

  public Map<UUID, SpecialChestInventory> getInventories() {
    return inventories;
  }

  public void setInventories(Map<UUID, SpecialChestInventory> inventories) {
    this.inventories = inventories;
  }

  @Nullable
  private SpecialChestInventory getInventory(ServerPlayerEntity player, BlockPos pos) {
    SpecialChestInventory result = inventories.get(player.getUniqueID());
    if (result != null) {
      result.setBlockPos(pos);
    }
    return result;
  }

  private SpecialChestInventory createInventory(ServerPlayerEntity player, ILootTile.LootFiller filler, @Nullable LockableLootTileEntity tile) {
    ServerWorld world = (ServerWorld) player.world;
    SpecialChestInventory result;
    if (entityId != null) {
      Entity initial = world.getEntityByUuid(entityId);
      if (!(initial instanceof LootrChestMinecartEntity)) {
        return null;
      }
      LootrChestMinecartEntity cart = (LootrChestMinecartEntity) initial;
      NonNullList<ItemStack> items = NonNullList.withSize(cart.getSizeInventory(), ItemStack.EMPTY);
      // Saving this is handled elsewhere
      result = new SpecialChestInventory(items, cart.getDisplayName(), pos);
    } else {
      if (world.getDimensionKey() != dimension) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
          return null;
        }
        world = server.getWorld(dimension);
      }

      if (world == null || tile == null) {
        return null;
      }

      NonNullList<ItemStack> items = NonNullList.withSize(tile.getSizeInventory(), ItemStack.EMPTY);
      // Saving this is handled elsewhere
      result = new SpecialChestInventory(items, tile.getDisplayName(), pos);
    }
    if (this.custom) {
      customInventory().fillWithLoot(player, result);
    } else {
      filler.fillWithLoot(player, result);
    }
    inventories.put(player.getUniqueID(), result);
    markDirty();
    world.getSavedData().save();
    return result;
  }

  @Override
  public void read(CompoundNBT compound) {
    inventories.clear();
    pos = null;
    dimension = null;
    entityId = null;
    tileId = null;
    if (compound.contains("position")) {
      pos = BlockPos.fromLong(compound.getLong("position"));
    }
    if (compound.contains("dimension")) {
      dimension = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(compound.getString("dimension")));
    }
    if (compound.hasUniqueId("entityId")) {
      entityId = compound.getUniqueId("entityId");
    }
    if (compound.hasUniqueId("tileId")) {
      tileId = compound.getUniqueId("tileId");
    }
    if (compound.contains("custom")) {
      custom = compound.getBoolean("custom");
    }
    if (compound.hasUniqueId("customId")) {
      customId = compound.getUniqueId("customId");
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
      UUID uuid = thisTag.getUniqueId("uuid");
      inventories.put(uuid, new SpecialChestInventory(items, name, pos));
    }
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    if (pos != null) {
      compound.putLong("position", pos.toLong());
    }
    if (dimension != null) {
      compound.putString("dimension", dimension.getLocation().toString());
    }
    if (entityId != null) {
      compound.putUniqueId("entityId", entityId);
    }
    if (tileId != null) {
      compound.putUniqueId("tileId", tileId);
    }
    if (customId != null) {
      compound.putUniqueId("customId", customId);
    }
    compound.putBoolean("custom", custom);
    if (reference != null) {
      compound.putInt("referenceSize", reference.size());
      compound.put("reference", ItemStackHelper.saveAllItems(new CompoundNBT(), reference, true));
    }
    ListNBT compounds = new ListNBT();
    for (Map.Entry<UUID, SpecialChestInventory> entry : inventories.entrySet()) {
      CompoundNBT thisTag = new CompoundNBT();
      thisTag.putUniqueId("uuid", entry.getKey());
      thisTag.put("chest", entry.getValue().writeItems());
      thisTag.putString("name", entry.getValue().writeName());
      compounds.add(thisTag);
    }
    compound.put("inventories", compounds);

    return compound;
  }

  @SuppressWarnings("NullableProblems")
  public class SpecialChestInventory implements IInventory, INamedContainerProvider {
    private final NonNullList<ItemStack> contents;
    private final ITextComponent name;
    @Nullable
    private BlockPos pos;

    public SpecialChestInventory(NonNullList<ItemStack> contents, ITextComponent name, @Nullable BlockPos pos) {
      this.contents = contents;
      this.name = name;
      this.pos = pos;
    }

    public SpecialChestInventory(CompoundNBT items, String componentAsJSON, BlockPos pos) {
      this.name = ITextComponent.Serializer.getComponentFromJson(componentAsJSON);
      this.contents = NonNullList.withSize(27, ItemStack.EMPTY);
      ItemStackHelper.loadAllItems(items, this.contents);
      this.pos = pos;
    }

    public void setBlockPos (BlockPos pos) {
      this.pos = pos;
    }

    @Nullable
    public LockableLootTileEntity getTile(World world) {
      if (world == null || world.isRemote() || pos == null) {
        return null;
      }

      TileEntity te = world.getTileEntity(pos);
      if (te instanceof ILootTile) {
        return (LockableLootTileEntity) te;
      }

      return null;
    }

    @Nullable
    public LootrChestMinecartEntity getEntity (World world) {
      if (world == null || world.isRemote() || entityId == null) {
        return null;
      }

      if (!(world instanceof ServerWorld)) {
        return null;
      }

      ServerWorld serverWorld = (ServerWorld) world;
      Entity entity = serverWorld.getEntityByUuid(entityId);
      if (entity instanceof LootrChestMinecartEntity) {
        return (LootrChestMinecartEntity) entity;
      }

      return null;
    }

    @Override
    public int getSizeInventory() {
      return 27;
    }

    @Override
    public boolean isEmpty() {
      for (ItemStack itemstack : this.contents) {
        if (!itemstack.isEmpty()) {
          return false;
        }
      }

      return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
      return contents.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
      ItemStack itemstack = ItemStackHelper.getAndSplit(this.contents, index, count);
      if (!itemstack.isEmpty()) {
        this.markDirty();
        // TODO: Trigger save?
      }

      return itemstack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
      ItemStack result = ItemStackHelper.getAndRemove(contents, index);
      if (!result.isEmpty()) {
        this.markDirty();
      }

      return result;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
      this.contents.set(index, stack);
      if (stack.getCount() > this.getInventoryStackLimit()) {
        stack.setCount(this.getInventoryStackLimit());
      }

      this.markDirty();
    }

    @Override
    public void markDirty() {
      NewChestData.this.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
      return true;
    }

    @Override
    public void clear() {
      contents.clear();
      markDirty();
    }

    @Override
    public ITextComponent getDisplayName() {
      return name;
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
      return ChestContainer.createGeneric9X3(id, inventory, this);
    }

    @Override
    public void openInventory(PlayerEntity player) {
      World world = player.world;
      LockableLootTileEntity tile = getTile(world);
      if (tile != null) {
        tile.openInventory(player);
      }
      if (entityId != null) {
        LootrChestMinecartEntity entity = getEntity(world);
        if (entity != null) {
          entity.openInventory(player);
        }
      }
    }

    @Override
    public void closeInventory(PlayerEntity player) {
      markDirty();
      World world = player.world;
      if (pos != null) {
        LockableLootTileEntity tile = getTile(world);
        if (tile != null) {
          tile.closeInventory(player);
        }
      }
      if (entityId != null) {
        LootrChestMinecartEntity entity = getEntity(world);
        if (entity != null) {
          entity.closeInventory(player);
        }
      }
    }

    public CompoundNBT writeItems() {
      CompoundNBT result = new CompoundNBT();
      return ItemStackHelper.saveAllItems(result, this.contents);
    }

    public String writeName() {
      return ITextComponent.Serializer.toJson(this.name);
    }

    @Nullable
    public BlockPos getPos() {
      return pos;
    }
  }

  public void clear() {
    inventories.clear();
  }

  private static ServerWorld getServerWorld() {
    return ServerLifecycleHooks.getCurrentServer().getWorld(World.OVERWORLD);
  }

  private static NewChestData getInstancePos(ServerWorld world, BlockPos pos) {
    RegistryKey<World> dimension = world.getDimensionKey();
    return getServerWorld().getSavedData().get(() -> new NewChestData(dimension, pos), OLD_ID(dimension, pos));
  }

  private static NewChestData getInstanceUuid (ServerWorld world, UUID id) {
    RegistryKey<World> dimension = world.getDimensionKey();
    return getServerWorld().getSavedData().getOrCreate(() -> new NewChestData(dimension, id), ID(dimension, id));
  }

  private static NewChestData getInstance(ServerWorld world, UUID id) {
    return getServerWorld().getSavedData().getOrCreate(() -> new NewChestData(id), ENTITY(id));
  }

  @Nullable
  public static SpecialChestInventory getInventory(World world, UUID uuid, BlockPos pos, ServerPlayerEntity player, LockableLootTileEntity tile, ILootTile.LootFiller filler) {
    if (world.isRemote || !(world instanceof ServerWorld)) {
      return null;
    }

    NewChestData data = getInstanceUuid((ServerWorld) world, uuid);
    NewChestData oldData = getInstancePos((ServerWorld) world, pos);
    if (oldData != null) {
      Map<UUID, SpecialChestInventory> inventories = data.getInventories();
      inventories.putAll(oldData.getInventories());
      data.setInventories(inventories);
      oldData.clear();
      oldData.markDirty();
    }
    SpecialChestInventory inventory = data.getInventory(player, pos);
    if (inventory == null) {
      inventory = data.createInventory(player, filler, tile);
      inventory.setBlockPos(pos);
    }

    return inventory;
  }

  @Nullable
  public static SpecialChestInventory getInventory(World world, LootrChestMinecartEntity cart, ServerPlayerEntity player, ILootTile.LootFiller filler) {
    if (world.isRemote || !(world instanceof ServerWorld)) {
      return null;
    }

    NewChestData data = getInstance((ServerWorld) world, cart.getUniqueID());
    SpecialChestInventory inventory = data.getInventory(player, null);
    if (inventory == null) {
      inventory = data.createInventory(player, filler, null);
    }

    return inventory;
  }

  public static void wipeInventory(ServerWorld world, BlockPos pos) {
    ServerWorld serverWorld = getServerWorld();
    RegistryKey<World> dimension = world.getDimensionKey();
    DimensionSavedDataManager manager = serverWorld.getSavedData();
    String id = OLD_ID(dimension, pos);
    if (!manager.savedDatum.containsKey(id)) {
      return;
    }
    NewChestData data = manager.get(() -> null, id);
    if (data != null) {
      data.clear();
      data.markDirty();
    }
  }

  public static void deleteLootChest(ServerWorld world, BlockPos pos) {
    if (world.isRemote()) {
      return;
    }
    NewChestData.wipeInventory(world, pos);
    getServerWorld().getSavedData().save();
  }
}
