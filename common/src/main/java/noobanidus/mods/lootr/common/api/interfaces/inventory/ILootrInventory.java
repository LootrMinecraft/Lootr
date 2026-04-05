package noobanidus.mods.lootr.common.api.interfaces.inventory;

import com.mojang.serialization.Codec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import noobanidus.mods.lootr.common.api.data.ILootrData;
import noobanidus.mods.lootr.common.api.interfaces.container.IMenuBuilder;
import noobanidus.mods.lootr.common.api.data.ILootrInventoryStore;
import noobanidus.mods.lootr.common.data.LootrInventory;

/**
 * This interface represents an actual "inventory" specific to
 * a player. It is provided by `LootrAPI::getInventory`.
 */
public interface ILootrInventory extends Container, MenuProvider {
  Codec<ILootrInventory> CODEC = ItemStack.OPTIONAL_CODEC.listOf().xmap(data -> new LootrInventory(NonNullList.of(ItemStack.EMPTY, data.toArray(new ItemStack[0]))), ILootrInventory::getInventoryContents);

  default ILootrData getData () {
    return getInventoryStore().getData();
  }

  ILootrInventoryStore getInventoryStore();

  void setInventoryStore(ILootrInventoryStore containerStore);

  @Override
  default Component getDisplayName() {
    Component name = getData().getDataDisplayName();
    if (name == null) {
      return Component.empty();
    }
    return name;
  }

  NonNullList<ItemStack> getInventoryContents();

  void setMenuBuilder(IMenuBuilder builder);

  Container getContainer (Level level);
}
