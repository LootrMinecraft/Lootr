package noobanidus.mods.lootr.common.api.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.BuiltInLootrTypes;
import noobanidus.mods.lootr.common.api.ILootrType;
import noobanidus.mods.lootr.common.api.LootrAPI;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * The base class for all Lootr information holders.
 * <br />
 * This specifically stores information regarding both the container
 * (and its type), as well as its location in the world (and this value
 * is updated every time the container is accessed), loot table,
 * seed, etc, but it is *not necessarily* equivalent to the actual
 * object storing the information (such as the block entity or entity).
 * <br />
 * In theory, there is no need for anyone to ever implement this directly.
 * Instead, implement ILootrInfoProvider, ILootrEntity or ILootrBlockEntity.
 */
@ApiStatus.Internal
public interface ILootrInfo {
  @Deprecated
  @Nullable
  LootrBlockType getInfoBlockType();

  @Deprecated
  @Nullable
  LootrInfoType getInfoType();

  @Nullable
  ILootrType getInfoNewType();

  default LootFiller getDefaultFiller() {
    ILootrType type = getInfoNewType();
    if (type == null) {
      return DefaultLootFiller.getInstance();
    } else {
      return type.getDefaultFiller();
    }
  }

  default boolean canRefresh() {
    ILootrType type = getInfoNewType();
    if (type == null) {
      return true;
    } else {
      return type.canRefresh();
    }
  }

  default boolean canDecay() {
    ILootrType type = getInfoNewType();
    if (type == null) {
      return true;
    } else {
      return type.canDecay();
    }
  }

  default boolean canBeMarkedUnopened() {
    ILootrType type = getInfoNewType();
    if (type == null) {
      return true;
    } else {
      return type.canBeMarkedUnopened();
    }
  }

  default boolean canDropContentsWhenBroken() {
    ILootrType type = getInfoNewType();
    if (type == null) {
      return true;
    } else {
      return type.canDropContentsWhenBroken();
    }
  }

  // Prefer using getInfoNewType().getReplacementBlock()
  // This exists for backwards compatibility with "simple"
  // implementations.
  @Nullable
  @Deprecated
  default Block getReplacementBlock() {
    ILootrType type = getInfoNewType();
    if (type == null) {
      return null;
    } else {
      return type.getReplacementBlock();
    }
  }

  // Prefer using getInfoNewType().getReplacementEntity()
  // This exists for backwards compatibility with "simple"
  // implementations.
  @Nullable
  @Deprecated
  default EntityType<?> getReplacementEntity() {
    ILootrType type = getInfoNewType();
    if (type == null) {
      return null;
    } else {
      return type.getReplacementEntity();
    }
  }

  // Prefer using getInfoNewType().isEntity() This exists
  // exists for backwards compatibility with "simple"
  // implementations.
  @Deprecated
  default boolean isEntity () {
    ILootrType type = getInfoNewType();
    if (type == null) {
      return false;
    } else {
      return type.isEntity();
    }
  }

  @NotNull
  default Vec3 getInfoVec() {
    return Vec3.atCenterOf(getInfoPos());
  }

  @NotNull
  UUID getInfoUUID();

  String getInfoKey();

  static String generateInfoKey(UUID id) {
    String idString = id.toString();
    return "lootr/" + idString.charAt(0) + "/" + idString.substring(0, 2) + "/" + idString;
  }

  // The container has been opened at some point in time and has at least one inventory contained (unless inventories have been cleared).
  boolean hasBeenOpened();

  // This container is currently open or opening.
  boolean isPhysicallyOpen();

  @NotNull
  BlockPos getInfoPos();

  @Nullable Component getInfoDisplayName();

  @NotNull
  ResourceKey<Level> getInfoDimension();

  int getInfoContainerSize();

  @Nullable
  NonNullList<ItemStack> getInfoReferenceInventory();

  void setInfoReferenceInventory (NonNullList<ItemStack> reference);

  default boolean canPlayerOpen (ServerPlayer player) {
    return true;
  }

  default void informPlayerCannotOpen (ServerPlayer player) {
  }

  boolean isInfoReferenceInventory();

  default boolean isInfoReferenceInventoryInternal (boolean isReferenceInventory) {
    if (isReferenceInventory && getInfoLootTable() != null) {
      LootrAPI.LOG.error("Lootr container {} at {} in {} has both a loot table and a custom inventory. This is not supported and may cause issues.", this, getInfoPos(), getInfoDimension());
    }
    return isReferenceInventory;
  }

  // This can be null but only if it is a custom inventory.
  @Nullable
  ResourceKey<LootTable> getInfoLootTable();

  long getInfoLootSeed();

  Set<Class<?>> WARNED_CLASSES = new HashSet<>();

  default Level getInfoLevel() {
    if (!WARNED_CLASSES.contains(this.getClass())) {
      LootrAPI.LOG.error("Class {} does not implement `getInfoLevel`! Falling back on `getDefaultLevel`.", this.getClass()
          .getName());
      WARNED_CLASSES.add(this.getClass());
    }
    return getDefaultLevel();
  }

  // TODO: WTF?????
  @Nullable
  default Level getDefaultLevel() {
    MinecraftServer server = LootrAPI.getServer();
    if (server == null) {
      return null;
    }

    return server.getLevel(getInfoDimension());
  }

  @Nullable
  default Container getInfoContainer() {
    if (!(getInfoLevel() instanceof ServerLevel level) || level.isClientSide()) {
      return null;
    }

    if (getInfoNewType() == null) {
      // Try to guess
      BlockEntity be = level.getBlockEntity(getInfoPos());
      if (be instanceof Container container) {
        return container;
      }
      Entity entity = level.getEntity(getInfoUUID());
      if (entity instanceof Container container) {
        return container;
      }
      LootrAPI.LOG.warn("Unable to guess container type for LootrInfo with key '{}'", getInfoKey());
      return null;
    } else {
      return getInfoNewType().getContainer(this, level);
    }
  }

  default NonNullList<ItemStack> buildInitialInventory() {
    return NonNullList.withSize(getInfoContainerSize(), ItemStack.EMPTY);
  }

  default void saveInfoToTag(CompoundTag tag, HolderLookup.Provider provider) {
    if (getInfoType() != null) {
      tag.putInt("type", getInfoType().ordinal());
    }
    if (getInfoNewType() != null) {
      tag.putString("newType", getInfoNewType().getName());
    }
    if (getInfoBlockType() != null) {
      tag.putInt("blockType", getInfoBlockType().ordinal());
    }
    tag.put("position", NbtUtils.writeBlockPos(getInfoPos()));
    tag.putString("key", getInfoKey());
    tag.putString("dimension", getInfoDimension().location().toString());
    tag.putUUID("uuid", getInfoUUID());
    tag.putInt("size", getInfoContainerSize());
    if (getInfoLootTable() != null) {
      tag.putString("table", getInfoLootTable().location().toString());
      tag.putLong("seed", getInfoLootSeed());
    }
    if (getInfoDisplayName() != null) {
      tag.putString("name", Component.Serializer.toJson(getInfoDisplayName(), provider));
    }
    if (isInfoReferenceInventory()) {
      //noinspection DataFlowIssue
      tag.putInt("referenceSize", getInfoReferenceInventory().size());
      tag.put("reference", ContainerHelper.saveAllItems(new CompoundTag(), getInfoReferenceInventory(), true, provider));
    }
  }

  static ILootrInfo loadInfoFromTag(CompoundTag tag, HolderLookup.Provider provider) {
    ILootrType type = null;

    if (tag.contains("newType", CompoundTag.TAG_STRING)) {
      type = LootrAPI.getType(tag.getString("newType"));

      if (type == null) {
        LootrAPI.LOG.error("Couldn't find LootrType '{}' when loading LootrInfo from tag: {}", tag.getString("newType"), tag);
        throw new IllegalStateException("Couldn't find LootrType '" + tag.getString("newType") + "' when loading LootrInfo from tag: " + tag);
      }
    }

    // LEGACY
    if (type == null) {
      if (tag.contains("blockType", CompoundTag.TAG_INT)) {
        //noinspection deprecation
        LootrBlockType oldType = LootrBlockType.values()[tag.getInt("blockType")];
        //noinspection deprecation
        type = BuiltInLootrTypes.fromLegacy(oldType);
      } else {
        if (tag.contains("type", CompoundTag.TAG_INT)) {
          type = BuiltInLootrTypes.CHEST;
          // LEGACY
        } else if (tag.contains("entity") && tag.getBoolean("entity")) {
          type = BuiltInLootrTypes.MINECART;
        }
      }
    }
    BlockPos pos = NbtUtils.readBlockPos(tag, "position").orElse(BlockPos.ZERO);
    UUID uuid = tag.getUUID("uuid");
    ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString("dimension")));
    int size = tag.getInt("size");
    Component name = null;
    if (tag.contains("name")) {
      name = Component.Serializer.fromJson(tag.getString("name"), provider);
    }
    NonNullList<ItemStack> reference = null;
    if (tag.contains("reference") && tag.contains("referenceSize")) {
      reference = NonNullList.withSize(tag.getInt("referenceSize"), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(tag.getCompound("reference"), reference, provider);
      type = BuiltInLootrTypes.INVENTORY;
    }

    ResourceKey<LootTable> table = null;
    long seed = -1;
    if (tag.contains("table")) {
      table = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(tag.getString("table")));
      seed = tag.getLong("seed");
    }

    if (type == null) {
      LootrAPI.LOG.error("Couldn't determine LootrType when loading LootrInfo from tag, guessing chest: {}", tag);
      type = BuiltInLootrTypes.CHEST;
    }
    return new BaseLootrInfo(null, null, type, uuid, ILootrInfo.generateInfoKey(uuid), pos, name, dimension, size, reference, table, seed);
  }

  @Deprecated
  enum LootrInfoType {
    CONTAINER_BLOCK_ENTITY,
    CONTAINER_ENTITY;
  }
}
