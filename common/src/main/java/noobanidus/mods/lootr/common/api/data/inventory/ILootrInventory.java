package noobanidus.mods.lootr.common.api.data.inventory;

import com.mojang.serialization.Codec;
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
import noobanidus.mods.lootr.common.data.LootrInventory;

public interface ILootrInventory extends Container, MenuProvider {
  Codec<ILootrInventory> CODEC = ItemStack.OPTIONAL_CODEC.listOf().xmap(data -> new LootrInventory(NonNullList.of(ItemStack.EMPTY, data.toArray(new ItemStack[0]))), ILootrInventory::getInventoryContents);

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
