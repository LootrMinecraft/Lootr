package noobanidus.mods.lootr.tiles;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import noobanidus.mods.lootr.api.ILootTile;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.data.SpecialChestInventory;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.init.ModTiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings({"Duplicates", "ConstantConditions", "NullableProblems", "WeakerAccess"})
public class SpecialLootChestTile extends ChestTileEntity implements ILootTile {
  public Set<UUID> openers = new HashSet<>();
  private int ticksSinceSync;
  private int specialNumPlayersUsingChest;
  private ResourceLocation savedLootTable = null;
  private long seed = -1;
  private UUID tileId;
  private boolean opened;

  public SpecialLootChestTile() {
    super(ModTiles.SPECIAL_LOOT_CHEST);
  }

  @Override
  public UUID getTileId() {
    if (this.tileId == null) {
      this.tileId = UUID.randomUUID();
    }
    return this.tileId;
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

  public boolean isOpened() {
    return opened;
  }

  public void setOpened(boolean opened) {
    this.opened = opened;
  }

  @Override
  public void unpackLootTable(@Nullable PlayerEntity player) {
  }

  @Override
  public void fillWithLoot(PlayerEntity player, IInventory inventory, @Nullable ResourceLocation overrideTable, long seed) {
    if (this.level != null && this.savedLootTable != null && this.level.getServer() != null) {
      LootTable loottable = this.level.getServer().getLootTables().get(overrideTable != null ? overrideTable : this.savedLootTable);
      if (player instanceof ServerPlayerEntity) {
        CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayerEntity) player, overrideTable != null ? overrideTable : this.lootTable);
      }
      LootContext.Builder builder = (new LootContext.Builder((ServerWorld) this.level)).withParameter(LootParameters.ORIGIN, Vector3d.atCenterOf(this.worldPosition)).withOptionalRandomSeed(ConfigManager.RANDOMISE_SEED.get() ? ThreadLocalRandom.current().nextLong() : seed == Long.MIN_VALUE ? this.seed : seed);
      if (player != null) {
        builder.withLuck(player.getLuck()).withParameter(LootParameters.THIS_ENTITY, player);
      }

      loottable.fill(inventory, builder.create(LootParameterSets.CHEST));
    }
  }

  @Override
  public void load(BlockState state, CompoundNBT compound) {
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
    if (compound.hasUUID("tileId")) {
      this.tileId = compound.getUUID("tileId");
    } else if (this.tileId == null) {
      getTileId();
    }
    if (compound.contains("LootrOpeners")) {
      ListNBT openers = compound.getList("LootrOpeners", Constants.NBT.TAG_INT_ARRAY);
      this.openers.clear();
      for (INBT item : openers) {
        this.openers.add(NBTUtil.loadUUID(item));
      }
    }
    super.load(state, compound);
  }

  @Override
  public CompoundNBT save(CompoundNBT compound) {
    compound = super.save(compound);
    if (savedLootTable != null) {
      compound.putString("specialLootChest_table", savedLootTable.toString());
      compound.putString("LootTable", savedLootTable.toString());
    }
    if (seed != -1) {
      compound.putLong("specialLootChest_seed", seed);
      compound.putLong("LootTableSeed", seed);
    }
    compound.putUUID("tileId", getTileId());
    ListNBT list = new ListNBT();
    for (UUID opener : this.openers) {
      list.add(NBTUtil.createUUID(opener));
    }
    compound.put("LootrOpeners", list);
    return compound;
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    return LazyOptional.empty();
  }

  @Override
  public void tick() {
    int i = this.worldPosition.getX();
    int j = this.worldPosition.getY();
    int k = this.worldPosition.getZ();
    ++this.ticksSinceSync;

    this.specialNumPlayersUsingChest = calculatePlayersUsingSync(this.level, this, this.ticksSinceSync, i, j, k, this.specialNumPlayersUsingChest);
    this.oOpenness = this.openness;
    if (this.specialNumPlayersUsingChest > 0 && this.openness == 0.0F) {
      this.playSound(SoundEvents.CHEST_OPEN);
    }

    if (this.specialNumPlayersUsingChest == 0 && this.openness > 0.0F || this.specialNumPlayersUsingChest > 0 && this.openness < 1.0F) {
      float f1 = this.openness;
      if (this.specialNumPlayersUsingChest > 0) {
        this.openness += 0.1F;
      } else {
        this.openness -= 0.1F;
      }

      if (this.openness > 1.0F) {
        this.openness = 1.0F;
      }

      if (this.openness < 0.5F && f1 >= 0.5F) {
        this.playSound(SoundEvents.CHEST_CLOSE);
      }

      if (this.openness < 0.0F) {
        this.openness = 0.0F;
      }
    }
  }

  @Override
  public ResourceLocation getTable() {
    return savedLootTable;
  }

  @Override
  public long getSeed() {
    return seed;
  }

  @Override
  public Set<UUID> getOpeners() {
    return openers;
  }

  private void playSound(SoundEvent soundIn) {
    this.level.playSound(null, getBlockPos(), soundIn, SoundCategory.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
  }

  public static int calculatePlayersUsingSync(World world, LockableTileEntity tile, int ticksSinceSync, int x, int y, int z, int numPlayersUsing) {
    if (!world.isClientSide && numPlayersUsing != 0 && (ticksSinceSync + x + y + z) % 200 == 0) {
      numPlayersUsing = calculatePlayersUsing(world, tile, x, y, z);
    }

    return numPlayersUsing;
  }

  public static int calculatePlayersUsing(World world, LockableTileEntity tile, int x, int y, int z) {
    if (tile == null) {
      return 0;
    }
    int i = 0;

    for (PlayerEntity playerentity : world.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(x - 5.0, y - 5.0, z - 5.0, (x + 1) + 5.0, (y + 1) + 5.0, (z + 1) + 5.0))) {
      if (playerentity.containerMenu instanceof ChestContainer) {
        IInventory inv = ((ChestContainer) playerentity.containerMenu).getContainer();
        if (inv == tile || (inv instanceof SpecialChestInventory && ((SpecialChestInventory) inv).getPos().equals(tile.getBlockPos()))) {
          ++i;
        }
      }
    }

    return i;
  }

  @Override
  public void startOpen(PlayerEntity player) {
    if (!player.isSpectator()) {
      if (this.specialNumPlayersUsingChest < 0) {
        this.specialNumPlayersUsingChest = 0;
      }

      ++this.specialNumPlayersUsingChest;
      this.signalOpenCount();
    }
  }

  @Override
  public void stopOpen(PlayerEntity player) {
    if (!player.isSpectator()) {
      --this.specialNumPlayersUsingChest;
      this.signalOpenCount();
      openers.add(player.getUUID());
      this.setChanged();
      updatePacketViaState();
    }
  }

  @Override
  public void updatePacketViaState() {
    if (level != null && !level.isClientSide) {
      BlockState state = level.getBlockState(getBlockPos());
      level.sendBlockUpdated(getBlockPos(), state, state, 8);
    }
  }

  @Override
  protected void signalOpenCount() {
    Block block = this.getBlockState().getBlock();
    if (block instanceof ChestBlock) {
      this.level.blockEvent(this.worldPosition, block, 1, this.specialNumPlayersUsingChest);
      this.level.updateNeighborsAt(this.worldPosition, block);
    }
  }

  @Override
  public boolean triggerEvent(int id, int type) {
    if (id == 1) {
      this.specialNumPlayersUsingChest = type;
      return true;
    } else {
      return super.triggerEvent(id, type);
    }
  }

  @Override
  @Nonnull
  public CompoundNBT getUpdateTag() {
    return save(new CompoundNBT());
  }

  @Override
  @Nullable
  public SUpdateTileEntityPacket getUpdatePacket() {
    return new SUpdateTileEntityPacket(getBlockPos(), 0, getUpdateTag());
  }

  @Override
  public void onDataPacket(@Nonnull NetworkManager net, @Nonnull SUpdateTileEntityPacket pkt) {
    load(ModBlocks.CHEST.defaultBlockState(), pkt.getTag());
  }

  public static int getPlayersUsing(IBlockReader reader, BlockPos posIn) {
    BlockState blockstate = reader.getBlockState(posIn);
    if (blockstate.hasTileEntity()) {
      TileEntity tileentity = reader.getBlockEntity(posIn);
      if (tileentity instanceof SpecialLootChestTile) {
        return ((SpecialLootChestTile) tileentity).specialNumPlayersUsingChest;
      }
    }

    return 0;
  }
}
