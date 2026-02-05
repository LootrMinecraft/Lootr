package noobanidus.mods.lootr.common.api.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.BuiltInLootrTypes;
import noobanidus.mods.lootr.common.api.ILootrType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * This is a canonical, immutable implementation of ILootrInfo.
 * <br />
 * It is specifically used to store data in `LootrSavedData`.
 */
public record BaseLootrInfo(@Deprecated @Nullable LootrBlockType blockType,
                            @Deprecated @Nullable LootrInfoType infoType, @Nullable ILootrType type, UUID uuid,
                            String cachedKey, BlockPos pos, @Nullable Component name, ResourceKey<Level> dimension,
                            int containerSize, NonNullList<ItemStack> customInventory, ResourceKey<LootTable> table,
                            long seed) implements ILootrInfo {
  @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "deprecation"})
  public BaseLootrInfo(Optional<LootrBlockType> lootrBlockType, Optional<LootrInfoType> lootrInfoType, Optional<ILootrType> iLootrType, UUID uuid, String s, BlockPos pos, Optional<Component> component, ResourceKey<Level> levelResourceKey, Integer integer, Optional<NonNullList<ItemStack>> itemStacks, Optional<ResourceKey<LootTable>> lootTableResourceKey, Optional<Long> aLong) {
    this(lootrBlockType.orElse(null), lootrInfoType.orElse(null), iLootrType.orElse(null), uuid, s, pos, component.orElse(null), levelResourceKey, integer, itemStacks.orElse(null), lootTableResourceKey.orElse(null), aLong.orElse(-1L));
  }

  @SuppressWarnings("deprecation")
  public static BaseLootrInfo copy(ILootrInfo info) {
    return new BaseLootrInfo(info.getInfoBlockType(), info.getInfoType(), info.getInfoNewType(), info.getInfoUUID(), info.getInfoKey(), info.getInfoPos(), info.getInfoDisplayName(), info.getInfoDimension(), info.getInfoContainerSize(), info.getInfoReferenceInventory(), info.getInfoLootTable(), info.getInfoLootSeed());
  }

  @SuppressWarnings("deprecation")
  public BaseLootrInfo(@Deprecated @Nullable LootrBlockType blockType, @Deprecated @Nullable LootrInfoType infoType, @Nullable ILootrType type, UUID uuid, String cachedKey, BlockPos pos, @Nullable Component name, ResourceKey<Level> dimension, int containerSize, NonNullList<ItemStack> customInventory, ResourceKey<LootTable> table, long seed) {
    this.blockType = blockType;
    this.infoType = infoType;
    this.type = resolveType(blockType, infoType, type);
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

  @SuppressWarnings("deprecation")
  public static ILootrType resolveType (@Nullable LootrBlockType blockType, @Nullable LootrInfoType infoType, @Nullable ILootrType type) {
    if (type != null) {
      return type;
    }
    if (blockType != null) {
      return BuiltInLootrTypes.fromLegacy(blockType);
    }
    if (infoType != null) {
      if (infoType == LootrInfoType.CONTAINER_BLOCK_ENTITY) {
        return BuiltInLootrTypes.CHEST;
      } else if (infoType == LootrInfoType.CONTAINER_ENTITY) {
        return BuiltInLootrTypes.MINECART;
      }
    }

    return BuiltInLootrTypes.CHEST;
  }

  @Override
  @Deprecated
  @Nullable
  public LootrBlockType getInfoBlockType() {
    return blockType();
  }

  @Override
  @Deprecated
  @Nullable
  public LootrInfoType getInfoType() {
    return infoType();
  }

  @Override
  @Nullable
  public ILootrType getInfoNewType() {
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
