package noobanidus.mods.lootr.api.blockentity;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;
import java.util.UUID;

public interface ILootTile {
  void unpackLootTable(Player player, Container inventory, ResourceLocation table, long seed);

  ResourceLocation getTable();

  long getSeed();

  Set<UUID> getOpeners();

  UUID getTileId();

  void updatePacketViaState();

  void setOpened (boolean opened);
}
