package noobanidus.mods.lootr.api.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

import java.util.Set;
import java.util.UUID;

public interface ILootTile {
  void fillWithLoot(PlayerEntity player, IInventory inventory, ResourceLocation table, long seed);

  ResourceLocation getTable();

  Set<UUID> getOpeners();

  UUID getTileId();

  void updatePacketViaState();
}
