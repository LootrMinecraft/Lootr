package noobanidus.mods.lootr.common.block.entity;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.*;
import noobanidus.mods.lootr.common.api.advancement.IContainerTrigger;
import noobanidus.mods.lootr.common.api.data.ILootrInfo;
import noobanidus.mods.lootr.common.api.data.LootrBlockType;
import noobanidus.mods.lootr.common.api.data.SimpleLootrEntity;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class LootrShulkerBlockEntity extends RandomizableContainerBlockEntity implements ILootrBlockEntity {
  protected final SimpleLootrEntity simpleLootrEntity = new SimpleLootrEntity(this::getVisualOpeners, 27);

  private int openCount;
  private ShulkerBoxBlockEntity.AnimationStatus animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
  private float progress;
  private float progressOld;

  public LootrShulkerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
    super(LootrRegistry.getShulkerBlockEntity(), pWorldPosition, pBlockState);
  }

  @Override
  public void defaultTick(Level level, BlockPos pos, BlockState state) {
    ILootrBlockEntity.super.defaultTick(level, pos, state);
    this.updateAnimation(level, pos, state);
  }

  private void updateAnimation(Level pLevel, BlockPos pPos, BlockState pState) {
    this.progressOld = this.progress;
    switch (this.animationStatus) {
      case CLOSED -> this.progress = 0.0F;
      case OPENING -> {
        this.progress += 0.1F;
        if (this.progressOld == 0.0F) {
          doNeighborUpdates(pLevel, pPos, pState);
        }

        if (this.progress >= 1.0F) {
          this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.OPENED;
          this.progress = 1.0F;
          doNeighborUpdates(pLevel, pPos, pState);
        }

        this.moveCollidedEntities(pLevel, pPos, pState);
      }
      case OPENED -> this.progress = 1.0F;
      case CLOSING -> {
        this.progress -= 0.1F;
        if (this.progressOld == 1.0F) {
          doNeighborUpdates(pLevel, pPos, pState);
        }

        if (this.progress <= 0.0F) {
          this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
          this.progress = 0.0F;
          doNeighborUpdates(pLevel, pPos, pState);
        }
      }
    }
  }

  public ShulkerBoxBlockEntity.AnimationStatus getAnimationStatus() {
    return this.animationStatus;
  }

  public AABB getBoundingBox(BlockState pState) {
    return Shulker.getProgressAabb(1.0F, pState.getValue(ShulkerBoxBlock.FACING), 0.5F * this.getProgress(1.0F));
  }

  private void moveCollidedEntities(Level pLevel, BlockPos pPos, BlockState pState) {
    if (pState.getBlock() instanceof ShulkerBoxBlock) {
      Direction direction = pState.getValue(ShulkerBoxBlock.FACING);
      AABB aabb = Shulker.getProgressDeltaAabb(1.0F, direction, this.progressOld, this.progress).move(pPos);
      List<Entity> list = pLevel.getEntities(null, aabb);
      for (Entity entity : list) {
        if (entity.getPistonPushReaction() != PushReaction.IGNORE) {
          entity.move(
              MoverType.SHULKER_BOX,
              new Vec3(
                  (aabb.getXsize() + 0.01) * (double) direction.getStepX(),
                  (aabb.getYsize() + 0.01) * (double) direction.getStepY(),
                  (aabb.getZsize() + 0.01) * (double) direction.getStepZ()
              )
          );
        }
      }
    }
  }

  @Override
  public int getContainerSize() {
    return this.simpleLootrEntity.getInfoContainerSize();
  }

  @Override
  public boolean triggerEvent(int pEvent, int pCount) {
    if (pEvent == 1) {
      this.openCount = pCount;
      if (pCount == 0) {
        this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSING;
      }

      if (pCount == 1) {
        this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.OPENING;
      }

      return true;
    } else {
      return super.triggerEvent(pEvent, pCount);
    }
  }

  @Override
  public void startOpen(Player pPlayer) {
    if (!this.remove && !pPlayer.isSpectator()) {
      if (!this.simpleLootrEntity.hasBeenOpened()) {
        this.simpleLootrEntity.setHasBeenOpened();
        markChanged();
      }


      if (this.openCount < 0) {
        this.openCount = 0;
      }

      this.openCount++;
      this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
      if (this.openCount == 1) {
        this.level.gameEvent(pPlayer, GameEvent.CONTAINER_OPEN, this.worldPosition);
        this.level.playSound(null, this.worldPosition, SoundEvents.SHULKER_BOX_OPEN, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
      }
    }
  }

  @Override
  public void stopOpen(Player pPlayer) {
    if (!this.remove && !pPlayer.isSpectator()) {
      this.openCount--;
      this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
      if (this.openCount <= 0) {
        this.level.gameEvent(pPlayer, GameEvent.CONTAINER_CLOSE, this.worldPosition);
        this.level.playSound(null, this.worldPosition, SoundEvents.SHULKER_BOX_CLOSE, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
      }
    }
  }

  @Override
  protected Component getDefaultName() {
    return Component.translatable("container.shulkerBox");
  }

  @Override
  protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
    return null;
  }

  @Override
  public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
    super.loadAdditional(compound, provider);
    this.tryLoadLootTable(compound);
    this.simpleLootrEntity.loadAdditional(compound, provider);
  }

  @Override
  public void saveToItem(ItemStack itemstack, HolderLookup.Provider provider) {
    this.simpleLootrEntity.setSavingToItem(true);
    super.saveToItem(itemstack, provider);
    this.simpleLootrEntity.setSavingToItem(false);
  }

  @Override
  protected void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
    super.saveAdditional(compound, provider);
    this.trySaveLootTable(compound);
    this.simpleLootrEntity.saveAdditional(compound, provider, level != null && level.isClientSide());
  }

  @Override
  protected NonNullList<ItemStack> getItems() {
    return this.simpleLootrEntity.getItems();
  }

  @Override
  protected void setItems(NonNullList<ItemStack> pItems) {
  }

  public float getProgress(float pPartialTicks) {
    return Mth.lerp(pPartialTicks, this.progressOld, this.progress);
  }

  public boolean isClosed() {
    return this.animationStatus == ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
  }

  @Override
  public @Nullable Set<UUID> getClientOpeners() {
    return this.simpleLootrEntity.getClientOpeners();
  }

  @Override
  @Deprecated
  public LootrBlockType getInfoBlockType() {
    return LootrBlockType.SHULKER;
  }

  @Override
  public ILootrType getInfoNewType() {
    return BuiltInLootrTypes.SHULKER;
  }

  @Override
  @NotNull
  public UUID getInfoUUID() {
    return this.simpleLootrEntity.getInfoUUID();
  }

  @Override
  public String getInfoKey() {
    return this.simpleLootrEntity.getInfoKey();
  }

  @Override
  public boolean hasBeenOpened() {
    return this.simpleLootrEntity.hasBeenOpened();
  }

  @Override
  public boolean isPhysicallyOpen() {
    return !isClosed();
  }

  @Override
  @NotNull
  public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
    CompoundTag result = super.getUpdateTag(provider);
    this.simpleLootrEntity.fillUpdateTag(result, provider, level != null && level.isClientSide());
    return result;
  }

  @Override
  @Nullable
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
  }

  @Override
  public void unpackLootTable(@Nullable Player player) {
  }

  @Override
  public void markChanged() {
    setChanged();
    markDataChanged();
  }

  @Override
  public boolean isClientOpened() {
    return this.simpleLootrEntity.isClientOpened();
  }

  @Override
  public void setClientOpened(boolean opened) {
    this.simpleLootrEntity.setClientOpened(opened);
  }

  @Override
  public @NotNull BlockPos getInfoPos() {
    return getBlockPos();
  }

  @Override
  public ResourceKey<LootTable> getInfoLootTable() {
    return getLootTable();
  }

  @Override
  public @Nullable Component getInfoDisplayName() {
    return getDisplayName();
  }

  @Override
  public @NotNull ResourceKey<Level> getInfoDimension() {
    return getLevel().dimension();
  }

  @Override
  public int getInfoContainerSize() {
    return getContainerSize();
  }

  @Override
  public long getInfoLootSeed() {
    return getLootTableSeed();
  }

  @Override
  public @Nullable NonNullList<ItemStack> getInfoReferenceInventory() {
    return null;
  }

  @Override
  public boolean isInfoReferenceInventory() {
    return false;
  }

  @Override
  public Level getInfoLevel() {
    return getLevel();
  }

  @Override
  public int getPhysicalOpenerCount() {
    return openCount;
  }

  @Override
  public @Nullable IContainerTrigger getTrigger() {
    return LootrRegistry.getShulkerTrigger();
  }


  private static void doNeighborUpdates(Level pLevel, BlockPos pPos, BlockState pState) {
    pState.updateNeighbourShapes(pLevel, pPos, 3);
    pLevel.updateNeighborsAt(pPos, pState.getBlock());
  }

  public static class DefaultBlockEntityConverter implements ILootrBlockEntityConverter<LootrShulkerBlockEntity> {
    @Override
    public ILootrBlockEntity apply(LootrShulkerBlockEntity blockEntity) {
      return blockEntity;
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
      return LootrRegistry.getShulkerBlockEntity();
    }
  }
}
