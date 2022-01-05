package noobanidus.mods.lootr.blocks.entities;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.blockentity.ILootTile;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModBlockEntities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class LootrShulkerBlockEntity extends RandomizableContainerBlockEntity implements ILootTile {
  public Set<UUID> openers = new HashSet<>();
  protected ResourceLocation savedLootTable = null;
  protected long seed = -1;
  protected UUID tileId;
  protected boolean opened;
  private NonNullList<ItemStack> itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
  private int openCount;
  private ShulkerBoxBlockEntity.AnimationStatus animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
  private float progress;
  private float progressOld;
  @Nullable
  private final DyeColor color;

  public LootrShulkerBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
    super(pType, pWorldPosition, pBlockState);
    color = DyeColor.YELLOW;
  }

  public LootrShulkerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
    this(ModBlockEntities.SPECIAL_LOOT_SHULKER, pWorldPosition, pBlockState);
  }

  public static void tick(Level pLevel, BlockPos pPos, BlockState pState, LootrShulkerBlockEntity pBlockEntity) {
    pBlockEntity.updateAnimation(pLevel, pPos, pState);
  }

  private void updateAnimation(Level pLevel, BlockPos pPos, BlockState pState) {
    this.progressOld = this.progress;
    switch (this.animationStatus) {
      case CLOSED:
        this.progress = 0.0F;
        break;
      case OPENING:
        this.progress += 0.1F;
        if (this.progress >= 1.0F) {
          this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.OPENED;
          this.progress = 1.0F;
          doNeighborUpdates(pLevel, pPos, pState);
        }

        this.moveCollidedEntities(pLevel, pPos, pState);
        break;
      case CLOSING:
        this.progress -= 0.1F;
        if (this.progress <= 0.0F) {
          this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
          this.progress = 0.0F;
          doNeighborUpdates(pLevel, pPos, pState);
        }
        break;
      case OPENED:
        this.progress = 1.0F;
    }

  }

  public ShulkerBoxBlockEntity.AnimationStatus getAnimationStatus() {
    return this.animationStatus;
  }

  public AABB getBoundingBox(BlockState pState) {
    return Shulker.getProgressAabb(pState.getValue(ShulkerBoxBlock.FACING), 0.5F * this.getProgress(1.0F));
  }

  private void moveCollidedEntities(Level pLevel, BlockPos pPos, BlockState pState) {
    if (pState.getBlock() instanceof ShulkerBoxBlock) {
      Direction direction = pState.getValue(ShulkerBoxBlock.FACING);
      AABB aabb = Shulker.getProgressDeltaAabb(direction, this.progressOld, this.progress).move(pPos);
      List<Entity> list = pLevel.getEntities(null, aabb);
      if (!list.isEmpty()) {
        for (int i = 0; i < list.size(); ++i) {
          Entity entity = list.get(i);
          if (entity.getPistonPushReaction() != PushReaction.IGNORE) {
            entity.move(MoverType.SHULKER_BOX, new Vec3((aabb.getXsize() + 0.01D) * (double) direction.getStepX(), (aabb.getYsize() + 0.01D) * (double) direction.getStepY(), (aabb.getZsize() + 0.01D) * (double) direction.getStepZ()));
          }
        }

      }
    }
  }

  /**
   * Returns the number of slots in the inventory.
   */
  @Override
  public int getContainerSize() {
    return this.itemStacks.size();
  }

  @Override
  public boolean triggerEvent(int pId, int pType) {
    if (pId == 1) {
      this.openCount = pType;
      if (pType == 0) {
        this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSING;
        doNeighborUpdates(this.getLevel(), this.worldPosition, this.getBlockState());
      }

      if (pType == 1) {
        this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.OPENING;
        doNeighborUpdates(this.getLevel(), this.worldPosition, this.getBlockState());
      }

      return true;
    } else {
      return super.triggerEvent(pId, pType);
    }
  }

  private static void doNeighborUpdates(Level pLevel, BlockPos pPos, BlockState pState) {
    pState.updateNeighbourShapes(pLevel, pPos, 3);
  }

  @Override
  public void startOpen(Player pPlayer) {
    if (!pPlayer.isSpectator()) {
      if (this.openCount < 0) {
        this.openCount = 0;
      }

      ++this.openCount;
      this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
      if (this.openCount == 1) {
        this.level.gameEvent(pPlayer, GameEvent.CONTAINER_OPEN, this.worldPosition);
        this.level.playSound(null, this.worldPosition, SoundEvents.SHULKER_BOX_OPEN, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
      }
    }
  }

  @Override
  public void stopOpen(Player pPlayer) {
    if (!pPlayer.isSpectator()) {
      --this.openCount;
      this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
      if (this.openCount <= 0) {
        this.level.gameEvent(pPlayer, GameEvent.CONTAINER_CLOSE, this.worldPosition);
        this.level.playSound(null, this.worldPosition, SoundEvents.SHULKER_BOX_CLOSE, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
      }
      openers.add(pPlayer.getUUID());
      updatePacketViaState();
    }
  }

  @Override
  protected Component getDefaultName() {
    return new TranslatableComponent("container.shulkerBox");
  }

  @Override
  protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
    return null;
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
    } else if (this.tileId == null) {
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
    compound.putUUID("tileId", getTileId());
    ListTag list = new ListTag();
    for (UUID opener : this.openers) {
      list.add(NbtUtils.createUUID(opener));
    }
    compound.put("LootrOpeners", list);
  }

  @Override
  protected NonNullList<ItemStack> getItems() {
    return this.itemStacks;
  }

  @Override
  protected void setItems(NonNullList<ItemStack> pItems) {
    this.itemStacks = pItems;
  }

  public float getProgress(float pPartialTicks) {
    return Mth.lerp(pPartialTicks, this.progressOld, this.progress);
  }

  public boolean isClosed() {
    return this.animationStatus == ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
  }

  @Override
  public ResourceLocation getTable() {
    return savedLootTable;
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    return LazyOptional.empty();
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

  @Override
  public void updatePacketViaState() {
    if (level != null && !level.isClientSide) {
      BlockState state = level.getBlockState(getBlockPos());
      level.sendBlockUpdated(getBlockPos(), state, state, 8);
    }
  }

  @Override
  public void setOpened(boolean opened) {
    this.opened = true;
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
        Lootr.LOG.error("Unable to fill loot shulker in " + level.dimension() + " at " + worldPosition + " as the loot table '" + (overrideTable != null ? overrideTable : this.savedLootTable) + "' couldn't be resolved! Please search the loot table in `latest.log` to see if there are errors in loading.");
        if (ConfigManager.REPORT_UNRESOLVED_TABLES.get()) {
          player.sendMessage(new TranslatableComponent("lootr.message.invalid_table", (overrideTable != null ? overrideTable : this.savedLootTable).toString()).setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_RED)).withBold(true)), Util.NIL_UUID);
        }
      }
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
  public void setLootTable(ResourceLocation lootTableIn, long seedIn) {
    super.setLootTable(lootTableIn, seedIn);
    this.savedLootTable = lootTableIn;
    this.seed = seedIn;
  }
}
