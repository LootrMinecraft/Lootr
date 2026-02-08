package noobanidus.mods.lootr.common.api.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.type.ILootrType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public record CustomLootrInfoProvider(
    ILootrType type,
    UUID id,
    String cachedKey,
    BlockPos pos,
    int containerSize,
    ResourceKey<LootTable> lootTable,
    long lootSeed,
    Component displayName,
    ResourceKey<Level> dimension,
    NonNullList<ItemStack> customInventory) implements ILootrInfoProvider {

  @SuppressWarnings("deprecation")
  public CustomLootrInfoProvider(ILootrType type, UUID id, String cachedKey, BlockPos pos, int containerSize, ResourceKey<LootTable> lootTable, long lootSeed, Component displayName, ResourceKey<Level> dimension, NonNullList<ItemStack> customInventory) {
    this.type = type;
    this.id = id;
    this.cachedKey = cachedKey;
    this.pos = pos;
    this.containerSize = containerSize;
    this.lootTable = lootTable;
    this.lootSeed = lootSeed;
    this.displayName = displayName;
    this.dimension = dimension;
    this.customInventory = customInventory;
  }

  @Override
  public ILootrType getInfoType() {
    return type();
  }

  @Override
  public @NotNull UUID getInfoUUID() {
    return id();
  }

  @Override
  public String getInfoKey() {
    return cachedKey();
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
    return pos();
  }

  @Override
  public ResourceKey<LootTable> getInfoLootTable() {
    return lootTable();
  }

  @Override
  public @Nullable Component getInfoDisplayName() {
    return displayName();
  }

  @Override
  public @NotNull ResourceKey<Level> getInfoDimension() {
    return dimension();
  }

  @Override
  public int getInfoContainerSize() {
    return containerSize();
  }

  @Override
  public long getInfoLootSeed() {
    return lootSeed();
  }

  @Override
  public Level getInfoLevel() {
    return getDefaultLevel();
  }

  @Override
  public @Nullable NonNullList<ItemStack> getInfoReferenceInventory() {
    return customInventory();
  }

  @Override
  public boolean isInfoReferenceInventory() {
    return customInventory() == null || customInventory().isEmpty();
  }

  @Override
  public void markChanged() {
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
