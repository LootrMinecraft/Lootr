package noobanidus.mods.lootr.api.data.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.api.data.LootrBlockType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public record AbstractMinecartContainerLootrInfoProvider(
    AbstractMinecartContainer minecart) implements ILootrCart {

  @Override
  public LootrBlockType getInfoBlockType() {
    return LootrBlockType.ENTITY;
  }

  @Override
  public LootrInfoType getInfoType() {
    return LootrInfoType.CONTAINER_ENTITY;
  }

  @Override
  public @NotNull Vec3 getInfoVec() {
    return minecart.position();
  }

  @Override
  public @NotNull UUID getInfoUUID() {
    return minecart.getUUID();
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
    return minecart.blockPosition();
  }

  @Override
  public ResourceKey<LootTable> getInfoLootTable() {
    return minecart.getLootTable();
  }

  @Override
  public @Nullable Component getInfoDisplayName() {
    return minecart.getName();
  }

  @Override
  public @NotNull ResourceKey<Level> getInfoDimension() {
    return minecart.level().dimension();
  }

  @Override
  public int getInfoContainerSize() {
    return minecart.getContainerSize();
  }

  // Minecarts cannot have custom inventories.
  @Override
  public @Nullable NonNullList<ItemStack> getInfoReferenceInventory() {
    return null;
  }

  @Override
  public boolean isInfoReferenceInventory() {
    return false;
  }

  @Override
  public long getInfoLootSeed() {
    return minecart.getLootTableSeed();
  }

  @Override
  public Level getInfoLevel() {
    return minecart.level();
  }

  @Override
  public Container getInfoContainer() {
    return minecart;
  }

  @Override
  public void markChanged() {
    minecart.setChanged();
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
