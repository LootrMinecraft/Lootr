package noobanidus.mods.lootr.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

@FunctionalInterface
public interface LootFiller {
  void fillWithLoot(PlayerEntity player, IInventory inventory, ResourceLocation table, long seed);
}
