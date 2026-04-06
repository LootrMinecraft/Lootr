package noobanidus.mods.lootr.common.api.data.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.data.ILootrData;
import noobanidus.mods.lootr.common.api.interfaces.type.ILootrType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.UUID;

/**
 * This is a canonical, immutable implementation of ILootrBaseData.
 * <br />
 * It is specifically used to store data in `LootrSavedData`.
 */
public record BaseLootrData(ILootrType type, UUID uuid,
                            Identifier identifier, BlockPos pos, @Nullable Component name,
                            ResourceKey<Level> dimension,
                            int containerSize, NonNullList<ItemStack> customInventory, ResourceKey<LootTable> table,
                            long seed) implements ILootrData {
  @SuppressWarnings({"OptionalUsedAsFieldOrParameterType"})
  public BaseLootrData(ILootrType iLootrType, UUID uuid, Identifier identifier, BlockPos pos, Optional<Component> component, ResourceKey<Level> levelResourceKey, Integer integer, Optional<NonNullList<ItemStack>> itemStacks, Optional<ResourceKey<LootTable>> lootTableResourceKey, Optional<Long> aLong) {
    this(iLootrType, uuid, identifier, pos, component.orElse(null), levelResourceKey, integer, itemStacks.orElse(null), lootTableResourceKey.orElse(null), aLong.orElse(-1L));
  }

  public static BaseLootrData copy(ILootrData info) {
    return new BaseLootrData(info.getDataType(), info.getDataId(), info.getDataIdentifier(), info.getDataPos(), info.getDataDisplayName(), info.getDataDimension(), info.getDataContainerSize(), info.getDataReferenceInventory(), info.getDataLootTable(), info.getDataLootSeed());
  }

  public BaseLootrData(ILootrType type, UUID uuid, Identifier identifier, BlockPos pos, @Nullable Component name, ResourceKey<Level> dimension, int containerSize, NonNullList<ItemStack> customInventory, ResourceKey<LootTable> table, long seed) {
    this.type = type;
    this.uuid = uuid;
    this.identifier = identifier;
    this.pos = pos;
    this.name = name;
    this.dimension = dimension;
    this.containerSize = containerSize;
    this.customInventory = customInventory;
    this.table = table;
    this.seed = seed;
  }

  @Override
  public @NonNull ILootrType getDataType() {
    return type();
  }

  @Override
  public @NotNull UUID getDataId() {
    return uuid();
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
  public Component getDataDisplayName() {
    if (name == null) {
      return Component.empty();
    }

    return name;
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
  public @Nullable NonNullList<ItemStack> getDataReferenceInventory() {
    return customInventory();
  }

  @Override
  public boolean isDataReferenceInventory() {
    return customInventory() != null && !customInventory().isEmpty();
  }

  @Override
  public @Nullable ResourceKey<LootTable> getDataLootTable() {
    return table();
  }

  @Override
  public long getDataLootSeed() {
    return seed();
  }
}
