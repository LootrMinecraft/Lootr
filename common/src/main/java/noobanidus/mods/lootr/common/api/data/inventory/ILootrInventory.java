package noobanidus.mods.lootr.common.api.data.inventory;

import com.mojang.serialization.Codec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import noobanidus.mods.lootr.common.api.data.MenuBuilder;
import noobanidus.mods.lootr.common.api.data.ILootrInfo;
import noobanidus.mods.lootr.common.api.data.ILootrContainerData;
import noobanidus.mods.lootr.common.data.LootrInventory;

/**
 * This interface represents an actual "inventory" specific to
 * a player. It is provided by `LootrAPI::getInventory`.
 */
public interface ILootrInventory extends Container, MenuProvider {
  Codec<ILootrInventory> CODEC = ItemStack.OPTIONAL_CODEC.listOf().xmap(data -> new LootrInventory(NonNullList.of(ItemStack.EMPTY, data.toArray(new ItemStack[0]))), ILootrInventory::getInventoryContents);

  ILootrInfo getInfo();

  void setInfo(ILootrContainerData info);

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
}
