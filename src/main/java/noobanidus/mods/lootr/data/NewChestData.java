package noobanidus.mods.lootr.data;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.tiles.ILootTile;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewChestData extends WorldSavedData {
  private BlockPos pos;
  private int dimension;
  private Map<UUID, SpecialChestInventory> inventories = new HashMap<>();

  public static String ID(int dimension, BlockPos pos) {
    return "Lootr-chests-" + dimension + "-" + pos.toLong();
  }

  public NewChestData(int dimension, BlockPos pos) {
    super(ID(dimension, pos));
    this.pos = pos;
    this.dimension = dimension;
  }

  private void setInventory(ServerPlayerEntity player, SpecialChestInventory inventory) {
    inventory.filled();
    inventories.put(player.getUniqueID(), inventory);
    markDirty();
  }

  @Nullable
  private SpecialChestInventory getInventory(ServerPlayerEntity player) {
    SpecialChestInventory thisChest = inventories.get(player.getUniqueID());
    if (thisChest != null) {
      return thisChest;
    }

    World world;
    if (player.world.getDimension().getType().getId() != dimension) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server == null) {
        return null;
      }
      DimensionType type = DimensionType.getById(dimension);
      if (type == null) {
        return null;
      }
      world = server.getWorld(type);
    } else {
      world = player.world;
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
    return new SpecialChestInventory(items, tile.getDisplayName(), true, pos);
  }

  @Override
  public void read(CompoundNBT compound) {
    inventories.clear();
    pos = BlockPos.fromLong(compound.getLong("position"));
    dimension = compound.getInt("dimension");
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
    compound.putLong("position", pos.toLong());
    compound.putInt("dimension", dimension);
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
    private boolean wasNew;
    private BlockPos pos;

    public SpecialChestInventory(NonNullList<ItemStack> contents, ITextComponent name, boolean wasNew, BlockPos pos) {
      this.contents = contents;
      this.name = name;
      this.wasNew = wasNew;
      this.pos = pos;
    }

    public SpecialChestInventory(CompoundNBT items, String componentAsJSON, BlockPos pos) {
      this.name = ITextComponent.Serializer.fromJson(componentAsJSON);
      this.contents = NonNullList.withSize(27, ItemStack.EMPTY);
      ItemStackHelper.loadAllItems(items, this.contents);
      this.wasNew = false;
      this.pos = pos;
    }

    public boolean wasNew() {
      return wasNew;
    }

    @Nullable
    public LockableLootTileEntity getTile(World world) {
      if (world == null || world.isRemote()) {
        return null;
      }

      TileEntity te = world.getTileEntity(pos);
      if (te instanceof ILootTile) {
        return (LockableLootTileEntity) te;
      }

      return null;
    }

    public void filled() {
      this.wasNew = false;
      this.markDirty();
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

    public BlockPos getPos() {
      return pos;
    }
  }

  public void clear() {
    inventories.clear();
  }

  private static ServerWorld getServerWorld() {
    return ServerLifecycleHooks.getCurrentServer().getWorld(DimensionType.OVERWORLD);
  }

  private static NewChestData getInstance(IWorld world, BlockPos pos) {
    int dimension = world.getDimension().getType().getId();
    return getServerWorld().getSavedData().getOrCreate(() -> new NewChestData(dimension, pos), ID(dimension, pos));
  }

  @Nullable
  public static SpecialChestInventory getInventory(IWorld world, BlockPos pos, ServerPlayerEntity player) {
    Lootr.LOG.debug("Trying to get inventory in dim: "+ world.getDimension().getType().toString() + ", pos: " + pos.toString());
    NewChestData data = getInstance(world, pos);
    SpecialChestInventory inventory = data.getInventory(player);
    if (inventory == null) {
      return null;
    }
    if (!inventory.wasNew()) {
      return inventory;
    }

    TileEntity te = world.getTileEntity(pos);
    if (te instanceof ILootTile) {
      ILootTile tile = (ILootTile) te;
      tile.fillWithLoot(player, inventory);
      data.setInventory(player, inventory);
      tile.markForSync();
    }

    return inventory;
  }

  public static void wipeInventory(IWorld world, BlockPos pos) {
    ServerWorld serverWorld = getServerWorld();
    int dimension = world.getDimension().getType().getId();
    DimensionSavedDataManager manager = serverWorld.getSavedData();
    Lootr.LOG.debug("Wiped inventory in dim: "+ dimension + ", pos: " + pos.toString());
    String id = ID(dimension, pos);
    if (!manager.savedDatum.containsKey(id)) {
      return;
    }
    NewChestData data = manager.get(() -> null, id);
    if (data != null) {
      data.clear();
      data.markDirty();
    }
    // Saving is handled by the BooleanData.deleteLootChest
    //manager.save();
  }
}
