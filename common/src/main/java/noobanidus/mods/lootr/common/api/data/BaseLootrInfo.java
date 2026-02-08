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

import java.util.Optional;
import java.util.UUID;

/**
 * This is a canonical, immutable implementation of ILootrInfo.
 * <br />
 * It is specifically used to store data in `LootrSavedData`.
 */
public record BaseLootrInfo(ILootrType type, UUID uuid,
                            String cachedKey, BlockPos pos, @Nullable Component name, ResourceKey<Level> dimension,
                            int containerSize, NonNullList<ItemStack> customInventory, ResourceKey<LootTable> table,
                            long seed) implements ILootrInfo {
  @SuppressWarnings({"OptionalUsedAsFieldOrParameterType"})
  public BaseLootrInfo(ILootrType iLootrType, UUID uuid, String s, BlockPos pos, Optional<Component> component, ResourceKey<Level> levelResourceKey, Integer integer, Optional<NonNullList<ItemStack>> itemStacks, Optional<ResourceKey<LootTable>> lootTableResourceKey, Optional<Long> aLong) {
    this(iLootrType, uuid, s, pos, component.orElse(null), levelResourceKey, integer, itemStacks.orElse(null), lootTableResourceKey.orElse(null), aLong.orElse(-1L));
  }

  @SuppressWarnings("deprecation")
  public static BaseLootrInfo copy(ILootrInfo info) {
    return new BaseLootrInfo(info.getInfoType(), info.getInfoUUID(), info.getInfoKey(), info.getInfoPos(), info.getInfoDisplayName(), info.getInfoDimension(), info.getInfoContainerSize(), info.getInfoReferenceInventory(), info.getInfoLootTable(), info.getInfoLootSeed());
  }

  @SuppressWarnings("deprecation")
  public BaseLootrInfo(ILootrType type, UUID uuid, String cachedKey, BlockPos pos, @Nullable Component name, ResourceKey<Level> dimension, int containerSize, NonNullList<ItemStack> customInventory, ResourceKey<LootTable> table, long seed) {
    this.type = type;
    this.uuid = uuid;
    this.cachedKey = cachedKey;
    this.pos = pos;
    this.name = name;
    this.dimension = dimension;
    this.containerSize = containerSize;
    this.customInventory = customInventory;
    this.table = table;
    this.seed = seed;
  }

  @Override
  @Nullable
  public ILootrType getInfoType() {
    return type();
  }

  @Override
  public @NotNull UUID getInfoUUID() {
    return uuid();
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
  public Component getInfoDisplayName() {
    if (name == null) {
      return Component.empty();
    }

    return name;
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
  public @Nullable NonNullList<ItemStack> getInfoReferenceInventory() {
    return customInventory();
  }

  @Override
  public boolean isInfoReferenceInventory() {
    return customInventory() != null && !customInventory().isEmpty();
  }

  @Override
  public @Nullable ResourceKey<LootTable> getInfoLootTable() {
    return table();
  }

  @Override
  public long getInfoLootSeed() {
    return seed();
  }

  @Override
  public Level getInfoLevel() {
    return getDefaultLevel();
  }
}
