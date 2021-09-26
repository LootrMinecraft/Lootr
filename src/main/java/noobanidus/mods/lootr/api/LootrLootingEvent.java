package noobanidus.mods.lootr.api;

import net.minecraft.entity.item.minecart.ContainerMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class LootrLootingEvent extends PlayerEvent {
  protected final World world;
  protected final RegistryKey<World> dimension;
  protected final ILootrInventory inventory;
  protected final LockableLootTileEntity tile;
  protected final ContainerMinecartEntity cartEntity;

  public LootrLootingEvent(PlayerEntity player, World world, RegistryKey<World> dimension, ILootrInventory inventory, LockableLootTileEntity tile, ContainerMinecartEntity cartEntity) {
    super(player);
    this.world = world;
    this.dimension = dimension;
    this.inventory = inventory;
    this.tile = tile;
    this.cartEntity = cartEntity;
  }

  // Returns the unique identifier associated with the
  // entity or tile.
  @Nullable
  public UUID getUniqueId() {
    if (this.tile instanceof ILootTile) {
      return ((ILootTile) this.tile).getTileId();
    }
    if (cartEntity != null) {
      return cartEntity.getUUID();
    }
    return null;
  }

  // It is unlikely that this will ever be null, but there is
  // a possibility. Use this to access the loot table that is
  // going to be generated on, especially for Minecarts.
  @Nullable
  public ResourceLocation getTable() {
    if (tile instanceof ILootTile) {
      return ((ILootTile) tile).getTable();
    }
    if (cartEntity != null) {
      return cartEntity.lootTable;
    }
    return null;
  }

  // There is a strong likelihood that this value is -1 which
  // means "generate your own random seed".
  public long getSeed() {
    if (tile instanceof ILootTile) {
      return ((ILootTile) tile).getSeed();
    }
    if (cartEntity != null) {
      return cartEntity.lootTableSeed;
    }
    return -1;
  }

  // Contains the actual world that reflects the dimension
  // contained within getDimension.
  public World getWorld() {
    return world;
  }

  // Contains the dimension that this chest is being opened in.
  public RegistryKey<World> getDimension() {
    return dimension;
  }

  // This may be null for entities. For tiles, this is the original
  // location of the tile rather than its current location. To get
  // its current location, use the getPos function of the tile.
  @Nullable
  public BlockPos getPos() {
    return inventory.getPos();
  }

  // Returns a list of previous openers of this chest not
  // including the current opener.
  public Set<UUID> getOpeners() {
    if (tile instanceof ILootTile) {
      return ((ILootTile) tile).getOpeners();
    }
    if (cartEntity instanceof ILootCart) {
      return ((ILootCart) cartEntity).getOpeners();
    }
    return Collections.emptySet();
  }

  // Returns the count of getOpeners.
  public int getOpenerCount() {
    return getOpeners().size();
  }

  // If this event is associated with any tile entity this
  // will return that tile entity.
  @Nullable
  public LockableLootTileEntity getTile() {
    return tile;
  }

  // If this event is associated with a minecart entity
  // this will return that Minecart entity.
  public ContainerMinecartEntity getMinecart() {
    return cartEntity;
  }

  // A quick boolean check to determine if this is a Minecart
  // or a tile entity.
  public boolean isMinecart() {
    return cartEntity != null;
  }

  // This event is fired before the "filler" instance is called
  // Modifications to the loot table & seed that take place here
  // will be reflected in the function call.
  // This event is cancelable. Canceling it prevents any loot
  // from being generated.
  @Cancelable
  public static class Pre extends LootrLootingEvent {
    private ResourceLocation newTable = null;
    private long newSeed = Long.MIN_VALUE;

    public Pre(PlayerEntity player, World world, RegistryKey<World> dimension, ILootrInventory inventory, LockableLootTileEntity tile, ContainerMinecartEntity cartEntity) {
      super(player, world, dimension, inventory, tile, cartEntity);
    }


    // Replacing the loot table for this event does not replace
    // the overall loot table of the inventory. Instead, it merely
    // replaces the single instance.
    // If a loot table has been given an override, this function
    // will return that value.
    public ResourceLocation getNewTable() {
      if (newTable == null) {
        return getTable();
      }
      return newTable;
    }

    // Allows you to override the loot table being used.
    public void setTable(ResourceLocation newTable) {
      this.newTable = newTable;
    }

    // If the seed has been modified by setSeed, this will return
    // the override value. See getNewTable.
    public long getNewSeed() {
      if (newSeed == Long.MIN_VALUE) {
        return getSeed();
      }
      return newSeed;
    }

    // Allows you to override the seed being used for this loot
    // filling instance.
    public void setSeed(long newSeed) {
      this.newSeed = newSeed;
    }
  }

  // This event is fired after the chest has been filled
  // allowing for loot to be altered.
  public static class Post extends LootrLootingEvent {
    public Post(PlayerEntity player, World world, RegistryKey<World> dimension, ILootrInventory inventory, LockableLootTileEntity tile, ContainerMinecartEntity cartEntity) {
      super(player, world, dimension, inventory, tile, cartEntity);
    }

    // This function can be used to get the NonNullList containing
    // all of the items that the filler has placed in the the chest.
    // Editing this list will edit the items that go in the chest.
    public NonNullList<ItemStack> getContents() {
      return inventory.getContents();
    }
  }
}
