package net.zestyblaze.lootr.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface LootFiller {
    void fillWithLoot(PlayerEntity player, Inventory inventory, Identifier table, long seed);
}
