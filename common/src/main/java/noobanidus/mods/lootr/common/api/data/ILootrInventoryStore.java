package noobanidus.mods.lootr.common.api.data;

import net.minecraft.server.level.ServerPlayer;
import noobanidus.mods.lootr.common.api.filler.ILootFiller;
import noobanidus.mods.lootr.common.api.interfaces.IMarkChanged;
import noobanidus.mods.lootr.common.api.interfaces.IHasOpeners;
import noobanidus.mods.lootr.common.api.interfaces.inventory.ILootrInventory;

import java.util.UUID;

// Stores inventories (ILootrInventory) for a specific container
// Also stores ticking data and anything else that's needed
public interface ILootrInventoryStore extends IHasOpeners, IMarkChanged {

  boolean isRefreshing();

  boolean isDecaying();

  boolean isDecayed ();

  boolean isRefreshed ();

  void beginRefresh ();

  void beginDecay ();

  int remainingDecayTime ();

  int remainingRefreshTime ();

  void update(ILootrData info);

  void performRefresh();

  default boolean clearInventories(ServerPlayer player) {
    return clearInventories(player.getUUID());
  }

  boolean clearInventories(UUID id);

  ILootrData getData ();

  default ILootrInventory getInventory(ServerPlayer player) {
    return getInventory(player.getUUID());
  }

  default ILootrInventory getOrCreateInventory(ILootrContainerInstance instance, ServerPlayer player, ILootFiller filler) {
    if (instance.canPlayerOpen(player)) {
      ILootrInventory result = getInventory(player);
      if (result != null) {
        return result;
      }

      return createInventory(instance, player, filler);
    } else {
      instance.informPlayerCannotOpen(player);
      return null;
    }
  }

  ILootrInventory getInventory(UUID id);

  ILootrInventory createInventory(ILootrContainerInstance instance, ServerPlayer player, ILootFiller filler);
}
