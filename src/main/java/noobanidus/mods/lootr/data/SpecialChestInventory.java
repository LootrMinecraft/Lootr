package noobanidus.mods.lootr.data;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import noobanidus.mods.lootr.api.ILootrInventory;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.api.ILootTile;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
public class SpecialChestInventory implements ILootrInventory {
  private NewChestData newChestData;
  private final NonNullList<ItemStack> contents;
  private final ITextComponent name;

  @Nullable
  private BlockPos pos;

  public SpecialChestInventory(NewChestData newChestData, NonNullList<ItemStack> contents, ITextComponent name, @Nullable BlockPos pos) {
    this.newChestData = newChestData;
    this.contents = contents;
    this.name = name;
    this.pos = pos;
  }

  public SpecialChestInventory(NewChestData newChestData, CompoundNBT items, String componentAsJSON, BlockPos pos) {
    this.newChestData = newChestData;
    this.name = ITextComponent.Serializer.getComponentFromJson(componentAsJSON);
    this.contents = NonNullList.withSize(27, ItemStack.EMPTY);
    ItemStackHelper.loadAllItems(items, this.contents);
    this.pos = pos;
  }

  public void setBlockPos(BlockPos pos) {
    this.pos = pos;
  }

  @Override
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
  @Nullable
  public LootrChestMinecartEntity getEntity(World world) {
    if (world == null || world.isRemote() || newChestData.getEntityId() == null) {
      return null;
    }

    if (!(world instanceof ServerWorld)) {
      return null;
    }

    ServerWorld serverWorld = (ServerWorld) world;
    Entity entity = serverWorld.getEntityByUuid(newChestData.getEntityId());
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
    newChestData.markDirty();
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
    if (newChestData.getEntityId() != null) {
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
    if (newChestData.getEntityId() != null) {
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

  @Override
  @Nullable
  public BlockPos getPos() {
    return pos;
  }

  @Override
  public NonNullList<ItemStack> getContents() {
    return this.contents;
  }
}
