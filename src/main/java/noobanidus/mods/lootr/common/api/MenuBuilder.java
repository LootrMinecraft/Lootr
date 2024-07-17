package noobanidus.mods.lootr.common.api;


import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

@FunctionalInterface
public interface MenuBuilder {
  AbstractContainerMenu build(int id, Inventory inventory, Container container, int rows);
}
