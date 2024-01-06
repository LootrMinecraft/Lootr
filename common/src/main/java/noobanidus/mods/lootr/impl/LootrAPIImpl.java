package noobanidus.mods.lootr.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import noobanidus.mods.lootr.api.ILootrAPI;
import noobanidus.mods.lootr.api.LootFiller;
import noobanidus.mods.lootr.api.MenuBuilder;
import noobanidus.mods.lootr.config.LootrModConfig;
import noobanidus.mods.lootr.data.DataStorage;
import noobanidus.mods.lootr.data.SpecialChestInventory;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class LootrAPIImpl implements ILootrAPI {
    @Override
    public boolean isFakePlayer(Player player) {
        if (player instanceof ServerPlayer splayer) {
            return splayer.connection == null || splayer.getClass() != ServerPlayer.class;
        }
        return false;
    }

    @Override
    public boolean clearPlayerLoot(UUID id) {
        return DataStorage.clearInventories(id);
    }

    @Override
    public @Nullable MenuProvider getModdedMenu(Level world, UUID id, BlockPos pos, ServerPlayer player, BaseContainerBlockEntity blockEntity, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier) {
        return DataStorage.getInventory(world, id, pos, player, blockEntity, filler, tableSupplier, seedSupplier);
    }

    public @Nullable  MenuProvider getModdedMenu(Level world, UUID id, BlockPos pos, ServerPlayer player, BaseContainerBlockEntity blockEntity, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier, MenuBuilder builder) {
        SpecialChestInventory inventory = DataStorage.getInventory(world, id, pos, player, blockEntity, filler, tableSupplier, seedSupplier);
        if (inventory != null) {
            inventory.setMenuBuilder(builder);
        }
        return inventory;
    }

    @Override
    public @Nullable MenuProvider getModdedMenu(Level world, UUID id, BlockPos pos, ServerPlayer player, IntSupplier sizeSupplier, Supplier<Component> displaySupplier, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier) {
        return DataStorage.getInventory(world, id, pos, player, sizeSupplier, displaySupplier, filler, tableSupplier, seedSupplier);
    }

    @Override
    public @Nullable MenuProvider getModdedMenu(Level world, UUID id, BlockPos pos, ServerPlayer player, IntSupplier sizeSupplier, Supplier<Component> displaySupplier, LootFiller filler, Supplier<ResourceLocation> tableSupplier, LongSupplier seedSupplier, MenuBuilder builder) {
        SpecialChestInventory inventory = DataStorage.getInventory(world, id, pos, player, sizeSupplier, displaySupplier, filler, tableSupplier, seedSupplier);
        if (inventory != null) {
            inventory.setMenuBuilder(builder);
        }
        return inventory;
    }

    @Override
    public long getLootSeed(long seed) {
        if (LootrModConfig.get().seed.randomize_seed || seed == -1) {
            return ThreadLocalRandom.current().nextLong();
        }
        return seed;
    }
}
