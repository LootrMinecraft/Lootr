package noobanidus.mods.lootr.block.entities;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.data.SpecialChestInventory;
import noobanidus.mods.lootr.init.ModBlockEntities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LootrChestBlockEntity extends ChestBlockEntity implements ILootBlockEntity {
  public Set<UUID> openers = new HashSet<>();
  protected ResourceLocation savedLootTable = null;
  protected long seed = -1;
  protected UUID tileId;
  protected boolean opened;

  private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
    @Override
    protected void onOpen(Level level, BlockPos pos, BlockState state) {
      LootrChestBlockEntity.playSound(level, pos, state, SoundEvents.CHEST_OPEN);
    }

    @Override
    protected void onClose(Level level, BlockPos pos, BlockState state) {
      LootrChestBlockEntity.playSound(level, pos, state, SoundEvents.CHEST_CLOSE);
    }

    @Override
    protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int p_155364_, int p_155365_) {
      LootrChestBlockEntity.this.signalOpenCount(level, pos, state, p_155364_, p_155365_);
    }

    @Override
    protected boolean isOwnContainer(Player player) {
      if ((player.containerMenu instanceof ChestMenu menu)) {
        if (menu.getContainer() instanceof SpecialChestInventory chest) {
          return LootrChestBlockEntity.this.getTileId().equals(chest.getTileId());
        }
      }

      return false;
    }
  };
  private final ChestLidController chestLidController = new ChestLidController();

  protected LootrChestBlockEntity(BlockEntityType<?> p_155327_, BlockPos p_155328_, BlockState p_155329_) {
    super(p_155327_, p_155328_, p_155329_);
  }

  // TODO
  public LootrChestBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
    this(ModBlockEntities.SPECIAL_LOOT_CHEST, pWorldPosition, pBlockState);
  }

  @Override
  public void setLootTable(ResourceLocation lootTableIn, long seedIn) {
    super.setLootTable(lootTableIn, seedIn);
    this.savedLootTable = lootTableIn;
    this.seed = seedIn;
  }

  @Override
  public void load(CompoundTag compound) {
    if (compound.contains("specialLootChest_table", Tag.TAG_STRING)) {
      savedLootTable = new ResourceLocation(compound.getString("specialLootChest_table"));
    }
    if (compound.contains("specialLootChest_seed", Tag.TAG_LONG)) {
      seed = compound.getLong("specialLootChest_seed");
    }
    if (savedLootTable == null && compound.contains("LootTable", Tag.TAG_STRING)) {
      savedLootTable = new ResourceLocation(compound.getString("LootTable"));
      if (seed == 0L && compound.contains("LootTableSeed", Tag.TAG_LONG)) {
        seed = compound.getLong("LootTableSeed");
      }
    }
    if (compound.hasUUID("tileId")) {
      this.tileId = compound.getUUID("tileId");
    }
    if (this.tileId == null) {
      getTileId();
    }
    if (compound.contains("LootrOpeners")) {
      ListTag openers = compound.getList("LootrOpeners", Tag.TAG_INT_ARRAY);
      this.openers.clear();
      for (Tag item : openers) {
        this.openers.add(NbtUtils.loadUUID(item));
      }
    }
    super.load(compound);
  }

  private boolean savingToItem = false;

  @Override
  public void saveToItem(ItemStack itemstack) {
    savingToItem = true;
    super.saveToItem(itemstack);
    savingToItem = false;
  }

  @Override
  protected void saveAdditional(CompoundTag compound) {
    super.saveAdditional(compound);
    if (savedLootTable != null) {
      compound.putString("specialLootChest_table", savedLootTable.toString());
      compound.putString("LootTable", savedLootTable.toString());
    }
    if (seed != -1) {
      compound.putLong("specialLootChest_seed", seed);
      compound.putLong("LootTableSeed", seed);
    }
    if (!LootrAPI.shouldDiscard() && !savingToItem) {
      compound.putUUID("tileId", getTileId());
      ListTag list = new ListTag();
      for (UUID opener : this.openers) {
        list.add(NbtUtils.createUUID(opener));
      }
      compound.put("LootrOpeners", list);
    }
  }

  public static <T extends BlockEntity> void lootrLidAnimateTick(Level pLevel, BlockPos pPos, BlockState pState, T pBlockEntity) {
    ((LootrChestBlockEntity) pBlockEntity).chestLidController.tickLid();
  }

  protected static void playSound(Level pLevel, BlockPos pPos, BlockState pState, SoundEvent pSound) {
    ChestType chesttype = pState.getValue(ChestBlock.TYPE);
    if (chesttype != ChestType.LEFT) {
      double d0 = (double) pPos.getX() + 0.5D;
      double d1 = (double) pPos.getY() + 0.5D;
      double d2 = (double) pPos.getZ() + 0.5D;
      if (chesttype == ChestType.RIGHT) {
        Direction direction = ChestBlock.getConnectedDirection(pState);
        d0 += (double) direction.getStepX() * 0.5D;
        d2 += (double) direction.getStepZ() * 0.5D;
      }

      pLevel.playSound(null, d0, d1, d2, pSound, SoundSource.BLOCKS, 0.5F, pLevel.random.nextFloat() * 0.1F + 0.9F);
    }
  }

  @Override
  public boolean triggerEvent(int pId, int pType) {
    if (pId == 1) {
      this.chestLidController.shouldBeOpen(pType > 0);
      return true;
    } else {
      return super.triggerEvent(pId, pType);
    }
  }

  @Override
  public void startOpen(Player pPlayer) {
    if (!this.remove && !pPlayer.isSpectator()) {
      this.openersCounter.incrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
    }

  }

  @Override
  public void stopOpen(Player pPlayer) {
    if (!this.remove && !pPlayer.isSpectator()) {
      this.openersCounter.decrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
    }
  }

  @Override
  public void recheckOpen() {
    if (!this.remove) {
      this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
    }
  }

  @Override
  public float getOpenNess(float pPartialTicks) {
    return this.chestLidController.getOpenness(pPartialTicks);
  }

  @Override
  public void updatePacketViaState() {
    if (level != null && !level.isClientSide) {
      BlockState state = level.getBlockState(getBlockPos());
      level.sendBlockUpdated(getBlockPos(), state, state, 8);
    }
  }

  public static int getOpenCount(BlockGetter pLevel, BlockPos pPos) {
    BlockState blockstate = pLevel.getBlockState(pPos);
    if (blockstate.hasBlockEntity()) {
      BlockEntity blockentity = pLevel.getBlockEntity(pPos);
      if (blockentity instanceof LootrChestBlockEntity) {
        return ((LootrChestBlockEntity) blockentity).openersCounter.getOpenerCount();
      }
    }

    return 0;
  }

  @Override
  @Nonnull
  public CompoundTag getUpdateTag() {
    CompoundTag result = super.getUpdateTag();
    saveAdditional(result);
    return result;
  }

  @Override
  @Nullable
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
  }

  @Override
  public void onDataPacket(@Nonnull Connection net, @Nonnull ClientboundBlockEntityDataPacket pkt) {
    if (pkt.getTag() != null) {
      load(pkt.getTag());
    }
  }

  @Override
  public void unpackLootTable(@Nullable Player player) {
  }

  @Override
  public void unpackLootTable(Player player, Container inventory, ResourceLocation overrideTable, long seed) {
    if (this.level != null && this.savedLootTable != null && this.level.getServer() != null) {
      LootTable loottable = this.level.getServer().getLootTables().get(overrideTable != null ? overrideTable : this.savedLootTable);
      if (loottable == LootTable.EMPTY) {
        LootrAPI.LOG.error("Unable to fill loot chest in " + level.dimension() + " at " + worldPosition + " as the loot table '" + (overrideTable != null ? overrideTable : this.savedLootTable) + "' couldn't be resolved! Please search the loot table in `latest.log` to see if there are errors in loading.");
        if (ConfigManager.REPORT_UNRESOLVED_TABLES.get()) {
          player.sendMessage(new TranslatableComponent("lootr.message.invalid_table", (overrideTable != null ? overrideTable : this.savedLootTable).toString()).setStyle(ConfigManager.DISABLE_MESSAGE_STYLES.get() ? Style.EMPTY : Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_RED)).withBold(true)), Util.NIL_UUID);
        }
      }
      if (player instanceof ServerPlayer) {
        CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer) player, overrideTable != null ? overrideTable : this.savedLootTable);
      }
      LootContext.Builder builder = (new LootContext.Builder((ServerLevel) this.level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition)).withOptionalRandomSeed(ConfigManager.RANDOMISE_SEED.get() ? ThreadLocalRandom.current().nextLong() : seed == Long.MIN_VALUE ? this.seed : seed);
      if (player != null) {
        builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
      }

      loottable.fill(inventory, builder.create(LootContextParamSets.CHEST));
    }
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    return LazyOptional.empty();
  }

  @Override
  public ResourceLocation getTable() {
    return savedLootTable;
  }

  @Override
  public BlockPos getPosition() {
    return getBlockPos();
  }

  @Override
  public long getSeed() {
    return seed;
  }

  @Override
  public Set<UUID> getOpeners() {
    return openers;
  }

  @Override
  public UUID getTileId() {
    if (this.tileId == null) {
      this.tileId = UUID.randomUUID();
    }
    return this.tileId;
  }

  public boolean isOpened() {
    return opened;
  }

  @Override
  public void setOpened(boolean opened) {
    this.opened = opened;
  }
}
