package noobanidus.mods.lootr.tiles;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.DoubleSidedInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.data.BooleanData;
import noobanidus.mods.lootr.data.NewChestData;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.init.ModTiles;

import javax.annotation.Nullable;
import java.util.Random;

@SuppressWarnings({"Duplicates", "ConstantConditions", "NullableProblems", "WeakerAccess"})
public class SpecialLootChestTile extends ChestTileEntity implements ILootTile {
  private int ticksSinceSync;
  private int specialNumPlayersUsingChest;
  private Random random = new Random();
  private ResourceLocation savedLootTable = null;
  private long seed = -1;
  private boolean synchronised = false;

  public SpecialLootChestTile() {
    super(ModTiles.SPECIAL_LOOT_CHEST);
  }

  public SpecialLootChestTile(TileEntityType<?> tile) {
    super(tile);
  }

  @Override
  public void setLootTable(ResourceLocation lootTableIn, long seedIn) {
    super.setLootTable(lootTableIn, seedIn);
    this.setLootTable(lootTableIn, seedIn, true);
  }

  public void setLootTable(ResourceLocation lootTableIn, long seedIn, boolean doSync) {
/*    Lootr.LOG.debug("Set chest tile entity at " + (getPos() == null ? "unknown location" : getPos().toString()) + " to loot table " + lootTableIn.toString());*/
    this.savedLootTable = lootTableIn;
    this.seed = seedIn;
    if (doSync) {
      markForSync();
    }
  }

  @Override
  public void markForSync() {
/*    Lootr.LOG.debug("Marked chest tile entity at " + getPos().toString() + " for synchronisation");*/
    this.synchronised = false;
  }


  @Override
  public boolean isSpecialLootChest() {
    return savedLootTable != null;
  }

  @Override
  protected boolean checkLootAndRead(CompoundNBT compound) {
    if (isSpecialLootChest()) {
      return true;
    }
    return super.checkLootAndRead(compound);
  }

  @Override
  protected boolean checkLootAndWrite(CompoundNBT compound) {
    if (isSpecialLootChest()) {
      return true;
    }
    return super.checkLootAndWrite(compound);
  }

  @Override
  public void fillWithLoot(@Nullable PlayerEntity player) {
    // TODO: Override
  }

  @Override
  public void fillWithLoot(PlayerEntity player, IInventory inventory) {
/*    Lootr.LOG.debug("Filling chest tile entity at " + getPos().toString() + " with loot for " + (player == null ? "null player" : player.getScoreboardName()));*/
    if (this.world != null && this.savedLootTable != null && this.world.getServer() != null) {
      LootTable loottable = this.world.getServer().getLootTableManager().getLootTableFromLocation(this.savedLootTable);
      LootContext.Builder builder = (new LootContext.Builder((ServerWorld) this.world)).withParameter(LootParameters.POSITION, new BlockPos(this.pos)).withSeed(ConfigManager.RANDOMISE_SEED.get() ? random.nextLong() : this.seed);
      if (player != null) {
        builder.withLuck(player.getLuck()).withParameter(LootParameters.THIS_ENTITY, player);
      }

      loottable.fillInventory(inventory, builder.build(LootParameterSets.CHEST));
    }
  }

  @Override
  public void read(CompoundNBT compound) {
    super.read(compound);
    if (compound.contains("specialLootChest_table", Constants.NBT.TAG_STRING)) {
      savedLootTable = new ResourceLocation(compound.getString("specialLootChest_table"));
    }
    if (compound.contains("specialLootChest_seed", Constants.NBT.TAG_LONG)) {
      seed = compound.getLong("specialLootChest_seed");
    }
    if (savedLootTable == null && compound.contains("LootTable", Constants.NBT.TAG_STRING)) {
      savedLootTable = new ResourceLocation(compound.getString("LootTable"));
      if (seed == 0L && compound.contains("LootTableSeed", Constants.NBT.TAG_LONG)) {
        seed = compound.getLong("LootTableSeed");
      }
      setLootTable(savedLootTable, seed);
    }
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    compound = super.write(compound);
    if (savedLootTable != null) {
      compound.putString("specialLootChest_table", savedLootTable.toString());
    }
    if (seed != -1) {
      compound.putLong("specialLootChest_seed", seed);
    }
    return compound;
  }

  @Override
  public CompoundNBT getUpdateTag() {
    return write(super.getUpdateTag());
  }

  @Nullable
  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    if (isSpecialLootChest()) {
      return new SUpdateTileEntityPacket(this.pos, 9, getUpdateTag());
    } else {
      return null;
    }
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
    read(pkt.getNbtCompound());
  }

  // Specifically disabled to prevent weird interactions
  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    if (!isSpecialLootChest()) {
      return super.getCapability(cap, side);
    }

    return LazyOptional.empty();
  }

  @Override
  public void tick() {
    if (this.world != null && !synchronised) {
      if (!this.world.isRemote() && isSpecialLootChest()) {
        this.synchronised = true;
        BooleanData.markLootChest(world, getPos());
        BlockState state = this.world.getBlockState(getPos());
        boolean trapped = state.getBlock() == Blocks.TRAPPED_CHEST;
        if (state.getBlock() == Blocks.CHEST || state.getBlock() == Blocks.TRAPPED_CHEST) {
          world.setBlockState(pos, (trapped ? ModBlocks.CHEST : ModBlocks.TRAPPED_CHEST).getDefaultState().with(ChestBlock.FACING, state.get(ChestBlock.FACING)).with(ChestBlock.TYPE, ChestType.SINGLE));
          TileEntity te = world.getTileEntity(pos);
          if (te instanceof SpecialLootChestTile && te != this) {
            ((SpecialLootChestTile) te).setLootTable(savedLootTable, seed, false);
            BooleanData.markLootChest(world, getPos());
          } else if (te == this) {
            Lootr.LOG.error("Replaced chest tile but it was myself");
          }
        }
        this.world.notifyBlockUpdate(pos, state, state, 8);
/*        Lootr.LOG.debug("Synchronised chest block state at " + pos.toString());*/
      }
    }

    int i = this.pos.getX();
    int j = this.pos.getY();
    int k = this.pos.getZ();
    ++this.ticksSinceSync;
    /*    int count = calculatePlayersUsingSync(this.world, this, this.ticksSinceSync, i, j, k, this.specialNumPlayersUsingChest);*/
/*    if (count != specialNumPlayersUsingChest) {
      Lootr.LOG.debug("Number of players using chest changed from " + specialNumPlayersUsingChest + " to " + count);
    }*/
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

  private void playSound(SoundEvent soundIn) {
    this.world.playSound(null, getPos(), soundIn, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
  }

  public static int calculatePlayersUsingSync(World world, LockableTileEntity tile, int ticksSinceSync, int x, int y, int z, int numPlayersUsing) {
    if (!world.isRemote && numPlayersUsing != 0 && (ticksSinceSync + x + y + z) % 200 == 0) {
      numPlayersUsing = calculatePlayersUsing(world, tile, x, y, z);
    }

    return numPlayersUsing;
  }

  public static int calculatePlayersUsing(World world, LockableTileEntity tile, int x, int y, int z) {
    int i = 0;

    for (PlayerEntity playerentity : world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB((double) ((float) x - 5.0F), (double) ((float) y - 5.0F), (double) ((float) z - 5.0F), (double) ((float) (x + 1) + 5.0F), (double) ((float) (y + 1) + 5.0F), (double) ((float) (z + 1) + 5.0F)))) {
      if (playerentity.openContainer instanceof ChestContainer) {
        IInventory inv = ((ChestContainer) playerentity.openContainer).getLowerChestInventory();
        if ((inv instanceof NewChestData.SpecialChestInventory && ((NewChestData.SpecialChestInventory) inv).getPos().equals(tile.getPos())) || (inv == tile || inv instanceof DoubleSidedInventory && ((DoubleSidedInventory) inv).isPartOfLargeChest(tile))) {
          ++i;
        }
      }
    }

    return i;
  }

  @Override
  public void openInventory(PlayerEntity player) {
    if (!player.isSpectator()) {
/*      Lootr.LOG.debug("Player " + player.getScoreboardName() + " opened loot chest at " + getPos().toString());*/
      if (this.specialNumPlayersUsingChest < 0) {
        this.specialNumPlayersUsingChest = 0;
      }

      ++this.specialNumPlayersUsingChest;
/*      Lootr.LOG.debug("Total number of players at " + getPos().toString() + " using chest is now: " + specialNumPlayersUsingChest);*/
      this.onOpenOrClose();
    }
  }

  @Override
  public void closeInventory(PlayerEntity player) {
    if (!player.isSpectator()) {
      --this.specialNumPlayersUsingChest;
/*      Lootr.LOG.debug("Player " + player.getScoreboardName() + " closed loot chest at " + getPos().toString() + ", total number of players now using is " + specialNumPlayersUsingChest);*/
      this.onOpenOrClose();
    }
  }

  @Override
  protected void onOpenOrClose() {
    Block block = this.getBlockState().getBlock();
    if (block instanceof ChestBlock) {
      this.world.addBlockEvent(this.pos, block, 1, this.specialNumPlayersUsingChest);
      this.world.notifyNeighborsOfStateChange(this.pos, block);
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
}
