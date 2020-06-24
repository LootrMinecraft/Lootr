package noobanidus.mods.lootr.tiles;

import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.DoubleSidedInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
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
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.data.NewChestData;
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

  public SpecialLootChestTile() {
    super(ModTiles.SPECIAL_LOOT_CHEST);
  }

  public SpecialLootChestTile(TileEntityType<?> tile) {
    super(tile);
  }

  @Override
  public void setLootTable(ResourceLocation lootTableIn, long seedIn) {
    super.setLootTable(lootTableIn, seedIn);
    this.savedLootTable = lootTableIn;
    this.seed = seedIn;
  }

  @Override
  public void fillWithLoot(@Nullable PlayerEntity player) {
  }

  @Override
  public void fillWithLoot(PlayerEntity player, IInventory inventory) {
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
    }
    super.read(compound);
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
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    return LazyOptional.empty();
  }

  @Override
  public void tick() {
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
      if (this.specialNumPlayersUsingChest < 0) {
        this.specialNumPlayersUsingChest = 0;
      }

      ++this.specialNumPlayersUsingChest;
      this.onOpenOrClose();
    }
  }

  @Override
  public void closeInventory(PlayerEntity player) {
    if (!player.isSpectator()) {
      --this.specialNumPlayersUsingChest;
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
