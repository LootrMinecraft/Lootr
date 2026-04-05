package noobanidus.mods.lootr.common.block.entity;

import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.BuiltInLootrTypes;
import noobanidus.mods.lootr.common.api.interfaces.wrapper.ILootrBlockEntityWrapper;
import noobanidus.mods.lootr.common.api.interfaces.type.ILootrType;
import noobanidus.mods.lootr.common.api.interfaces.advancement.IContainerTrigger;
import noobanidus.mods.lootr.common.api.helper.SimpleLootrInstance;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.interfaces.inventory.ILootrInventory;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Set;
import java.util.UUID;

public class LootrBarrelBlockEntity extends RandomizableContainerBlockEntity implements ILootrBlockEntity {
  protected final SimpleLootrInstance simpleLootrInstance = new SimpleLootrInstance(this::getVisualOpeners, 27);

  private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
    @Override
    protected void onOpen(@NonNull Level level, @NonNull BlockPos pos, @NonNull BlockState state) {
      if (!LootrBarrelBlockEntity.this.hasBeenOpened()) {
        LootrBarrelBlockEntity.this.simpleLootrInstance.setHasBeenOpened();
        LootrBarrelBlockEntity.this.markInstanceChanged();
      }
      LootrBarrelBlockEntity.this.playSound(state, SoundEvents.BARREL_OPEN);
      LootrBarrelBlockEntity.this.updateBlockState(state, true);
    }

    @Override
    protected void onClose(@NonNull Level level, @NonNull BlockPos pos, @NonNull BlockState state) {
      LootrBarrelBlockEntity.this.playSound(state, SoundEvents.BARREL_CLOSE);
      LootrBarrelBlockEntity.this.updateBlockState(state, false);
    }

    @Override
    protected void openerCountChanged(@NonNull Level level, @NonNull BlockPos pos, @NonNull BlockState state, int p_155069_, int p_155070_) {
    }

    @Override
    public boolean isOwnContainer(Player player) {
      if (player.containerMenu instanceof ChestMenu chestMenu && chestMenu.getContainer() instanceof ILootrInventory data) {
        return data.getData().getDataId().equals(LootrBarrelBlockEntity.this.getDataId());
      }
      return false;
    }
  };

  public LootrBarrelBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
    super(LootrRegistry.getBarrelBlockEntity(), pWorldPosition, pBlockState);
  }

  @Override
  public @NonNull ILootrType getDataType() {
    return BuiltInLootrTypes.BARREL;
  }

  @Override
  @NotNull
  public UUID getDataId() {
    return this.simpleLootrInstance.getId();
  }

  @Override
  public int getDataKey() {
    return this.simpleLootrInstance.getKey();
  }

  @Override
  public Identifier getDataIdentifier() {
    return this.simpleLootrInstance.getIdentifier();
  }

  @Override
  public boolean isPhysicallyOpen() {
    return getBlockState().hasProperty(BarrelBlock.OPEN) && getBlockState().getValue(BarrelBlock.OPEN);
  }

  @Override
  protected @NonNull NonNullList<ItemStack> getItems() {
    return this.simpleLootrInstance.getEmptyInventory();
  }

  @Override
  protected void setItems(@NonNull NonNullList<ItemStack> pItems) {
  }

  @Override
  public void unpackLootTable(@Nullable Player player) {
  }

  @Override
  public void removeComponentsFromTag(@NonNull ValueOutput output) {
    super.removeComponentsFromTag(output);
    output.discard("LootrId");
  }

  @SuppressWarnings("Duplicates")
  @Override
  public void loadAdditional(@NonNull ValueInput input) {
    super.loadAdditional(input);
    this.tryLoadLootTable(input);
    this.simpleLootrInstance.loadAdditional(input);
  }

  @Override
  protected void saveAdditional(@NonNull ValueOutput output) {
    super.saveAdditional(output);
    this.trySaveLootTable(output);
    this.simpleLootrInstance.saveAdditional(output, level != null && level.isClientSide());
  }

  @Override
  protected @NonNull Component getDefaultName() {
    return Component.translatable("container.barrel");
  }

  @Override
  protected @NonNull AbstractContainerMenu createMenu(int pContainerId, @NonNull Inventory pInventory) {
    return null;
  }

  @Override
  public int getContainerSize() {
    return this.simpleLootrInstance.getContainerSize();
  }

  @Override
  public void startOpen(@NonNull ContainerUser user) {
    if (user instanceof ServerPlayer pPlayer) {
      if (!this.remove && !pPlayer.isSpectator()) {
        this.openersCounter.incrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState(), user.getContainerInteractionRange());
      }
    }
  }

  @Override
  public void stopOpen(@NonNull ContainerUser user) {
    if (user instanceof ServerPlayer pPlayer) {
      if (!this.remove && !pPlayer.isSpectator()) {
        this.openersCounter.decrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
      }
    }
  }

  public void recheckOpen() {
    if (!this.remove) {
      this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
    }
  }

  protected void updateBlockState(BlockState pState, boolean pOpen) {
    this.level.setBlock(this.getBlockPos(), pState.setValue(BarrelBlock.OPEN, pOpen), 3);
  }

  protected void playSound(BlockState pState, SoundEvent pSound) {
    Vec3i vec3i = pState.getValue(BarrelBlock.FACING).getUnitVec3i();
    double d0 = (double) this.worldPosition.getX() + 0.5D + (double) vec3i.getX() / 2.0D;
    double d1 = (double) this.worldPosition.getY() + 0.5D + (double) vec3i.getY() / 2.0D;
    double d2 = (double) this.worldPosition.getZ() + 0.5D + (double) vec3i.getZ() / 2.0D;
    this.level.playSound(null, d0, d1, d2, pSound, SoundSource.BLOCKS, 0.5F, this.level.getRandom().nextFloat() * 0.1F + 0.9F);
  }

  @Override
  public void markInstanceChanged() {
    setChanged();
    markSectionChanged();
  }

  @Override
  public boolean hasBeenOpened() {
    return this.simpleLootrInstance.hasBeenOpened();
  }

  @Override
  public @Nullable Set<UUID> getClientOpeners() {
    return this.simpleLootrInstance.getClientOpeners();
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
  public @Nullable IContainerTrigger getTrigger() {
    return LootrRegistry.getBarrelTrigger();
  }

  @Override
  public int getPhysicalOpenerCount() {
    return openersCounter.getOpenerCount();
  }

  @Override
  public double getParticleYOffset() {
    return 1.1;
  }

  @AutoService(ILootrBlockEntityWrapper.class)
  public static class DefaultBlockEntityWrapper implements ILootrBlockEntityWrapper<LootrBarrelBlockEntity> {
    @Override
    public ILootrBlockEntity apply(LootrBarrelBlockEntity blockEntity) {
      return blockEntity;
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
      return LootrRegistry.getBarrelBlockEntity();
    }
  }
}
