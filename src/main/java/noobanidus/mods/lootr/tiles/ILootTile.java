package noobanidus.mods.lootr.tiles;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;

public interface ILootTile {
  boolean isSpecialLootChest();

  void fillWithLoot(PlayerEntity player, IInventory inventory);

  void markForSync();
}
