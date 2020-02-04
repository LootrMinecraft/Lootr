package noobanidus.mods.lootr.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.BarrelTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.data.BooleanData;
import noobanidus.mods.lootr.init.ModTiles;

import javax.annotation.Nullable;
import java.util.Random;

public class SpecialLootBarrelTile extends BarrelTileEntity implements ITickableTileEntity {
  private Random random = new Random();
  private ResourceLocation savedLootTable = null;
  private long seed = -1;
  private boolean synchronised = false;

  public SpecialLootBarrelTile() {
    super(ModTiles.SPECIAL_LOOT_BARREL);
  }

  public SpecialLootBarrelTile(TileEntityType<?> tile) {
    super(tile);
  }

  @Override
  public void setLootTable(ResourceLocation lootTableIn, long seedIn) {
    super.setLootTable(lootTableIn, seedIn);
    this.savedLootTable = lootTableIn;
    this.seed = seedIn;
    markForSync();
  }

  public void markForSync() {
    this.synchronised = false;
  }

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

  @SuppressWarnings({"unused", "Duplicates"})
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

  @SuppressWarnings("Duplicates")
  @Override
  public void read(CompoundNBT compound) {
    super.read(compound);
    if (compound.contains("specialLootBarrel_table", Constants.NBT.TAG_STRING)) {
      savedLootTable = new ResourceLocation(compound.getString("specialLootBarrel_table"));
    }
    if (compound.contains("specialLootBarrel_seed", Constants.NBT.TAG_LONG)) {
      seed = compound.getLong("specialLootBarrel_seed");
    }
    if (savedLootTable == null && compound.contains("LootTable", Constants.NBT.TAG_STRING)) {
      savedLootTable = new ResourceLocation(compound.getString("LootTable"));
      markForSync();
    }
    if (seed == 0L && compound.contains("LootTableSeed", Constants.NBT.TAG_LONG)) {
      seed = compound.getLong("LootTableSeed");
    }
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    compound = super.write(compound);
    if (savedLootTable != null) {
      compound.putString("specialLootBarrel_table", savedLootTable.toString());
    }
    if (seed != -1) {
      compound.putLong("specialLootBarrel_seed", seed);
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

  @SuppressWarnings("Duplicates")
  @Override
  public void tick() {
    if (this.world != null && !synchronised) {
      if (!this.world.isRemote() && isSpecialLootChest()) {
        this.synchronised = true;
        BooleanData.markLootChest(world, getPos());
        BlockState state = this.world.getBlockState(getPos());
        this.world.notifyBlockUpdate(pos, state, state, 8);
      }
    }
  }
}
