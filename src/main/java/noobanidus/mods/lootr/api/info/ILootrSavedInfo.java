package noobanidus.mods.lootr.api.info;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.api.LootFiller;
import noobanidus.mods.lootr.api.inventory.ILootrInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

// There is considerable overlap between this and ILootrInfoProvider
// The info providers are always *instances* of something (even if it
// is a custom class), whereas the SavedData never has an associated
// level, block entity, etc.
public interface ILootrSavedInfo extends IRedirect<ILootrInfo>, ILootrInfo {
  void markChanged();

  void clearInventories();

  default ILootrInventory getInventory(Player player) {
    return getInventory(player.getUUID());
  }

  ILootrInventory getInventory(UUID id);

  ILootrInventory createInventory(ILootrInfoProvider provider, ServerPlayer player, LootFiller filler);

  @Override
  default LootrInfoType getInfoType() {
    return getRedirect().getInfoType();
  }

  @Override
  default @NotNull Vec3 getInfoVec() {
    return getRedirect().getInfoVec();
  }

  @Override
  default @NotNull UUID getInfoUUID() {
    return getRedirect().getInfoUUID();
  }

  @Override
  default String getInfoKey() {
    return getRedirect().getInfoKey();
  }

  @Override
  default @NotNull BlockPos getInfoPos() {
    return getRedirect().getInfoPos();
  }

  @Override
  default @Nullable Component getInfoDisplayName() {
    return getRedirect().getInfoDisplayName();
  }

  @Override
  default @NotNull ResourceKey<Level> getInfoDimension() {
    return getRedirect().getInfoDimension();
  }

  @Override
  default int getInfoContainerSize() {
    return getRedirect().getInfoContainerSize();
  }

  @Override
  default @Nullable Level getInfoLevel() {
    return getRedirect().getInfoLevel();
  }

  @Override
  default @Nullable Container getInfoContainer() {
    return getRedirect().getInfoContainer();
  }

  @Override
  default @Nullable NonNullList<ItemStack> getInfoReferenceInventory() {
    return getRedirect().getInfoReferenceInventory();
  }

  @Override
  default boolean isInfoReferenceInventory() {
    return getRedirect().isInfoReferenceInventory();
  }
}
