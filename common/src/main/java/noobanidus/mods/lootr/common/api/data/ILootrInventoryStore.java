package noobanidus.mods.lootr.common.api.data;

import net.minecraft.server.level.ServerPlayer;
import noobanidus.mods.lootr.common.api.filler.ILootFiller;
import noobanidus.mods.lootr.common.api.interfaces.IMarkChanged;
import noobanidus.mods.lootr.common.api.interfaces.IHasOpeners;
import noobanidus.mods.lootr.common.api.inventory.ILootrInventory;

import java.util.UUID;

public interface ILootrInventoryStore extends IHasOpeners, IMarkChanged {

  void update(ILootrData info);

  void refresh();

  default boolean clearInventories(ServerPlayer player) {
    return clearInventories(player.getUUID());
  }

  boolean clearInventories(UUID id);

  ILootrData getData ();

  default ILootrInventory getInventory(ServerPlayer player) {
    return getInventory(player.getUUID());
  }

  default ILootrInventory getOrCreateInventory(ILootrContainerInstance provider, ServerPlayer player, ILootFiller filler) {
    if (provider.canPlayerOpen(player)) {
      ILootrInventory result = getInventory(player);
      if (result != null) {
        return result;
      }

      return createInventory(provider, player, filler);
    } else {
      provider.informPlayerCannotOpen(player);
      return null;
    }
  }

  ILootrInventory getInventory(UUID id);

  ILootrInventory createInventory(ILootrContainerInstance provider, ServerPlayer player, ILootFiller filler);

/*  @Override
  default @NonNull ILootrType getInfoType() {
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
  default int getInfoKey () {
    return getRedirect().getInfoKey();
  }

  @Override
  default Identifier getInfoIdentifier() {
    return getRedirect().getInfoIdentifier();
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
  }*/
}
