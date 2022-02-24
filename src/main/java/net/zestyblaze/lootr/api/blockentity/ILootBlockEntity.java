package net.zestyblaze.lootr.api.blockentity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Set;
import java.util.UUID;

public interface ILootBlockEntity {
    void unpackLootTable(PlayerEntity player, Inventory inventory, Identifier table, long seed);

    Identifier getTable();

    BlockPos getPosition();

    long getSeed();

    Set<UUID> getOpeners();

    UUID getTileID();

    void updatePacketViaState();

    void setOpened (boolean opened);
}
