package noobanidus.mods.lootr.common.api.data.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.type.BuiltInLootrTypes;
import noobanidus.mods.lootr.common.api.type.ILootrType;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.ILootrInventoryStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Set;
import java.util.UUID;

// Currently used by:
// - Bumblezone integration
public record RandomizableContainerInstance(
    @NotNull RandomizableContainerBlockEntity blockEntity, UUID id, int key, Identifier identifier,
    NonNullList<ItemStack> customInventory) implements ILootrBlockEntity {

  @Override
  public @NonNull ILootrType getDataType() {
    return BuiltInLootrTypes.SIMPLE;
  }

  @Override
  public @NotNull UUID getDataId() {
    return id();
  }

  @Override
  public int getDataKey() {
    return key();
  }

  @Override
  public Identifier getDataIdentifier() {
    return identifier();
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
  public @NotNull BlockPos getDataPos() {
    return blockEntity.getBlockPos();
  }

  @Override
  public ResourceKey<LootTable> getDataLootTable() {
    return blockEntity.getLootTable();
  }

  @Override
  public Component getDataDisplayName() {
    return blockEntity.getDisplayName();
  }

  @Override
  public @NotNull ResourceKey<Level> getDataDimension() {
    // We don't care if this causes a null pointer.
    //noinspection DataFlowIssue
    return blockEntity.getLevel().dimension();
  }

  @Override
  public int getDataContainerSize() {
    return blockEntity.getContainerSize();
  }

  @Override
  public long getDataLootSeed() {
    return blockEntity.getLootTableSeed();
  }

  @Override
  public @Nullable NonNullList<ItemStack> getDataReferenceInventory() {
    return customInventory();
  }

  @Override
  public boolean isDataReferenceInventory() {
    return false;
  }

  @Override
  public Level getDataLevel() {
    return blockEntity.getLevel();
  }

  @Override
  public void markChanged() {
    blockEntity.setChanged();
  }

  @Override
  public void markDataChanged() {
    if (blockEntity.getLevel() != null && blockEntity.getLevel().isClientSide()) {
      return;
    }
    ILootrInventoryStore data = LootrAPI.getData(this);
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
