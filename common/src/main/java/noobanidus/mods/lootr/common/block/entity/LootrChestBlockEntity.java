package noobanidus.mods.lootr.common.block.entity;

import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.type.BuiltInLootrTypes;
import noobanidus.mods.lootr.common.api.wrapper.ILootrBlockEntityWrapper;
import noobanidus.mods.lootr.common.api.type.ILootrType;
import noobanidus.mods.lootr.common.api.advancement.IContainerTrigger;
import noobanidus.mods.lootr.common.api.helper.SimpleLootrInstance;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.data.LootrInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Set;
import java.util.UUID;

public class LootrChestBlockEntity extends ChestBlockEntity implements ILootrBlockEntity {
  protected final SimpleLootrInstance simpleLootrInstance = new SimpleLootrInstance(this::getVisualOpeners, 27);

  private final ChestLidController chestLidController = new ChestLidController();
  private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
    @Override
    protected void onOpen(Level level, BlockPos pos, BlockState state) {
      if (!LootrChestBlockEntity.this.hasBeenOpened()) {
        LootrChestBlockEntity.this.simpleLootrInstance.setHasBeenOpened();
        LootrChestBlockEntity.this.markChanged();
      }
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
    public boolean isOwnContainer(Player player) {
      if ((player.containerMenu instanceof ChestMenu menu)) {
        if (menu.getContainer() instanceof LootrInventory data) {
          return LootrChestBlockEntity.this.getDataId().equals(data.getInventoryStore().getData().getDataId());
        }
      }

      return false;
    }
  };

  protected LootrChestBlockEntity(BlockEntityType<?> p_155327_, BlockPos p_155328_, BlockState p_155329_) {
    super(p_155327_, p_155328_, p_155329_);
  }

  public LootrChestBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
    this(LootrRegistry.getChestBlockEntity(), pWorldPosition, pBlockState);
  }

  @Override
  public void defaultTick(Level level, BlockPos pos, BlockState state) {
    ILootrBlockEntity.super.defaultTick(level, pos, state);
    chestLidController.tickLid();
  }

  @Override
  public void loadAdditional(ValueInput input) {
    super.loadAdditional(input);
    this.tryLoadLootTable(input);
    this.simpleLootrInstance.loadAdditional(input);
  }

  @Override
  public void removeComponentsFromTag(ValueOutput output) {
    super.removeComponentsFromTag(output);
    output.discard("LootrId");
  }

  @Override
  protected void saveAdditional(ValueOutput output) {
    super.saveAdditional(output);
    this.trySaveLootTable(output);
    this.simpleLootrInstance.saveAdditional(output, level != null && level.isClientSide());
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
  public void startOpen(ContainerUser user) {
    if (user instanceof ServerPlayer pPlayer) {
      if (!this.remove && !pPlayer.isSpectator()) {
        this.openersCounter.incrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState(), user.getContainerInteractionRange());
      }
    }
  }

  @Override
  public void stopOpen(ContainerUser user) {
    if (user instanceof ServerPlayer pPlayer) {
      if (!this.remove && !pPlayer.isSpectator()) {
        this.openersCounter.decrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
      }
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
  @NotNull
  public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
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
  public @Nullable Set<UUID> getClientOpeners() {
    return this.simpleLootrInstance.getClientOpeners();
  }

  @Override
  public @NonNull ILootrType getDataType() {
    return BuiltInLootrTypes.CHEST;
  }

  @Override
  public void markChanged() {
    setChanged();
    markDataChanged();
  }

  @Override
  @NotNull
  public UUID getDataId() {
    return this.simpleLootrInstance.getInfoUUID();
  }

  @Override
  public int getDataKey() {
    return this.simpleLootrInstance.getInfoKey();
  }

  @Override
  public Identifier getDataIdentifier() {
    return this.simpleLootrInstance.getInfoIdentifier();
  }

  @Override
  public boolean hasBeenOpened() {
    return this.simpleLootrInstance.hasBeenOpened();
  }

  @Override
  public boolean isPhysicallyOpen() {
    return getOpenNess(1f) > 0;
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
    return this.simpleLootrInstance.getInfoContainerSize();
  }

  @Override
  public long getDataLootSeed() {
    return getLootTableSeed();
  }

  @Override
  public @Nullable NonNullList<ItemStack> getDataReferenceInventory() {
    return this.simpleLootrInstance.getReferenceInventory();
  }

  @Override
  public boolean isDataReferenceInventory() {
    return this.simpleLootrInstance.isReferenceInventory();
  }

  @Override
  public Level getDataLevel() {
    return getLevel();
  }

  @Override
  public int getPhysicalOpenerCount() {
    return this.openersCounter.getOpenerCount();
  }

  @Override
  public @Nullable IContainerTrigger getTrigger() {
    return LootrRegistry.getChestTrigger();
  }

  public static int getOpenCount(BlockGetter pLevel, BlockPos pPos) {
    BlockState blockstate = pLevel.getBlockState(pPos);
    if (blockstate.hasBlockEntity()) {
      BlockEntity blockentity = pLevel.getBlockEntity(pPos);
      if (blockentity instanceof LootrChestBlockEntity chest) {
        return chest.openersCounter.getOpenerCount();
      }
    }

    return 0;
  }

  protected static void playSound(Level pLevel, BlockPos pPos, BlockState pState, SoundEvent pSound) {
    double d0 = (double) pPos.getX() + 0.5D;
    double d1 = (double) pPos.getY() + 0.5D;
    double d2 = (double) pPos.getZ() + 0.5D;

    pLevel.playSound(null, d0, d1, d2, pSound, SoundSource.BLOCKS, 0.5F, pLevel.getRandom().nextFloat() * 0.1F + 0.9F);
  }

  @AutoService(ILootrBlockEntityWrapper.class)
  public static class DefaultBlockEntityWrapper implements ILootrBlockEntityWrapper<LootrChestBlockEntity> {
    @Override
    public ILootrBlockEntity apply(LootrChestBlockEntity blockEntity) {
      return blockEntity;
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
      return LootrRegistry.getChestBlockEntity();
    }
  }
}
