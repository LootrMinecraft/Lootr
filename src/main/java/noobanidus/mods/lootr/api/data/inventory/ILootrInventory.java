package noobanidus.mods.lootr.api.data.inventory;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import noobanidus.mods.lootr.api.MenuBuilder;
import noobanidus.mods.lootr.api.data.ILootrInfo;
import noobanidus.mods.lootr.api.data.ILootrSavedData;

public interface ILootrInventory extends Container, MenuProvider {
  ILootrInfo getInfo();

  void setInfo(ILootrSavedData info);

  @Override
  default Component getDisplayName() {
    return getInfo().getInfoDisplayName();
  }

  NonNullList<ItemStack> getInventoryContents();

  void setMenuBuilder(MenuBuilder builder);

  CompoundTag saveToTag(HolderLookup.Provider provider);
}
