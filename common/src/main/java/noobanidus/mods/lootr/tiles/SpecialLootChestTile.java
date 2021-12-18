package noobanidus.mods.lootr.tiles;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
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

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

@SuppressWarnings({"Duplicates", "ConstantConditions", "NullableProblems", "WeakerAccess"})
public class SpecialLootChestTile extends ChestBlockEntity implements ILootTile {
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

  public SpecialLootChestTile(BlockEntityType<?> tile) {
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
  public void unpackLootTable(@Nullable Player player) {
  }

  @Override
  public void fillWithLoot(Player player, Container inventory, @Nullable ResourceLocation overrideTable, long seed) {
    if (this.level != null && this.savedLootTable != null && this.level.getServer() != null) {
      LootTable loottable = this.level.getServer().getLootTables().get(overrideTable != null ? overrideTable : this.savedLootTable);
      if (player instanceof ServerPlayer) {
        CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer) player, overrideTable != null ? overrideTable : this.lootTable);
      }
      LootContext.Builder builder = (new LootContext.Builder((ServerLevel) this.level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition)).withOptionalRandomSeed(ConfigManager.RANDOMISE_SEED.get() ? ThreadLocalRandom.current().nextLong() : seed == Long.MIN_VALUE ? this.seed : seed);
      if (player != null) {
        builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
      }

      loottable.fill(inventory, builder.create(LootContextParamSets.CHEST));
    }
  }

  @Override
  public void load(BlockState state, CompoundTag compound) {
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
      ListTag openers = compound.getList("LootrOpeners", Constants.NBT.TAG_INT_ARRAY);
      this.openers.clear();
      for (Tag item : openers) {
        this.openers.add(NbtUtils.loadUUID(item));
      }
    }
    super.load(state, compound);
  }

  @Override
  public CompoundTag save(CompoundTag compound) {
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
    ListTag list = new ListTag();
    for (UUID opener : this.openers) {
      list.add(NbtUtils.createUUID(opener));
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
    this.level.playSound(null, getBlockPos(), soundIn, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
  }

  public static int calculatePlayersUsingSync(Level world, BaseContainerBlockEntity tile, int ticksSinceSync, int x, int y, int z, int numPlayersUsing) {
    if (!world.isClientSide && numPlayersUsing != 0 && (ticksSinceSync + x + y + z) % 200 == 0) {
      numPlayersUsing = calculatePlayersUsing(world, tile, x, y, z);
    }

    return numPlayersUsing;
  }

  public static int calculatePlayersUsing(Level world, BaseContainerBlockEntity tile, int x, int y, int z) {
    if (tile == null) {
      return 0;
    }
    int i = 0;

    for (Player playerentity : world.getEntitiesOfClass(Player.class, new AABB(x - 5.0, y - 5.0, z - 5.0, (x + 1) + 5.0, (y + 1) + 5.0, (z + 1) + 5.0))) {
      if (playerentity.containerMenu instanceof ChestMenu) {
        Container inv = ((ChestMenu) playerentity.containerMenu).getContainer();
        if (inv == tile || (inv instanceof SpecialChestInventory && ((SpecialChestInventory) inv).getPos().equals(tile.getBlockPos()))) {
          ++i;
        }
      }
    }

    return i;
  }

  @Override
  public void startOpen(Player player) {
    if (!player.isSpectator()) {
      if (this.specialNumPlayersUsingChest < 0) {
        this.specialNumPlayersUsingChest = 0;
      }

      ++this.specialNumPlayersUsingChest;
      this.signalOpenCount();
    }
  }

  @Override
  public void stopOpen(Player player) {
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
  public CompoundTag getUpdateTag() {
    return save(new CompoundTag());
  }

  @Override
  @Nullable
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return new ClientboundBlockEntityDataPacket(getBlockPos(), 0, getUpdateTag());
  }

  @Override
  public void onDataPacket(@Nonnull Connection net, @Nonnull ClientboundBlockEntityDataPacket pkt) {
    load(ModBlocks.CHEST.defaultBlockState(), pkt.getTag());
  }

  public static int getPlayersUsing(BlockGetter reader, BlockPos posIn) {
    BlockState blockstate = reader.getBlockState(posIn);
    if (blockstate.hasTileEntity()) {
      BlockEntity tileentity = reader.getBlockEntity(posIn);
      if (tileentity instanceof SpecialLootChestTile) {
        return ((SpecialLootChestTile) tileentity).specialNumPlayersUsingChest;
      }
    }

    return 0;
  }
}
