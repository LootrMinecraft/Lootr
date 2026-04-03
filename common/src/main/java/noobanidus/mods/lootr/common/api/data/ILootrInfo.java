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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.type.ILootrType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Optional;
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
public interface ILootrInfo extends IKeyedData {
  @NonNull
  ILootrType getInfoType();

  default LootFiller getDefaultFiller() {
    ILootrType type = getInfoType();
    return type.getDefaultFiller();
  }

  default boolean canRefresh() {
    ILootrType type = getInfoType();
    return type.canRefresh();
  }

  default boolean canDecay() {
    ILootrType type = getInfoType();
    return type.canDecay();
  }

  default boolean canBeMarkedUnopened() {
    ILootrType type = getInfoType();
    return type.canBeMarkedUnopened();
  }

  default boolean canDropContentsWhenBroken() {
    ILootrType type = getInfoType();
    return type.canDropContentsWhenBroken();
  }

  @NotNull
  default Vec3 getInfoVec() {
    return Vec3.atCenterOf(getInfoPos());
  }

  @Override
  UUID getInfoUUID();

  @Override
  int getInfoKey();

  @Override
  Identifier getInfoIdentifier();

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

  default boolean canPlayerOpen(ServerPlayer player) {
    return true;
  }

  default void informPlayerCannotOpen(ServerPlayer player) {
  }

  boolean isInfoReferenceInventory();

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

    return getInfoType().getContainer(this, level);
  }

  default NonNullList<ItemStack> buildInitialInventory() {
    return NonNullList.withSize(getInfoContainerSize(), ItemStack.EMPTY);
  }

  Codec<ILootrInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      ILootrType.CODEC.fieldOf("type").forGetter(ILootrInfo::getInfoType),
      UUIDUtil.CODEC.fieldOf("uuid").forGetter(ILootrInfo::getInfoUUID),
      Codec.INT.fieldOf("key").forGetter(ILootrInfo::getInfoKey),
      Identifier.CODEC.fieldOf("identifier").forGetter(ILootrInfo::getInfoIdentifier),
      BlockPos.CODEC.fieldOf("position").forGetter(ILootrInfo::getInfoPos),
      ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(i -> Optional.ofNullable(i.getInfoDisplayName())),
      Identifier.CODEC.xmap(loc -> ResourceKey.create(Registries.DIMENSION, loc), ResourceKey::identifier)
          .fieldOf("dimension").forGetter(ILootrInfo::getInfoDimension),
      Codec.INT.fieldOf("size").forGetter(ILootrInfo::getInfoContainerSize),
      ItemStack.OPTIONAL_CODEC.listOf()
          .xmap(list -> NonNullList.of(ItemStack.EMPTY, list.toArray(new ItemStack[0])), list -> list)
          .optionalFieldOf("reference")
          .forGetter(info -> info.isInfoReferenceInventory() ? Optional.ofNullable(info.getInfoReferenceInventory()) : Optional.empty()),
      Identifier.CODEC.xmap(loc -> ResourceKey.create(Registries.LOOT_TABLE, loc), ResourceKey::identifier)
          .optionalFieldOf("table").forGetter(i -> Optional.ofNullable(i.getInfoLootTable())),
      Codec.LONG.optionalFieldOf("seed").forGetter(info ->
          info.getInfoLootTable() != null ? Optional.of(info.getInfoLootSeed()) : Optional.empty()
      )
  ).apply(instance, BaseLootrInfo::new));
}
