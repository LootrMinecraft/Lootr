package net.zestyblaze.lootr.api.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.zestyblaze.lootr.api.IHasOpeners;

import java.util.UUID;

public interface ILootBlockEntity extends IHasOpeners {
  void unpackLootTable(Player player, Container inventory, ResourceLocation table, long seed);

  ResourceLocation getTable();

  BlockPos getPosition();

  long getSeed();

  UUID getTileId();

  void updatePacketViaState();

  void setOpened(boolean opened);
}
