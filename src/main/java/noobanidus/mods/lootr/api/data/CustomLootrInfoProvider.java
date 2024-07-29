package noobanidus.mods.lootr.api.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public record CustomLootrInfoProvider(
    UUID id,
    BlockPos pos,
    int containerSize,
    ResourceKey<LootTable> lootTable,
    long lootSeed,
    Component displayName,
    ResourceKey<Level> dimension,
    NonNullList<ItemStack> customInventory,
    LootrInfoType type,
    LootrBlockType blockType) implements ILootrInfoProvider {

  @Override
  public LootrBlockType getInfoBlockType() {
    return blockType();
  }

  @Override
  public LootrInfoType getInfoType() {
    return type();
  }

  @Override
  public @NotNull UUID getInfoUUID() {
    return id();
  }

  @Override
  public boolean hasBeenOpened() {
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
