package noobanidus.mods.lootr.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

@FunctionalInterface
public interface LootFiller {
  void fillWithLoot(EntityPlayer player, IInventory inventory, ResourceLocation table, long seed);
}
