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
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.LootrAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ILootrInfo {
  LootrBlockType getInfoBlockType();

  LootrInfoType getInfoType();

  @NotNull
  default Vec3 getInfoVec() {
    return Vec3.atCenterOf(getInfoPos());
  }

  @NotNull
  UUID getInfoUUID();

  default String getInfoKey() {
    String idString = getInfoUUID().toString();
    return "lootr/" + idString.charAt(0) + "/" + idString.substring(0, 2) + "/" + idString;
  }

  // TODO: Does this really make sense?
  boolean hasBeenOpened ();

  boolean isPhysicallyOpen ();

  @NotNull
  BlockPos getInfoPos();

  @Nullable Component getInfoDisplayName();

  @NotNull
  ResourceKey<Level> getInfoDimension();

  int getInfoContainerSize();

  @Nullable
  NonNullList<ItemStack> getInfoReferenceInventory();

  boolean isInfoReferenceInventory();

  // This can be null but only if it is a custom inventory.
  @Nullable
  ResourceKey<LootTable> getInfoLootTable();

  long getInfoLootSeed();

  @Nullable
  default Level getInfoLevel() {
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

    if (getInfoType() == LootrInfoType.CONTAINER_ENTITY) {
      Entity entity = level.getEntity(getInfoUUID());
      if (entity instanceof Container container) {
        return container;
      }
    } else if (getInfoType() == LootrInfoType.CONTAINER_BLOCK_ENTITY) {
      BlockEntity be = level.getBlockEntity(getInfoPos());
      if (be instanceof Container container) {
        return container;
      }
    }

    return null;
  }

  default NonNullList<ItemStack> buildInitialInventory() {
    return NonNullList.withSize(getInfoContainerSize(), ItemStack.EMPTY);
  }

  default void saveInfoToTag(CompoundTag tag, HolderLookup.Provider provider) {
    tag.putInt("type", getInfoType().ordinal());
    tag.putInt("blockType", getInfoBlockType().ordinal());
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
      tag.putInt("referenceSize", getInfoReferenceInventory().size());
      tag.put("reference", ContainerHelper.saveAllItems(new CompoundTag(), getInfoReferenceInventory(), true, provider));
    }
  }

  static ILootrInfo loadInfoFromTag(CompoundTag tag, HolderLookup.Provider provider) {
    LootrInfoType infoType = LootrInfoType.CONTAINER_BLOCK_ENTITY;
    if (tag.contains("type", CompoundTag.TAG_INT)) {
      infoType = LootrInfoType.values()[tag.getInt("type")];
    } else if (tag.contains("entity") && tag.getBoolean("entity")) {
      infoType = LootrInfoType.CONTAINER_ENTITY;
    } else {
      LootrAPI.LOG.error("Couldn't deduce the infoType of LootrInfo from tag: {}", tag);
    }
    LootrBlockType blockType = null;
    if (tag.contains("blockType", CompoundTag.TAG_INT)) {
      blockType = LootrBlockType.values()[tag.getInt("blockType")];
    }
    BlockPos pos = NbtUtils.readBlockPos(tag, "position").orElseThrow();
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
      blockType = LootrBlockType.INVENTORY;
    }
    if (blockType == null) {
      if (infoType == LootrInfoType.CONTAINER_ENTITY) {
        blockType = LootrBlockType.ENTITY;
      } else {
        blockType = LootrBlockType.CHEST;
      }
    }
    ResourceKey<LootTable> table = null;
    long seed = -1;
    if (tag.contains("table")) {
      table = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(tag.getString("table")));
      seed = tag.getLong("seed");
    }
    return new BaseLootrInfo(blockType, infoType, uuid, pos, name, dimension, size, reference, table, seed);
  }

  enum LootrInfoType {
    CONTAINER_BLOCK_ENTITY,
    CONTAINER_ENTITY;
  }
}
