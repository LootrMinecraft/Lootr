package noobanidus.mods.lootr.common.block.entity;

import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ContainerUser;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.BuiltInLootrTypes;
import noobanidus.mods.lootr.common.api.NBTConstants;
import noobanidus.mods.lootr.common.api.interfaces.wrapper.ILootrBlockEntityWrapper;
import noobanidus.mods.lootr.common.api.interfaces.type.ILootrType;
import noobanidus.mods.lootr.common.api.interfaces.advancement.IContainerTrigger;
import noobanidus.mods.lootr.common.api.helper.SimpleLootrInstance;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class LootrShulkerBlockEntity extends RandomizableContainerBlockEntity implements ILootrBlockEntity {
  protected final SimpleLootrInstance simpleLootrInstance = new SimpleLootrInstance(this::getVisualOpeners, 27);

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
    Vec3 vec3 = new Vec3(0.5, 0.0, 0.5);
    return Shulker.getProgressAabb(1.0F, pState.getValue(ShulkerBoxBlock.FACING), 0.5F * this.getProgress(1.0F), vec3);
  }

  private void moveCollidedEntities(Level pLevel, BlockPos pPos, BlockState pState) {
    if (pState.getBlock() instanceof ShulkerBoxBlock) {
      Direction direction = pState.getValue(ShulkerBoxBlock.FACING);
      AABB aabb = Shulker.getProgressDeltaAabb(1.0F, direction, this.progressOld, this.progress, pPos.getBottomCenter());
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
    return this.simpleLootrInstance.getContainerSize();
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
  public void startOpen(@NonNull ContainerUser user) {
    if (user instanceof ServerPlayer pPlayer) {
      if (!this.remove && !pPlayer.isSpectator()) {
        if (!this.simpleLootrInstance.hasBeenOpened()) {
          this.simpleLootrInstance.setHasBeenOpened();
          markInstanceChanged();
        }


        if (this.openCount < 0) {
          this.openCount = 0;
        }

        this.openCount++;
        this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
        if (this.openCount == 1) {
          this.level.gameEvent(pPlayer, GameEvent.CONTAINER_OPEN, this.worldPosition);
          this.level.playSound(null, this.worldPosition, SoundEvents.SHULKER_BOX_OPEN, SoundSource.BLOCKS, 0.5F, this.level.getRandom().nextFloat() * 0.1F + 0.9F);
        }
      }
    }
  }

  @Override
  public void stopOpen(@NonNull ContainerUser user) {
    if (user instanceof ServerPlayer pPlayer) {
      if (!this.remove && !pPlayer.isSpectator()) {
        this.openCount--;
        this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
        if (this.openCount <= 0) {
          this.level.gameEvent(pPlayer, GameEvent.CONTAINER_CLOSE, this.worldPosition);
          this.level.playSound(null, this.worldPosition, SoundEvents.SHULKER_BOX_CLOSE, SoundSource.BLOCKS, 0.5F, this.level.getRandom().nextFloat() * 0.1F + 0.9F);
        }
      }
    }
  }

  @Override
  protected @NonNull Component getDefaultName() {
    return Component.translatable("container.shulkerBox");
  }

  @Override
  protected @NonNull AbstractContainerMenu createMenu(int pContainerId, @NonNull Inventory pInventory) {
    return null;
  }

  @Override
  public void loadAdditional(@NonNull ValueInput input) {
    super.loadAdditional(input);
    this.tryLoadLootTable(input);
    this.simpleLootrInstance.loadAdditional(input);
  }

  @Override
  public void removeComponentsFromTag(@NonNull ValueOutput output) {
    super.removeComponentsFromTag(output);
    output.discard(NBTConstants.INSTANCE_ID);
  }

  @Override
  protected void saveAdditional(@NonNull ValueOutput output) {
    super.saveAdditional(output);
    this.trySaveLootTable(output);
    this.simpleLootrInstance.saveAdditional(output, level != null && level.isClientSide());
  }

  @Override
  protected @NonNull NonNullList<ItemStack> getItems() {
    return this.simpleLootrInstance.getEmptyInventory();
  }

  @Override
  protected void setItems(@NonNull NonNullList<ItemStack> pItems) {
  }

  public float getProgress(float pPartialTicks) {
    return Mth.lerp(pPartialTicks, this.progressOld, this.progress);
  }

  public boolean isClosed() {
    return this.animationStatus == ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
  }

  @Override
  public @Nullable Set<UUID> getClientOpeners() {
    return this.simpleLootrInstance.getClientOpeners();
  }

  @Override
  public @NonNull ILootrType getDataType() {
    return BuiltInLootrTypes.SHULKER_BOX;
  }

  @Override
  @NotNull
  public UUID getDataId() {
    return this.simpleLootrInstance.getId();
  }

  @Override
  public Identifier getDataIdentifier() {
    return this.simpleLootrInstance.getIdentifier();
  }

  @Override
  public boolean hasBeenOpened() {
    return this.simpleLootrInstance.hasBeenOpened();
  }

  @Override
  public boolean isPhysicallyOpen() {
    return !isClosed();
  }

  @Override
  @NotNull
  public CompoundTag getUpdateTag(HolderLookup.@NonNull Provider provider) {
    CompoundTag result = super.getUpdateTag(provider);
    result.merge(this.simpleLootrInstance.fillUpdateTag(provider, level != null && level.isClientSide(), this));
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
  public void markInstanceChanged() {
    setChanged();
    markSectionChanged();
  }

  @Override
  public boolean isClientOpened() {
    return this.simpleLootrInstance.isClientOpened();
  }

  @Override
  public void setClientOpened(boolean opened) {
    this.simpleLootrInstance.setClientOpened(opened);
  }

  @Override
  public @NotNull BlockPos getDataPos() {
    return getBlockPos();
  }

  @Override
  public ResourceKey<LootTable> getDataLootTable() {
    return getLootTable();
  }

  @Override
  public @Nullable Component getDataDisplayName() {
    return getDisplayName();
  }

  @Override
  public @NotNull ResourceKey<Level> getDataDimension() {
    return getLevel().dimension();
  }

  @Override
  public int getDataContainerSize() {
    return getContainerSize();
  }

  @Override
  public long getDataLootSeed() {
    return getLootTableSeed();
  }

  @Override
  public @Nullable NonNullList<ItemStack> getDataReferenceInventory() {
    return simpleLootrInstance.getReferenceInventory();
  }

  @Override
  public boolean isDataReferenceInventory() {
    return simpleLootrInstance.isReferenceInventory();
  }

  @Override
  public Level getDataLevel() {
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

  @Override
  public double getParticleYOffset() {
    return 1.1;
  }

  private static void doNeighborUpdates(Level pLevel, BlockPos pPos, BlockState pState) {
    pState.updateNeighbourShapes(pLevel, pPos, 3);
    pLevel.updateNeighborsAt(pPos, pState.getBlock());
  }

  @AutoService(ILootrBlockEntityWrapper.class)
  public static class DefaultBlockEntityWrapper implements ILootrBlockEntityWrapper<LootrShulkerBlockEntity> {
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
