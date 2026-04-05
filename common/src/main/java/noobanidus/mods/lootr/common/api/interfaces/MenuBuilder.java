package noobanidus.mods.lootr.common.api.interfaces;


import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * This (optional) functional interface is used by `LootrInventory` to create
 * menus. If not provided, generic chest menus will be used, guessed from the
 * size of the container itself, as found in `LootrInventory::createMenu`.
 */
@FunctionalInterface
public interface MenuBuilder {
  AbstractContainerMenu build(int id, Inventory inventory, Container container, int rows);
}
