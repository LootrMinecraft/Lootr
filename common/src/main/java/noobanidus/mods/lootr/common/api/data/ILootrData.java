package noobanidus.mods.lootr.common.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.base.BaseLootrData;
import noobanidus.mods.lootr.common.api.filler.ILootFiller;
import noobanidus.mods.lootr.common.api.interfaces.type.ILootrType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.UUID;

/**
 * This is the main method for accessing information from a Lootr container.
 * Specifically for accessing loot table, position, etc.
 * <br />
 * This specifically stores information regarding both the container
 * (and its type), as well as its location in the world (and this value
 * is updated every time the container is accessed), loot table,
 * seed, etc, but it is *not necessarily* equivalent to the actual
 * object storing the information (such as the block entity or entity).
 * <br />
 * In theory, there is no need for anyone to ever implement this directly.
 * Instead, implement ILootrDataInstance, ILootrEntity or ILootrBlockEntity.
 */
@ApiStatus.Internal
public interface ILootrData extends IKeyedData {
  @NonNull
  ILootrType getDataType();

  default ILootFiller getDefaultFiller() {
    ILootrType type = getDataType();
    return type.getDefaultFiller();
  }

  default boolean canRefresh() {
    ILootrType type = getDataType();
    return type.canRefresh();
  }

  default boolean canDecay() {
    ILootrType type = getDataType();
    return type.canDecay();
  }

  default boolean canBeMarkedUnopened() {
    ILootrType type = getDataType();
    return type.canBeMarkedUnopened();
  }

  default boolean canDropContentsWhenBroken() {
    ILootrType type = getDataType();
    return type.canDropContentsWhenBroken();
  }

  @NotNull
  default Vec3 getDataVec() {
    return Vec3.atCenterOf(getDataPos());
  }

  @Override
  UUID getDataId();

  @Override
  Identifier getDataIdentifier();

  // The container has been opened at some point in time and has at least one inventory contained (unless inventories have been cleared).
  boolean hasBeenOpened();

  // This container is currently open or opening.
  boolean isPhysicallyOpen();

  @NotNull
  BlockPos getDataPos();

  @Nullable Component getDataDisplayName();

  @NotNull
  ResourceKey<Level> getDataDimension();

  int getDataContainerSize();

  @Nullable
  NonNullList<ItemStack> getDataReferenceInventory();

  default boolean canPlayerOpen(ServerPlayer player) {
    return true;
  }

  default void informPlayerCannotOpen(ServerPlayer player) {
  }

  default boolean isDataReferenceInventoryInternal (boolean isReferenceInventory) {
    if (isReferenceInventory && getDataLootTable() != null) {
      LootrAPI.LOG.error("Lootr container {} at {} in {} has both a loot table and a custom inventory. This is not supported and may cause issues.", this, getDataPos(), getDataDimension());
    }
    return isReferenceInventory;
  }

  boolean isDataReferenceInventory();

  // This can be null but only if it is a custom inventory.
  @Nullable
  ResourceKey<LootTable> getDataLootTable();

  long getDataLootSeed();

  default NonNullList<ItemStack> buildInitialInventory() {
    return NonNullList.withSize(getDataContainerSize(), ItemStack.EMPTY);
  }

  Codec<ILootrData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      ILootrType.CODEC.fieldOf("type").forGetter(ILootrData::getDataType),
      UUIDUtil.CODEC.fieldOf("uuid").forGetter(ILootrData::getDataId),
      Identifier.CODEC.fieldOf("identifier").forGetter(ILootrData::getDataIdentifier),
      BlockPos.CODEC.fieldOf("position").forGetter(ILootrData::getDataPos),
      ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(i -> Optional.ofNullable(i.getDataDisplayName())),
      Identifier.CODEC.xmap(loc -> ResourceKey.create(Registries.DIMENSION, loc), ResourceKey::identifier)
          .fieldOf("dimension").forGetter(ILootrData::getDataDimension),
      Codec.INT.fieldOf("size").forGetter(ILootrData::getDataContainerSize),
      ItemStack.OPTIONAL_CODEC.listOf()
          .xmap(list -> NonNullList.of(ItemStack.EMPTY, list.toArray(new ItemStack[0])), list -> list)
          .optionalFieldOf("reference")
          .forGetter(info -> info.isDataReferenceInventory() ? Optional.ofNullable(info.getDataReferenceInventory()) : Optional.empty()),
      Identifier.CODEC.xmap(loc -> ResourceKey.create(Registries.LOOT_TABLE, loc), ResourceKey::identifier)
          .optionalFieldOf("table").forGetter(i -> Optional.ofNullable(i.getDataLootTable())),
      Codec.LONG.optionalFieldOf("seed").forGetter(info ->
          info.getDataLootTable() != null ? Optional.of(info.getDataLootSeed()) : Optional.empty()
      )
  ).apply(instance, BaseLootrData::new));
}
