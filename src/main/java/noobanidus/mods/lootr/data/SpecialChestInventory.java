package noobanidus.mods.lootr.data;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import noobanidus.mods.lootr.api.inventory.ILootrInventory;
import noobanidus.mods.lootr.api.tile.ILootTile;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
public class SpecialChestInventory implements ILootrInventory {
  private final ChestData chestData;
  private final NonNullList<ItemStack> contents;
  private final ITextComponent name;

  @Nullable
  private BlockPos pos;

  public SpecialChestInventory(ChestData chestData, NonNullList<ItemStack> contents, ITextComponent name, @Nullable BlockPos pos) {
    this.chestData = chestData;
    this.contents = contents;
    this.name = name;
    this.pos = pos;
  }

  public SpecialChestInventory(ChestData chestData, NBTTagCompound items, String componentAsJSON, BlockPos pos) {
    this.chestData = chestData;
    this.name = ITextComponent.Serializer.jsonToComponent(componentAsJSON);
    this.contents = NonNullList.withSize(27, ItemStack.EMPTY);
    ItemStackHelper.loadAllItems(items, this.contents);
    this.pos = pos;
  }

  public void setBlockPos(BlockPos pos) {
    this.pos = pos;
  }

  @Override
  @Nullable
  public TileEntityLockableLoot getTile(World world) {
    if (world == null || world.isRemote || pos == null) {
      return null;
    }

    TileEntity te = world.getTileEntity(pos);
    if (te instanceof ILootTile) {
      return (TileEntityLockableLoot) te;
    }

    return null;
  }

  @Override
  @Nullable
  public LootrChestMinecartEntity getEntity(World world) {
    if (world == null || world.isRemote || chestData.getEntityId() == null) {
      return null;
    }

    if (!(world instanceof WorldServer)) {
      return null;
    }

    WorldServer serverWorld = (WorldServer) world;
    Entity entity = serverWorld.getEntityFromUuid(chestData.getEntityId());
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
    chestData.setDirty(true);
  }

  @Override
  public boolean isUsableByPlayer(EntityPlayer player) {
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
  public Container createContainer(InventoryPlayer inventory, EntityPlayer player) {
    return new ContainerChest(inventory, this, player);
  }

  @Override
  public void openInventory(EntityPlayer player) {
    World world = player.world;
    TileEntityLockableLoot tile = getTile(world);
    if (tile != null) {
      tile.openInventory(player);
    }
    if (chestData.getEntityId() != null) {
      LootrChestMinecartEntity entity = getEntity(world);
      if (entity != null) {
        entity.openInventory(player);
      }
    }
  }

  @Override
  public void closeInventory(EntityPlayer player) {
    markDirty();
    World world = player.world;
    if (pos != null) {
      TileEntityLockableLoot tile = getTile(world);
      if (tile != null) {
        tile.closeInventory(player);
      }
    }
    if (chestData.getEntityId() != null) {
      LootrChestMinecartEntity entity = getEntity(world);
      if (entity != null) {
        entity.closeInventory(player);
      }
    }
  }

  public NBTTagCompound writeItems() {
    NBTTagCompound result = new NBTTagCompound();
    return ItemStackHelper.saveAllItems(result, this.contents);
  }

  public String writeName() {
    return ITextComponent.Serializer.componentToJson(this.name);
  }

  @Override
  @Nullable
  public BlockPos getPos() {
    return pos;
  }

  @Override
  public NonNullList<ItemStack> getInventoryContents() {
    return this.contents;
  }

  @Override
  public int getInventoryStackLimit() {
    return 64;
  }

  public boolean isItemValidForSlot(int index, ItemStack stack)
  {
    return true;
  }

  public int getField(int id)
  {
    return 0;
  }

  public void setField(int id, int value)
  {
  }

  public int getFieldCount()
  {
    return 0;
  }

  @Override
  public String getGuiID() {
    return "minecraft:chest";
  }

  @Override
  public String getName() {
    return this.name.getFormattedText();
  }

  @Override
  public boolean hasCustomName() {
    return this.name != null;
  }
}
