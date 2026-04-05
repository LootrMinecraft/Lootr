package noobanidus.mods.lootr.common.api.data.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.data.ILootrContainerInstance;
import noobanidus.mods.lootr.common.api.interfaces.type.ILootrType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Set;
import java.util.UUID;

public record CustomLootrContainerInstance(
    Level level,
    ILootrType type,
    UUID id,
    int key,
    Identifier identifier,
    BlockPos pos,
    int containerSize,
    ResourceKey<LootTable> lootTable,
    long lootSeed,
    Component displayName,
    ResourceKey<Level> dimension,
    NonNullList<ItemStack> customInventory) implements ILootrContainerInstance {

  @Override
  public @NonNull ILootrType getDataType() {
    return type();
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
    return pos();
  }

  @Override
  public ResourceKey<LootTable> getDataLootTable() {
    return lootTable();
  }

  @Override
  public @Nullable Component getDataDisplayName() {
    return displayName();
  }

  @Override
  public @NotNull ResourceKey<Level> getDataDimension() {
    return dimension();
  }

  @Override
  public int getDataContainerSize() {
    return containerSize();
  }

  @Override
  public long getDataLootSeed() {
    return lootSeed();
  }

  @Override
  public Level getDataLevel() {
    return level();
  }

  @Override
  public @Nullable NonNullList<ItemStack> getDataReferenceInventory() {
    return customInventory();
  }

  @Override
  public boolean isDataReferenceInventory() {
    return customInventory() == null || customInventory().isEmpty();
  }

  @Override
  public void markInstanceChanged() {
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
