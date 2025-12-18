package noobanidus.mods.lootr.common.api.data.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.ILootrType;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.ILootrSavedData;
import noobanidus.mods.lootr.common.api.data.LootrBlockType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

// TODO: Continue supporting this?
public record RandomizableContainerBlockEntityLootrInfoProvider(
    @NotNull RandomizableContainerBlockEntity blockEntity, UUID id, String cachedId,
    NonNullList<ItemStack> customInventory) implements ILootrBlockEntity {

  @Override
  @Deprecated
  public LootrBlockType getInfoBlockType() {
    return switch (blockEntity) {
      case BarrelBlockEntity ignored -> LootrBlockType.BARREL;
      case TrappedChestBlockEntity ignored -> LootrBlockType.TRAPPED_CHEST;
      case ShulkerBoxBlockEntity ignored -> LootrBlockType.SHULKER;
      default -> LootrBlockType.CHEST;
    };
  }

  @Override
  public ILootrType getInfoNewType() {
    // TODO: Guess the type
    return null;
  }

  @Override
  public @NotNull UUID getInfoUUID() {
    return id();
  }

  @Override
  public String getInfoKey() {
    return cachedId();
  }

  @Override
  public boolean hasBeenOpened() {
    return false;
  }

  @Override
  public boolean isPhysicallyOpen() {
    return false;
  }

  @Override
  public @NotNull BlockPos getInfoPos() {
    return blockEntity.getBlockPos();
  }

  @Override
  public ResourceKey<LootTable> getInfoLootTable() {
    return blockEntity.getLootTable();
  }

  @Override
  public Component getInfoDisplayName() {
    return blockEntity.getDisplayName();
  }

  @Override
  public @NotNull ResourceKey<Level> getInfoDimension() {
    // We don't care if this causes a null pointer.
    //noinspection DataFlowIssue
    return blockEntity.getLevel().dimension();
  }

  @Override
  public int getInfoContainerSize() {
    return blockEntity.getContainerSize();
  }

  @Override
  public long getInfoLootSeed() {
    return blockEntity.getLootTableSeed();
  }

  @Override
  public @Nullable NonNullList<ItemStack> getInfoReferenceInventory() {
    return customInventory();
  }

  @Override
  public boolean isInfoReferenceInventory() {
    return false;
  }

  @Override
  public Level getInfoLevel() {
    return blockEntity.getLevel();
  }

  @Override
  public Container getInfoContainer() {
    return blockEntity;
  }

  @Override
  public void markChanged() {
    blockEntity.setChanged();
  }

  @Override
  public void markDataChanged() {
    // TODO: This is actually quite bad
    ILootrSavedData data = LootrAPI.getData(this);
    if (data != null) {
      data.markChanged();
    }
  }

  @Override
  public @Nullable Set<UUID> getClientOpeners() {
    return null;
  }

  @Override
  public boolean isClientOpened() {
    return false;
  }

  @Override
  public void setClientOpened(boolean opened) {
  }
}
