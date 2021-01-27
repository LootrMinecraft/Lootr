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
  private Map<UUID, SpecialChestInventory> inventories = new HashMap<>();

  public static String ID(RegistryKey<World> dimension, BlockPos pos) {
    return "Lootr-chests-" + dimension.getLocation().getPath() + "-" + pos.toLong();
  }

  public NewChestData(RegistryKey<World> dimension, BlockPos pos) {
    super(ID(dimension, pos));
    this.pos = pos;
    this.dimension = dimension;
    this.entityId = null;
  }

  public NewChestData(UUID entityId) {
    super(entityId.toString());
    this.pos = null;
    this.dimension = null;
    this.entityId = entityId;
  }

  @Nullable
  private SpecialChestInventory getInventory(ServerPlayerEntity player) {
    return inventories.get(player.getUniqueID());
  }

  private SpecialChestInventory createInventory(ServerPlayerEntity player, ILootTile.LootFiller filler) {
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

      if (world == null) {
        return null;
      }

      TileEntity te = world.getTileEntity(pos);

      LockableLootTileEntity tile;
      if (te instanceof ILootTile) {
        tile = (LockableLootTileEntity) te;
      } else {
        return null;
      }
      NonNullList<ItemStack> items = NonNullList.withSize(tile.getSizeInventory(), ItemStack.EMPTY);
      // Saving this is handled elsewhere
      result = new SpecialChestInventory(items, tile.getDisplayName(), pos);
      ChunkPos chunk = new ChunkPos(pos);
    }
    filler.fillWithLoot(player, result);
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
    if (compound.contains("position")) {
      pos = BlockPos.fromLong(compound.getLong("position"));
    }
    if (compound.contains("dimension")) {
      dimension = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(compound.getString("dimension")));
    }
    if (compound.hasUniqueId("entityId")) {
      entityId = compound.getUniqueId("entityId");
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
    }

    @Override
    public void closeInventory(PlayerEntity player) {
      markDirty();
      World world = player.world;
      LockableLootTileEntity tile = getTile(world);
      if (tile != null) {
        tile.closeInventory(player);
      }
      ((ServerWorld) world).getSavedData().save();
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

  private static NewChestData getInstance(ServerWorld world, BlockPos pos) {
    RegistryKey<World> dimension = world.getDimensionKey();
    return getServerWorld().getSavedData().getOrCreate(() -> new NewChestData(dimension, pos), ID(dimension, pos));
  }

  private static NewChestData getInstance(ServerWorld world, UUID id) {
    return getServerWorld().getSavedData().getOrCreate(() -> new NewChestData(id), id.toString());
  }

  @Nullable
  public static SpecialChestInventory getInventory(World world, BlockPos pos, ServerPlayerEntity player, ILootTile.LootFiller filler) {
    if (world.isRemote || !(world instanceof ServerWorld)) {
      return null;
    }

    NewChestData data = getInstance((ServerWorld) world, pos);
    SpecialChestInventory inventory = data.getInventory(player);
    if (inventory == null) {
      inventory = data.createInventory(player, filler);
    }

    return inventory;
  }

  @Nullable
  public static SpecialChestInventory getInventory(World world, LootrChestMinecartEntity cart, ServerPlayerEntity player, ILootTile.LootFiller filler) {
    if (world.isRemote || !(world instanceof ServerWorld)) {
      return null;
    }

    NewChestData data = getInstance((ServerWorld) world, cart.getUniqueID());
    SpecialChestInventory inventory = data.getInventory(player);
    if (inventory == null) {
      inventory = data.createInventory(player, filler);
    }

    return inventory;
  }

  public static void wipeInventory(ServerWorld world, BlockPos pos) {
    ServerWorld serverWorld = getServerWorld();
    RegistryKey<World> dimension = world.getDimensionKey();
    DimensionSavedDataManager manager = serverWorld.getSavedData();
    String id = ID(dimension, pos);
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
