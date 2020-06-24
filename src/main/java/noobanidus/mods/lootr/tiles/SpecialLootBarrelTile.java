package noobanidus.mods.lootr.tiles;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.BarrelTileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.init.ModTiles;

import javax.annotation.Nullable;
import java.util.Random;

@SuppressWarnings({"ConstantConditions", "NullableProblems", "WeakerAccess"})
public class SpecialLootBarrelTile extends BarrelTileEntity implements ILootTile {
  private int specialNumPlayersUsingBarrel;

  private Random random = new Random();
  private ResourceLocation savedLootTable = null;
  private long seed = -1;

  public SpecialLootBarrelTile() {
    super(ModTiles.SPECIAL_LOOT_BARREL);
  }

  @Override
  public void setLootTable(ResourceLocation lootTableIn, long seedIn) {
    this.savedLootTable = lootTableIn;
    this.seed = seedIn;
    super.setLootTable(lootTableIn, seedIn);
  }

  @Override
  public void fillWithLoot(@Nullable PlayerEntity player) {
    // TODO: Override
  }

  @Override
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
    if (compound.contains("specialLootChest_table", Constants.NBT.TAG_STRING)) {
      savedLootTable = new ResourceLocation(compound.getString("specialLootChest_table"));
    }
    if (compound.contains("specialLootChest_seed", Constants.NBT.TAG_LONG)) {
      seed = compound.getLong("specialLootChest_seed");
    }
    if (savedLootTable == null && compound.contains("LootTable", Constants.NBT.TAG_STRING)) {
      savedLootTable = new ResourceLocation(compound.getString("LootTable"));
      if (compound.contains("LootTableSeed", Constants.NBT.TAG_LONG)) {
        seed = compound.getLong("LootTableSeed");
      }
      setLootTable(savedLootTable, seed);
    }
    super.read(compound);
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
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    return LazyOptional.empty();
  }

  @Override
  public void barrelTick() {
    int x = this.pos.getX();
    int y = this.pos.getY();
    int z = this.pos.getZ();
    this.specialNumPlayersUsingBarrel = SpecialLootChestTile.calculatePlayersUsing(this.world, this, x, y, z);
    if (this.specialNumPlayersUsingBarrel > 0) {
      this.scheduleTick();
    } else {
      BlockState state = this.getBlockState();
      if (state.getBlock() != ModBlocks.BARREL && state.getBlock() != Blocks.BARREL) {
        this.remove();
        return;
      }

      boolean open = state.get(BarrelBlock.PROPERTY_OPEN);
      if (open) {
        this.playSound(state, SoundEvents.BLOCK_BARREL_CLOSE);
        this.setOpenProperty(state, false);
      }
    }
  }

  private void setOpenProperty(BlockState state, boolean open) {
    this.world.setBlockState(this.getPos(), state.with(BarrelBlock.PROPERTY_OPEN, open), 3);
  }

  private void playSound(BlockState state, SoundEvent sound) {
    Vec3i dir = state.get(BarrelBlock.PROPERTY_FACING).getDirectionVec();
    double x = (double) this.pos.getX() + 0.5D + (double) dir.getX() / 2.0D;
    double y = (double) this.pos.getY() + 0.5D + (double) dir.getY() / 2.0D;
    double z = (double) this.pos.getZ() + 0.5D + (double) dir.getZ() / 2.0D;
    this.world.playSound(null, x, y, z, sound, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
  }

  private void scheduleTick() {
    this.world.getPendingBlockTicks().scheduleTick(this.getPos(), this.getBlockState().getBlock(), 5);
  }

  @Override
  public void openInventory(PlayerEntity player) {
    if (!player.isSpectator()) {
      if (this.specialNumPlayersUsingBarrel < 0) {
        this.specialNumPlayersUsingBarrel = 0;
      }

      ++this.specialNumPlayersUsingBarrel;
      BlockState state = this.getBlockState();
      boolean open = state.get(BarrelBlock.PROPERTY_OPEN);
      if (!open) {
        this.playSound(state, SoundEvents.BLOCK_BARREL_OPEN);
        this.setOpenProperty(state, true);
      }

      this.scheduleTick();
    }
  }

  @Override
  public void closeInventory(PlayerEntity player) {
    if (!player.isSpectator()) {
      --this.specialNumPlayersUsingBarrel;
    }
  }
}
