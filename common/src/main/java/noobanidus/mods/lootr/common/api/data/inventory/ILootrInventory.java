package noobanidus.mods.lootr.common.api.data.inventory;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import noobanidus.mods.lootr.common.api.MenuBuilder;
import noobanidus.mods.lootr.common.api.data.ILootrInfo;
import noobanidus.mods.lootr.common.api.data.ILootrSavedData;

/**
 * This interface represents an actual "inventory" specific to
 * a player. It is provided by `LootrAPI::getInventory`.
 */
public interface ILootrInventory extends Container, MenuProvider {
  ILootrInfo getInfo();

  void setInfo(ILootrSavedData info);

  @Override
  default Component getDisplayName() {
    Component name = getInfo().getInfoDisplayName();
    if (name == null) {
      return Component.empty();
    }
    return name;
  }

  NonNullList<ItemStack> getInventoryContents();

  void setMenuBuilder(MenuBuilder builder);

  CompoundTag saveToTag(HolderLookup.Provider provider);
}
