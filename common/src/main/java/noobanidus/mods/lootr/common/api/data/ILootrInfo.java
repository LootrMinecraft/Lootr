package noobanidus.mods.lootr.common.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.LootrAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;
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

  String getInfoKey ();

  static String generateInfoKey (UUID id) {
    String idString = id.toString();
    return "lootr/" + idString.charAt(0) + "/" + idString.substring(0, 2) + "/" + idString;
  }

  // The container has been trapped at some point in time and has at least one inventory contained (unless inventories have been cleared).
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

  Codec<ILootrInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
          LootrBlockType.CODEC.fieldOf("blockType").forGetter(ILootrInfo::getInfoBlockType),
          LootrInfoType.CODEC.fieldOf("type").forGetter(ILootrInfo::getInfoType),
          UUIDUtil.CODEC.fieldOf("uuid").forGetter(ILootrInfo::getInfoUUID),
          Codec.STRING.fieldOf("key").forGetter(ILootrInfo::getInfoKey),
          BlockPos.CODEC.fieldOf("position").forGetter(ILootrInfo::getInfoPos),
          // Optional display name
          ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(i -> Optional.ofNullable(i.getInfoDisplayName())),
          ResourceLocation.CODEC.xmap(loc -> ResourceKey.create(Registries.DIMENSION, loc), ResourceKey::location).fieldOf("dimension").forGetter(ILootrInfo::getInfoDimension),
          Codec.INT.fieldOf("size").forGetter(ILootrInfo::getInfoContainerSize),
          ItemStack.OPTIONAL_CODEC.listOf().xmap(list -> NonNullList.of(ItemStack.EMPTY, list.toArray(new ItemStack[0])), list -> list).optionalFieldOf("reference").forGetter(info -> info.isInfoReferenceInventory() ? Optional.ofNullable(info.getInfoReferenceInventory()) : Optional.empty()),
          // Optional loot table and seed
          ResourceLocation.CODEC.xmap(loc -> ResourceKey.create(Registries.LOOT_TABLE, loc), ResourceKey::location).optionalFieldOf("table").forGetter(i -> Optional.ofNullable(i.getInfoLootTable())),
          Codec.LONG.optionalFieldOf("seed").forGetter(info ->
                  info.getInfoLootTable() != null ? Optional.of(info.getInfoLootSeed()) : Optional.empty()
          )
  ).apply(instance, BaseLootrInfo::new));

  enum LootrInfoType implements StringRepresentable {
    CONTAINER_BLOCK_ENTITY,
    CONTAINER_ENTITY;

    @Override
    public String getSerializedName() {
      return name().toLowerCase(Locale.ROOT);
    }

    public static final Codec<LootrInfoType> CODEC = StringRepresentable.fromEnum(LootrInfoType::values);
  }
}
