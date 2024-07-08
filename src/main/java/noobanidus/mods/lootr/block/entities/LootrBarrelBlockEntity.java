package noobanidus.mods.lootr.block.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.client.model.data.ModelData;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.api.registry.LootrRegistry;
import noobanidus.mods.lootr.block.LootrBarrelBlock;
import noobanidus.mods.lootr.data.LootrInventory;
import noobanidus.mods.lootr.util.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LootrBarrelBlockEntity extends RandomizableContainerBlockEntity implements ILootrBlockEntity {
  private final Set<UUID> openers = new HashSet<>();
  private final Set<UUID> actualOpeners = new HashSet<>();
  private final NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
  protected UUID infoId = null;
  private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
    @Override
    protected void onOpen(Level level, BlockPos pos, BlockState state) {
      LootrBarrelBlockEntity.this.playSound(state, SoundEvents.BARREL_OPEN);
      LootrBarrelBlockEntity.this.updateBlockState(state, true);
    }

    @Override
    protected void onClose(Level level, BlockPos pos, BlockState state) {
      LootrBarrelBlockEntity.this.playSound(state, SoundEvents.BARREL_CLOSE);
      LootrBarrelBlockEntity.this.updateBlockState(state, false);
    }

    @Override
    protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int p_155069_, int p_155070_) {
    }

    @Override
    protected boolean isOwnContainer(Player player) {
      if (player.containerMenu instanceof ChestMenu && ((ChestMenu) player.containerMenu).getContainer() instanceof LootrInventory data) {
        return data.getInfo().getInfoUUID().equals(LootrBarrelBlockEntity.this.getInfoUUID());
      }
      return false;
    }
  };
  protected boolean clientOpened = false;
  private ModelData modelData = null;
  private boolean savingToItem = false;

  public LootrBarrelBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
    super(LootrRegistry.getBarrelBlockEntity(), pWorldPosition, pBlockState);
  }

  @NotNull
  @Override
  public ModelData getModelData() {
    if (modelData == null) {
      modelData = ModelData.builder().with(LootrBarrelBlock.OPENED, false).build();
    }
    Player player = Getter.getPlayer();
    if (player != null) {
      return modelData.derive().with(LootrBarrelBlock.OPENED, openers.contains(player.getUUID())).build();
    }
    return modelData;
  }

  @Override
  @NotNull
  public UUID getInfoUUID() {
    if (this.infoId == null) {
      this.infoId = UUID.randomUUID();
    }
    return this.infoId;
  }

  @Override
  protected NonNullList<ItemStack> getItems() {
    return items;
  }

  @Override
  protected void setItems(NonNullList<ItemStack> pItems) {
  }

  @Override
  public void unpackLootTable(@Nullable Player player) {
  }

  @Override
  public Set<UUID> getVisualOpeners() {
    return openers;
  }

  @Override
  public Set<UUID> getActualOpeners() {
    return actualOpeners;
  }

  @SuppressWarnings("Duplicates")
  @Override
  public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
    if (compound.hasUUID("LootrId")) {
      this.infoId = compound.getUUID("LootrId");
    }
    if (this.infoId == null) {
      getInfoUUID();
    }
    if (compound.contains("LootrOpeners")) {
      ListTag openers = compound.getList("LootrOpeners", Tag.TAG_INT_ARRAY);
      this.openers.clear();
      for (Tag item : openers) {
        this.openers.add(NbtUtils.loadUUID(item));
      }
    }
    if (compound.contains("LootrActualOpeners")) {
      ListTag openers = compound.getList("LootrActualOpeners", Tag.TAG_INT_ARRAY);
      this.actualOpeners.clear();
      for (Tag item : openers) {
        this.actualOpeners.add(NbtUtils.loadUUID(item));
      }
    }
    requestModelDataUpdate();
    super.loadAdditional(compound, provider);
  }

  @Override
  public void saveToItem(ItemStack itemstack, HolderLookup.Provider provider) {
    savingToItem = true;
    super.saveToItem(itemstack, provider);
    savingToItem = false;
  }

  @Override
  protected void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
    super.saveAdditional(compound, provider);
    if (!LootrAPI.shouldDiscard() && !savingToItem) {
      compound.putUUID("LootrId", getInfoUUID());
      ListTag list = new ListTag();
      for (UUID opener : this.openers) {
        list.add(NbtUtils.createUUID(opener));
      }
      compound.put("LootrOpeners", list);
      ListTag list2 = new ListTag();
      for (UUID opener : this.actualOpeners) {
        list2.add(NbtUtils.createUUID(opener));
      }
      compound.put("LootrActualOpeners", list2);
    }
  }

  @Override
  protected Component getDefaultName() {
    return Component.translatable("container.barrel");
  }

  @Override
  protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
    return null;
  }

  @Override
  public int getContainerSize() {
    return 27;
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

  public void recheckOpen() {
    if (!this.remove) {
      this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
    }
  }

  protected void updateBlockState(BlockState pState, boolean pOpen) {
    this.level.setBlock(this.getBlockPos(), pState.setValue(BarrelBlock.OPEN, pOpen), 3);
  }

  protected void playSound(BlockState pState, SoundEvent pSound) {
    Vec3i vec3i = pState.getValue(BarrelBlock.FACING).getNormal();
    double d0 = (double) this.worldPosition.getX() + 0.5D + (double) vec3i.getX() / 2.0D;
    double d1 = (double) this.worldPosition.getY() + 0.5D + (double) vec3i.getY() / 2.0D;
    double d2 = (double) this.worldPosition.getZ() + 0.5D + (double) vec3i.getZ() / 2.0D;
    this.level.playSound(null, d0, d1, d2, pSound, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
  }

  @Override
  public boolean isClientOpened() {
    return clientOpened;
  }

  @Override
  public void setClientOpened(boolean opened) {
    this.clientOpened = opened;
  }

  @Override
  @NotNull
  public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
    CompoundTag result = super.getUpdateTag(provider);
    saveAdditional(result, provider);
    return result;
  }

  @Override
  @Nullable
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
  }

  @Override
  public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
    if (pkt.getTag() != null) {
      loadAdditional(pkt.getTag(), provider);
    }
  }

  @Override
  public BlockPos getInfoPos() {
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
    return getInfoLootSeed();
  }

  @Override
  public @Nullable NonNullList<ItemStack> getInfoReferenceInventory() {
    return null;
  }

  @Override
  public Level getInfoLevel() {
    return getLevel();
  }
}
