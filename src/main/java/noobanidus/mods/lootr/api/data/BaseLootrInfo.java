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

import java.util.UUID;

public record BaseLootrInfo(LootrBlockType blockType, LootrInfoType infoType, UUID uuid, BlockPos pos, Component name, ResourceKey<Level> dimension,
                            int containerSize, NonNullList<ItemStack> customInventory, ResourceKey<LootTable> table, long seed) implements ILootrInfo {
  public static BaseLootrInfo copy(ILootrInfo info) {
    return new BaseLootrInfo(info.getInfoBlockType(), info.getInfoType(), info.getInfoUUID(), info.getInfoPos(), info.getInfoDisplayName(), info.getInfoDimension(), info.getInfoContainerSize(), info.getInfoReferenceInventory(), info.getInfoLootTable(), info.getInfoLootSeed());
  }

  @Override
  public LootrBlockType getInfoBlockType() {
    return blockType();
  }

  @Override
  public LootrInfoType getInfoType() {
    return infoType();
  }

  @Override
  public @NotNull UUID getInfoUUID() {
    return uuid();
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
  public @Nullable Component getInfoDisplayName() {
    return name();
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
}
