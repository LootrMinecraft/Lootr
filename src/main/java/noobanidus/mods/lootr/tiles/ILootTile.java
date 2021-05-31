package noobanidus.mods.lootr.tiles;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ILootTile {
  void fillWithLoot(PlayerEntity player, IInventory inventory);

  void setTable (ResourceLocation table);
  void setSeed (long seed);

  Set<UUID> getOpeners ();
  UUID getTileId ();

  @FunctionalInterface
  interface LootFiller {
    void fillWithLoot(PlayerEntity player, IInventory inventory);
  }
}
