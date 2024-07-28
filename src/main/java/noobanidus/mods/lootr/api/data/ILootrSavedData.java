package noobanidus.mods.lootr.api.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.api.IMarkChanged;
import noobanidus.mods.lootr.api.IOpeners;
import noobanidus.mods.lootr.api.IRedirect;
import noobanidus.mods.lootr.api.data.inventory.ILootrInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

// There is considerable overlap between this and ILootrInfoProvider
// The info providers are always *instances* of something (even if it
// is a custom class), whereas the SavedData never has an associated
// level, block entity, etc.
public interface ILootrSavedData extends IRedirect<ILootrInfo>, ILootrInfo, IOpeners, IMarkChanged {

  boolean shouldUpdate ();

  void update (ILootrInfo info);

  void refresh();

  boolean hasBeenOpened ();

  default boolean clearInventories (ServerPlayer player) {
    return clearInventories(player.getUUID());
  }

  boolean clearInventories (UUID id);

  default ILootrInventory getInventory(ServerPlayer player) {
    return getInventory(player.getUUID());
  }

  default ILootrInventory getOrCreateInventory (ILootrInfoProvider provider, ServerPlayer player, LootFiller filler) {
    ILootrInventory result = getInventory(player);
    if (result != null) {
      return result;
    }

    return createInventory(provider, player, filler);
  }

  ILootrInventory getInventory(UUID id);

  ILootrInventory createInventory(ILootrInfoProvider provider, ServerPlayer player, LootFiller filler);

  @Override
  default LootrInfoType getInfoType() {
    return getRedirect().getInfoType();
  }

  @Override
  default LootrBlockType getInfoBlockType () {
    return getRedirect().getInfoBlockType();
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

  @Override
  default ResourceKey<LootTable> getInfoLootTable() {
    return getRedirect().getInfoLootTable();
  }

  @Override
  default long getInfoLootSeed() {
    return getRedirect().getInfoLootSeed();
  }
}
