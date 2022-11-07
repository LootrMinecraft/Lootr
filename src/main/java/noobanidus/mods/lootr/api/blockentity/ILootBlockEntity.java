package noobanidus.mods.lootr.api.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.resources.ResourceLocation;
import noobanidus.mods.lootr.api.IHasOpeners;

import java.util.Set;
import java.util.UUID;

public interface ILootBlockEntity extends IHasOpeners {
  void unpackLootTable(Player player, Container inventory, ResourceLocation table, long seed);

  ResourceLocation getTable();

  BlockPos getPosition ();

  long getSeed();

  UUID getTileId();

  void updatePacketViaState();

  void setOpened (boolean opened);
}
