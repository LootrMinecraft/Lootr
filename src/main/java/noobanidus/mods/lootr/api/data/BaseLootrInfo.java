package noobanidus.mods.lootr.api.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record BaseLootrInfo(LootrInfoType type, UUID uuid, BlockPos pos, Component name, ResourceKey<Level> dimension,
                            int containerSize, NonNullList<ItemStack> customInventory) implements ILootrInfo {
  public static BaseLootrInfo copy(ILootrInfo info) {
    return new BaseLootrInfo(info.getInfoType(), info.getInfoUUID(), info.getInfoPos(), info.getInfoDisplayName(), info.getInfoDimension(), info.getInfoContainerSize(), info.getInfoReferenceInventory());
  }

  @Override
  public LootrInfoType getInfoType() {
    return type();
  }

  @Override
  public @NotNull UUID getInfoUUID() {
    return uuid();
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
    return customInventory() == null || customInventory().isEmpty();
  }
}
