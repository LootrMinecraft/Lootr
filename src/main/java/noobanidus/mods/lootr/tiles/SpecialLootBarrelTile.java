package noobanidus.mods.lootr.tiles;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
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
import net.minecraft.tileentity.BarrelTileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import noobanidus.mods.lootr.api.ILootTile;
import noobanidus.mods.lootr.blocks.LootrBarrelBlock;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.init.ModTiles;
import noobanidus.mods.lootr.util.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings({"ConstantConditions", "NullableProblems", "WeakerAccess"})
public class SpecialLootBarrelTile extends BarrelTileEntity implements ILootTile {
  public Set<UUID> openers = new HashSet<>();
  private int specialNumPlayersUsingBarrel;
  private ResourceLocation savedLootTable = null;
  private long seed = -1;
  private UUID tileId = null;

  public SpecialLootBarrelTile() {
    super(ModTiles.SPECIAL_LOOT_BARREL);
  }

  @Nonnull
  @Override
  public IModelData getModelData() {
    IModelData data = new ModelDataMap.Builder().withInitial(LootrBarrelBlock.OPENED, false).build();
    PlayerEntity player = Getter.getPlayer();
    if (player != null) {
      data.setData(LootrBarrelBlock.OPENED, openers.contains(player.getUniqueID()));
    }
    return data;
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
  public void fillWithLoot(PlayerEntity player, IInventory inventory, @Nullable ResourceLocation overrideTable, long seed) {
    if (this.world != null && this.savedLootTable != null && this.world.getServer() != null) {
      LootTable loottable = this.world.getServer().getLootTableManager().getLootTableFromLocation(overrideTable != null ? overrideTable : this.savedLootTable);
      if (player instanceof ServerPlayerEntity) {
        CriteriaTriggers.PLAYER_GENERATES_CONTAINER_LOOT.test((ServerPlayerEntity) player, overrideTable != null ? overrideTable : this.lootTable);
      }
      LootContext.Builder builder = (new LootContext.Builder((ServerWorld) this.world)).withParameter(LootParameters.field_237457_g_, Vector3d.copyCentered(this.pos)).withSeed(ConfigManager.RANDOMISE_SEED.get() ? ThreadLocalRandom.current().nextLong() : seed == Long.MIN_VALUE ? this.seed : seed);
      if (player != null) {
        builder.withLuck(player.getLuck()).withParameter(LootParameters.THIS_ENTITY, player);
      }

      loottable.fillInventory(inventory, builder.build(LootParameterSets.CHEST));
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

  @SuppressWarnings("Duplicates")
  @Override
  public void read(BlockState state, CompoundNBT compound) {
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
    if (compound.hasUniqueId("tileId")) {
      this.tileId = compound.getUniqueId("tileId");
    } else if (this.tileId == null) {
      getTileId();
    }
    if (compound.contains("LootrOpeners")) {
      ListNBT openers = compound.getList("LootrOpeners", Constants.NBT.TAG_INT_ARRAY);
      this.openers.clear();
      for (INBT item : openers) {
        this.openers.add(NBTUtil.readUniqueId(item));
      }
    }
    requestModelDataUpdate();
    super.read(state, compound);
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    compound = super.write(compound);
    if (savedLootTable != null) {
      compound.putString("specialLootBarrel_table", savedLootTable.toString());
      compound.putString("LootTable", savedLootTable.toString());
    }
    if (seed != -1) {
      compound.putLong("specialLootBarrel_seed", seed);
      compound.putLong("LootTableSeed", seed);
    }
    compound.putUniqueId("tileId", getTileId());
    ListNBT list = new ListNBT();
    for (UUID opener : this.openers) {
      list.add(NBTUtil.func_240626_a_(opener));
    }
    compound.put("LootrOpeners", list);
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
    Vector3i dir = state.get(BarrelBlock.PROPERTY_FACING).getDirectionVec();
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
      openers.add(player.getUniqueID());
      this.markDirty();
      updatePacketViaState();
    }
  }

  @Override
  public void updatePacketViaState() {
    if (world != null && !world.isRemote) {
      BlockState state = world.getBlockState(getPos());
      world.notifyBlockUpdate(getPos(), state, state, 8);
    }
  }


  @Override
  @Nonnull
  public CompoundNBT getUpdateTag() {
    return write(new CompoundNBT());
  }

  @Override
  @Nullable
  public SUpdateTileEntityPacket getUpdatePacket() {
    return new SUpdateTileEntityPacket(getPos(), 0, getUpdateTag());
  }

  @Override
  public void onDataPacket(@Nonnull NetworkManager net, @Nonnull SUpdateTileEntityPacket pkt) {
    read(ModBlocks.CHEST.getDefaultState(), pkt.getNbtCompound());
  }
}
