package noobanidus.mods.lootr.block.tile;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.tile.ILootTile;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.data.SpecialChestInventory;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.init.ModTiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings({"Duplicates", "ConstantConditions", "NullableProblems", "WeakerAccess"})
public class LootrChestTileEntity extends TileEntityChest implements ILootTile, ITickable {
  public Set<UUID> openers = new HashSet<>();
  private int ticksSinceSync;
  private int specialNumPlayersUsingChest;
  private ResourceLocation savedLootTable = null;
  private long seed = -1;
  private UUID tileId;
  private boolean opened;

  public LootrChestTileEntity() {
    super();
  }

  @Override
  public UUID getTileId() {
    if (this.tileId == null) {
      this.tileId = UUID.randomUUID();
    }
    return this.tileId;
  }

  @Override
  public void setLootTable(ResourceLocation lootTableIn, long seedIn) {
    super.setLootTable(lootTableIn, seedIn);
    this.savedLootTable = lootTableIn;
    this.seed = seedIn;
  }

  public boolean isOpened() {
    return opened;
  }

  public void setOpened(boolean opened) {
    this.opened = opened;
  }

  @Override
  public void fillWithLoot(@Nullable EntityPlayer player) {
  }

  @Override
  public void fillWithLoot(EntityPlayer player, IInventory inventory, @Nullable ResourceLocation overrideTable, long seed) {
    if (this.world != null && this.savedLootTable != null && this.world.getMinecraftServer() != null) {
      BlockPos worldPosition = this.getPos();
      LootTable loottable = this.world.getLootTableManager().getLootTableFromLocation(overrideTable != null ? overrideTable : this.savedLootTable);
      if (loottable == LootTable.EMPTY_LOOT_TABLE) {
        Lootr.LOG.error("Unable to fill loot chest in " + this.world + " at " + worldPosition + " as the loot table '" + (overrideTable != null ? overrideTable : this.savedLootTable) + "' couldn't be resolved! Please search the loot table in `latest.log` to see if there are errors in loading.");
      }
      if (player instanceof EntityPlayerMP) {
        //CriteriaTriggers.GENERATE_LOOT.trigger((EntityPlayerMP) player, overrideTable != null ? overrideTable : this.lootTable);
      }
      Random random;
      long theSeed = ConfigManager.RANDOMISE_SEED ? ThreadLocalRandom.current().nextLong() : seed == Long.MIN_VALUE ? this.seed : seed;
      LootContext.Builder builder = (new LootContext.Builder((WorldServer) this.world));
      if (player != null) {
        builder.withLuck(player.getLuck()).withPlayer(player);
      }

      if (theSeed == 0L)
      {
        random = new Random();
      }
      else
      {
        random = new Random(theSeed);
      }


      loottable.fillInventory(inventory, random, builder.build());
    }
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    if (compound.hasKey("specialLootChest_table", Constants.NBT.TAG_STRING)) {
      savedLootTable = new ResourceLocation(compound.getString("specialLootChest_table"));
    }
    if (compound.hasKey("specialLootChest_seed", Constants.NBT.TAG_LONG)) {
      seed = compound.getLong("specialLootChest_seed");
    }
    if (savedLootTable == null && compound.hasKey("LootTable", Constants.NBT.TAG_STRING)) {
      savedLootTable = new ResourceLocation(compound.getString("LootTable"));
      if (seed == 0L && compound.hasKey("LootTableSeed", Constants.NBT.TAG_LONG)) {
        seed = compound.getLong("LootTableSeed");
      }
    }
    if (compound.hasUniqueId("tileId")) {
      this.tileId = compound.getUniqueId("tileId");
    }
    if (compound.hasKey("LootrOpeners")) {
      NBTTagList openers = compound.getTagList("LootrOpeners", Constants.NBT.TAG_COMPOUND);
      this.openers.clear();
      for (NBTBase item : openers) {
        this.openers.add(NBTUtil.getUUIDFromTag((NBTTagCompound)item));
      }
    }
    super.readFromNBT(compound);
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    compound = super.writeToNBT(compound);
    if (savedLootTable != null) {
      compound.setString("specialLootChest_table", savedLootTable.toString());
      compound.setString("LootTable", savedLootTable.toString());
    }
    if (seed != -1) {
      compound.setLong("specialLootChest_seed", seed);
      compound.setLong("LootTableSeed", seed);
    }
    if(this.tileId != null)
      compound.setUniqueId("tileId", this.tileId);
    NBTTagList list = new NBTTagList();
    for (UUID opener : this.openers) {
      list.appendTag(NBTUtil.createUUIDTag(opener));
    }
    compound.setTag("LootrOpeners", list);
    return compound;
  }

  @Override
  public <T> T getCapability(Capability<T> cap, EnumFacing side) {
    return null;
  }

  @Override
  public void update() {
    int i = this.pos.getX();
    int j = this.pos.getY();
    int k = this.pos.getZ();
    ++this.ticksSinceSync;

    this.specialNumPlayersUsingChest = calculatePlayersUsingSync(this.world, this, this.ticksSinceSync, i, j, k, this.specialNumPlayersUsingChest);
    this.prevLidAngle = this.lidAngle;
    if (this.specialNumPlayersUsingChest > 0 && this.lidAngle == 0.0F) {
      this.playSound(SoundEvents.BLOCK_CHEST_OPEN);
    }

    if (this.specialNumPlayersUsingChest == 0 && this.lidAngle > 0.0F || this.specialNumPlayersUsingChest > 0 && this.lidAngle < 1.0F) {
      float f1 = this.lidAngle;
      if (this.specialNumPlayersUsingChest > 0) {
        this.lidAngle += 0.1F;
      } else {
        this.lidAngle -= 0.1F;
      }

      if (this.lidAngle > 1.0F) {
        this.lidAngle = 1.0F;
      }

      if (this.lidAngle < 0.5F && f1 >= 0.5F) {
        this.playSound(SoundEvents.BLOCK_CHEST_CLOSE);
      }

      if (this.lidAngle < 0.0F) {
        this.lidAngle = 0.0F;
      }
    }
  }

  @Override
  public ResourceLocation getTable() {
    return savedLootTable;
  }

  @Override
  public Set<UUID> getOpeners() {
    return openers;
  }

  private void playSound(SoundEvent soundIn) {
    this.world.playSound(null, getPos(), soundIn, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
  }

  public static int calculatePlayersUsingSync(World world, TileEntityLockableLoot tile, int ticksSinceSync, int x, int y, int z, int numPlayersUsing) {
    if (!world.isRemote && numPlayersUsing != 0 && (ticksSinceSync + x + y + z) % 200 == 0) {
      numPlayersUsing = calculatePlayersUsing(world, tile, x, y, z);
    }

    return numPlayersUsing;
  }

  public static int calculatePlayersUsing(World world, TileEntityLockableLoot tile, int x, int y, int z) {
    if (tile == null) {
      return 0;
    }
    int i = 0;

    for (EntityPlayer playerentity : world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(x - 5.0, y - 5.0, z - 5.0, (x + 1) + 5.0, (y + 1) + 5.0, (z + 1) + 5.0))) {
      if (playerentity.openContainer instanceof ContainerChest) {
        IInventory inv = ((ContainerChest) playerentity.openContainer).getLowerChestInventory();
        if (inv == null) {
          continue;
        }
        if (inv == tile || (inv instanceof SpecialChestInventory && tile.getPos().equals(((SpecialChestInventory) inv).getPos()))) {
          ++i;
        }
      }
    }

    return i;
  }

  @Override
  public void openInventory(EntityPlayer player) {
    if (!player.isSpectator()) {
      if (this.specialNumPlayersUsingChest < 0) {
        this.specialNumPlayersUsingChest = 0;
      }

      ++this.specialNumPlayersUsingChest;
      this.signalOpenCount();
    }
  }

  @Override
  public void closeInventory(EntityPlayer player) {
    if (!player.isSpectator()) {
      --this.specialNumPlayersUsingChest;
      this.signalOpenCount();
      openers.add(player.getUniqueID());
      this.markDirty();
      updatePacketViaState();
    }
  }

  @Override
  public void updatePacketViaState() {
    if (world != null && !world.isRemote) {
      IBlockState state = world.getBlockState(getPos());
      world.notifyBlockUpdate(getPos(), state, state, 8);
    }
  }

  protected void signalOpenCount() {
    if(this.getBlockType() instanceof BlockChest) {
      this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.specialNumPlayersUsingChest);
      this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
    }
  }

  @Override
  public boolean receiveClientEvent(int id, int type) {
    if (id == 1) {
      this.specialNumPlayersUsingChest = type;
      return true;
    } else {
      return super.receiveClientEvent(id, type);
    }
  }

  @Override
  @Nonnull
  public NBTTagCompound getUpdateTag() {
    return writeToNBT(new NBTTagCompound());
  }

  @Override
  @Nullable
  public SPacketUpdateTileEntity getUpdatePacket() {
    return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
  }

  @Override
  public void onDataPacket(@Nonnull NetworkManager net, @Nonnull SPacketUpdateTileEntity pkt) {
    readFromNBT(pkt.getNbtCompound());
  }

  @Override
  public void checkForAdjacentChests() {
    if (!this.adjacentChestChecked) {
      this.adjacentChestChecked = true;
      this.adjacentChestXNeg = null;
      this.adjacentChestXPos = null;
      this.adjacentChestZNeg = null;
      this.adjacentChestZPos = null;
    }
  }

  public static int getPlayersUsing(IBlockAccess reader, BlockPos posIn) {
    IBlockState blockstate = reader.getBlockState(posIn);
    if (blockstate.getBlock().hasTileEntity(blockstate)) {
      TileEntity tileentity = reader.getTileEntity(posIn);
      if (tileentity instanceof LootrChestTileEntity) {
        return ((LootrChestTileEntity) tileentity).specialNumPlayersUsingChest;
      }
    }

    return 0;
  }
}
