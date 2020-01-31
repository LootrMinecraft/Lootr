/*package noobanidus.mods.lootr.data;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.tiles.SpecialLootChestTile;

import javax.annotation.Nullable;
import java.util.UUID;

public class OldChestData extends WorldSavedData {
  private UUID playerId;
  private Int2ObjectOpenHashMap<Long2ObjectOpenHashMap<OldSpecialChestInventory>> inventories = new Int2ObjectOpenHashMap<>();

  public static String ID(ServerPlayerEntity player) {
    return "Lootr-chests-" + player.getCachedUniqueIdString();
  }

  public OldChestData(ServerPlayerEntity player) {
    super(ID(player));
    this.playerId = player.getUniqueID();
  }

  private Long2ObjectOpenHashMap<OldSpecialChestInventory> getDimension(int dimension) {
    return inventories.computeIfAbsent(dimension, o -> {
      Long2ObjectOpenHashMap<OldSpecialChestInventory> map = new Long2ObjectOpenHashMap<>();
      map.defaultReturnValue(null);
      return map;
    });
  }

  private void setInventory(OldSpecialChestInventory inventory, IWorld world, BlockPos pos) {
    inventory.filled();
    Long2ObjectOpenHashMap<OldSpecialChestInventory> dimMap = getDimension(world.getDimension().getType().getId());
    long position = pos.toLong();
    dimMap.put(position, inventory);
    markDirty();
  }

  @Nullable
  private OldSpecialChestInventory getInventory(IWorld world, BlockPos position) {
    long pos = position.toLong();
    TileEntity te = world.getTileEntity(position);
    if (te == null) {
      return null;
    }

    SpecialLootChestTile tile = (SpecialLootChestTile) te;

    Long2ObjectOpenHashMap<OldSpecialChestInventory> dimMap = getDimension(world.getDimension().getType().getId());
    OldSpecialChestInventory thisChest = dimMap.get(pos);
    if (thisChest != null) {
      return thisChest;
    }

    NonNullList<ItemStack> items = NonNullList.withSize(tile.getSizeInventory(), ItemStack.EMPTY);
    return new OldSpecialChestInventory(items, tile.getDisplayName(), true, position, tile);
  }

  @Override
  public void read(CompoundNBT compound) {
    inventories.clear();
    playerId = compound.getUniqueId("playerId");
    for (String key : compound.keySet()) {
      if (key.equals("playerIdMost") || key.equals("playerIdLeast")) {
        continue;
      }

      int dim = Integer.parseInt(key);
      ListNBT dimensionList = compound.getList(key, Constants.NBT.TAG_COMPOUND);
      Long2ObjectOpenHashMap<OldSpecialChestInventory> dimMap = getDimension(dim);
      for (int i = 0; i < dimensionList.size(); i++) {
        CompoundNBT thisTag = dimensionList.getCompound(i);
        long pos = thisTag.getLong("position");
        CompoundNBT items = thisTag.getCompound("chest");
        String name = thisTag.getString("name");
        BlockPos position = BlockPos.fromLong(pos);
        dimMap.put(pos, new OldSpecialChestInventory(items, name, position));
      }
    }
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    compound.putUniqueId("playerId", playerId);
    for (Int2ObjectMap.Entry<Long2ObjectOpenHashMap<OldSpecialChestInventory>> entry : inventories.int2ObjectEntrySet()) {
      int dimension = entry.getIntKey();
      ListNBT compoundList = new ListNBT();
      for (Long2ObjectMap.Entry<OldSpecialChestInventory> thisEntry : entry.getValue().long2ObjectEntrySet()) {
        CompoundNBT thisTag = new CompoundNBT();
        thisTag.putLong("position", thisEntry.getLongKey());
        thisTag.put("chest", thisEntry.getValue().writeItems());
        thisTag.putString("name", thisEntry.getValue().writeName());
        thisTag.putInt("dimension", dimension);
        compoundList.add(thisTag);
      }
      compound.put(String.valueOf(dimension), compoundList);
    }

    return compound;
  }

  @SuppressWarnings("NullableProblems")
  public class OldSpecialChestInventory implements IInventory, INamedContainerProvider {
    private final NonNullList<ItemStack> contents;
    private final ITextComponent name;
    private boolean wasNew;
    private BlockPos pos;

    public OldSpecialChestInventory(NonNullList<ItemStack> contents, ITextComponent name, boolean wasNew, BlockPos pos, SpecialLootChestTile tile) {
      this.contents = contents;
      this.name = name;
      this.wasNew = wasNew;
      this.pos = pos;
    }

    public OldSpecialChestInventory(CompoundNBT items, String componentAsJSON, BlockPos pos) {
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
    public SpecialLootChestTile getTile (World world) {
      if (world == null || world.isRemote()) {
        return null;
      }

      TileEntity te = world.getTileEntity(pos);
      if (te instanceof SpecialLootChestTile) {
        return (SpecialLootChestTile) te;
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
      OldChestData.this.markDirty();
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
      if (!world.isRemote()) {
        SpecialLootChestTile tile = getTile(world);
        if (tile != null) {
          tile.openInventory(player);
        }
      }
    }

    @Override
    public void closeInventory(PlayerEntity player) {
      markDirty();
      World world = player.world;
      if (!world.isRemote) {
        SpecialLootChestTile tile = getTile(world);
        if (tile != null) {
          tile.closeInventory(player);
        }
        ((ServerWorld) world).getSavedData().save();
      }
    }

    public CompoundNBT writeItems() {
      CompoundNBT result = new CompoundNBT();
      return ItemStackHelper.saveAllItems(result, this.contents);
    }

    public String writeName() {
      return ITextComponent.Serializer.toJson(this.name);
    }
  }

  private static ServerWorld getServerWorld() {
    return ServerLifecycleHooks.getCurrentServer().getWorld(DimensionType.OVERWORLD);
  }

  private static OldChestData getInstance(ServerPlayerEntity player) {
    return getServerWorld().getSavedData().getOrCreate(() -> new OldChestData(player), ID(player));
  }

  @Nullable
  public static OldSpecialChestInventory getInventory(IWorld world, BlockPos pos, ServerPlayerEntity player) {
    TileEntity te = world.getTileEntity(pos);
    if (!(te instanceof SpecialLootChestTile)) {
      return null;
    }

    SpecialLootChestTile tile = (SpecialLootChestTile) te;
    OldChestData data = getInstance(player);
    OldSpecialChestInventory inventory = data.getInventory(world, pos);
    if (inventory == null) {
      return null;
    }
    if (inventory.wasNew()) {
      tile.fillWithLoot(player, inventory);
      data.setInventory(inventory, world, pos);
      tile.markForSync();
    }

    return inventory;
  }
}*/
